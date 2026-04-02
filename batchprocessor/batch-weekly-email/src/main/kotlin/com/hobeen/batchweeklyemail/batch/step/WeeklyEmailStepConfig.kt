package com.hobeen.batchweeklyemail.batch.step

import com.hobeen.batchweeklyemail.dto.EmailRecommendation
import com.hobeen.batchweeklyemail.dto.RecommendedPost
import com.hobeen.batchweeklyemail.dto.UserEmailTarget
import com.hobeen.batchweeklyemail.template.EmailTemplateRenderer
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.PlatformTransactionManager
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.SendEmailRequest
import software.amazon.awssdk.services.ses.model.Body
import software.amazon.awssdk.services.ses.model.Content
import software.amazon.awssdk.services.ses.model.Destination
import software.amazon.awssdk.services.ses.model.Message
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.sql.DataSource

@Configuration
class WeeklyEmailStepConfig(
    @Qualifier("postDataSource") private val postDataSource: DataSource,
    private val sesClient: SesClient,
    private val emailTemplateRenderer: EmailTemplateRenderer,
    @Value("\${aws.ses.from}") private val fromEmail: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val jdbcTemplate by lazy { JdbcTemplate(postDataSource) }

    @Bean
    fun weeklyEmailStep(
        jobRepository: JobRepository,
        @Qualifier("postTransactionManager") transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("weeklyEmailStep", jobRepository)
            .chunk<UserEmailTarget, EmailRecommendation>(1, transactionManager)
            .reader(weeklyEmailReader(null))
            .processor(weeklyEmailProcessor())
            .writer(weeklyEmailWriter())
            .faultTolerant()
            .skip(Exception::class.java)
            .skipLimit(100)
            .build()
    }

    @Bean
    @StepScope
    fun weeklyEmailReader(
        @Value("#{jobParameters['target']}") target: String?
    ): JdbcCursorItemReader<UserEmailTarget> {
        val targetEmails = target?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

        val sql = if (targetEmails.isEmpty()) {
            """
                SELECT up.user_id, au.email
                FROM user_preference up
                JOIN auth.users au ON up.user_id::uuid = au.id
                WHERE up.email_subscription = true
            """.trimIndent()
        } else {
            val inClause = targetEmails.joinToString(",") { "'${it.replace("'", "''")}'" }
            """
                SELECT au.id::text as user_id, au.email
                FROM auth.users au
                WHERE au.email IN ($inClause)
            """.trimIndent()
        }

        return JdbcCursorItemReaderBuilder<UserEmailTarget>()
            .name("weeklyEmailReader")
            .dataSource(postDataSource)
            .sql(sql)
            .rowMapper { rs, _ ->
                UserEmailTarget(
                    userId = rs.getString("user_id"),
                    email = rs.getString("email"),
                )
            }
            .build()
    }

    @Bean
    fun weeklyEmailProcessor(): ItemProcessor<UserEmailTarget, EmailRecommendation> {
        return ItemProcessor { user ->
            try {
                log.info("Processing user [{}] {}", user.userId, user.email)

                // 1. Get user behavior data
                val views = jdbcTemplate.queryForList(
                    "SELECT pv.post_id, pv.viewed_at FROM post_view pv WHERE pv.user_id = ? ORDER BY pv.viewed_at DESC LIMIT 15",
                    user.userId
                )
                val likes = jdbcTemplate.queryForList(
                    "SELECT l.post_id, l.created_at FROM \"like\" l WHERE l.user_id = ? ORDER BY l.created_at DESC LIMIT 15",
                    user.userId
                )
                val bookmarks = jdbcTemplate.queryForList(
                    "SELECT b.post_id, b.created_at FROM bookmark b JOIN bookmark_group bg ON b.bookmark_group_id = bg.bookmark_group_id WHERE bg.user_id = ? ORDER BY b.created_at DESC LIMIT 15",
                    user.userId
                )

                data class BehaviorEntry(val postId: Long, val behaviorScore: Int, val date: LocalDateTime)

                val behaviors = mutableListOf<BehaviorEntry>()
                views.forEach { row ->
                    behaviors.add(BehaviorEntry(row["post_id"] as Long, 1, (row["viewed_at"] as java.sql.Timestamp).toLocalDateTime()))
                }
                likes.forEach { row ->
                    behaviors.add(BehaviorEntry(row["post_id"] as Long, 2, (row["created_at"] as java.sql.Timestamp).toLocalDateTime()))
                }
                bookmarks.forEach { row ->
                    behaviors.add(BehaviorEntry(row["post_id"] as Long, 3, (row["created_at"] as java.sql.Timestamp).toLocalDateTime()))
                }

                // 2. Get tags for behavior posts
                val behaviorPostIds = behaviors.map { it.postId }.distinct()
                val postTagsMap = mutableMapOf<Long, List<Pair<String, Int?>>>()
                if (behaviorPostIds.isNotEmpty()) {
                    val inClause = behaviorPostIds.joinToString(",")
                    val tagRows = jdbcTemplate.queryForList(
                        "SELECT pt.post_id, t.name, pt.tag_level FROM post_tag pt JOIN tag t ON pt.tag_id = t.tag_id WHERE pt.post_id IN ($inClause)"
                    )
                    tagRows.groupBy { it["post_id"] as Long }.forEach { (postId, rows) ->
                        postTagsMap[postId] = rows.map { (it["name"] as String) to (it["tag_level"] as Int?) }
                    }
                }

                // 3. Calculate tag scores
                val now = LocalDateTime.now()
                val tagScores = mutableMapOf<String, Double>()
                for (b in behaviors) {
                    val daysSince = ChronoUnit.DAYS.between(b.date, now)
                    val timeWeight = when {
                        daysSince <= 7 -> 1.0
                        daysSince <= 14 -> 0.8
                        daysSince <= 21 -> 0.6
                        daysSince <= 28 -> 0.4
                        else -> 0.2
                    }
                    val tags = postTagsMap[b.postId] ?: emptyList()
                    for ((tagName, tagLevel) in tags) {
                        val tagLevelWeight = when (tagLevel) {
                            1 -> 1.0
                            2 -> 2.0
                            3 -> 3.0
                            else -> 0.7
                        }
                        tagScores[tagName] = (tagScores[tagName] ?: 0.0) + b.behaviorScore * timeWeight * tagLevelWeight
                    }
                }

                // 4. Get candidate posts (last 7 days based on send date)
                val today = LocalDate.now()
                val periodEnd = today.minusDays(1).atTime(23, 59, 59)  // 어제까지
                val periodStart = today.minusDays(7).atStartOfDay()     // 7일 전부터

                val candidates = jdbcTemplate.queryForList(
                    "SELECT p.post_id, p.title, p.source, p.url, p.description, p.thumbnail FROM post p WHERE p.pub_date BETWEEN ? AND ?",
                    periodStart, periodEnd
                )

                // 5. Score each candidate
                data class ScoredPost(val postId: Long, val title: String, val source: String, val url: String, val description: String?, val thumbnail: String?, val score: Double)

                val scoredPosts = candidates.map { row ->
                    val postId = row["post_id"] as Long
                    val candidateTags = jdbcTemplate.queryForList(
                        "SELECT t.name FROM post_tag pt JOIN tag t ON pt.tag_id = t.tag_id WHERE pt.post_id = ?",
                        String::class.java,
                        postId
                    )
                    val postScore = candidateTags.sumOf { tagScores[it] ?: 0.0 }
                    ScoredPost(
                        postId = postId,
                        title = row["title"] as String,
                        source = row["source"] as String,
                        url = row["url"] as String,
                        description = row["description"] as String?,
                        thumbnail = row["thumbnail"] as String?,
                        score = postScore
                    )
                }

                // 6. Select top 5
                val hasBehavior = behaviors.isNotEmpty()
                val selected = mutableListOf<ScoredPost>()

                if (hasBehavior) {
                    val scored = scoredPosts.filter { it.score > 0 }.sortedByDescending { it.score }.take(5)
                    selected.addAll(scored)

                    if (selected.size < 5) {
                        val selectedIds = selected.map { it.postId }.toSet()
                        val filler = scoredPosts
                            .filter { it.postId !in selectedIds }
                            .sortedByDescending { it.postId }
                            .take(5 - selected.size)
                        selected.addAll(filler)
                    }
                } else {
                    selected.addAll(scoredPosts.sortedByDescending { it.postId }.take(5))
                }

                // 7. Get tag names for each selected post
                val recommendedPosts = selected.map { sp ->
                    val tags = jdbcTemplate.queryForList(
                        "SELECT t.name FROM post_tag pt JOIN tag t ON pt.tag_id = t.tag_id WHERE pt.post_id = ? AND pt.tag_level IS NOT NULL",
                        String::class.java,
                        sp.postId
                    )
                    RecommendedPost(
                        postId = sp.postId,
                        title = sp.title,
                        source = sp.source,
                        url = sp.url,
                        description = sp.description,
                        thumbnail = sp.thumbnail,
                        tags = tags,
                    )
                }

                log.info("  -> {} recommended posts for user [{}]", recommendedPosts.size, user.userId)
                EmailRecommendation(
                    userEmail = user.email,
                    userId = user.userId,
                    posts = recommendedPosts,
                )
            } catch (e: Exception) {
                log.error("Failed to process user [{}]: {}", user.userId, e.message)
                throw e
            }
        }
    }

    @Bean
    fun weeklyEmailWriter(): ItemWriter<EmailRecommendation> {
        return ItemWriter { chunks ->
            for (recommendation in chunks) {
                try {
                    val html = emailTemplateRenderer.render(recommendation.posts)
                    sesClient.sendEmail(
                        SendEmailRequest.builder()
                            .source(fromEmail)
                            .destination(
                                Destination.builder()
                                    .toAddresses(recommendation.userEmail)
                                    .build()
                            )
                            .message(
                                Message.builder()
                                    .subject(
                                        Content.builder()
                                            .data("DevelopTag - 이번 주 추천 포스트")
                                            .charset("UTF-8")
                                            .build()
                                    )
                                    .body(
                                        Body.builder()
                                            .html(
                                                Content.builder()
                                                    .data(html)
                                                    .charset("UTF-8")
                                                    .build()
                                            )
                                            .build()
                                    )
                                    .build()
                            )
                            .build()
                    )
                    log.info("  Sent email to {}", recommendation.userEmail)
                } catch (e: Exception) {
                    log.error("Failed to send email to {}: {}", recommendation.userEmail, e.message)
                }
            }
        }
    }
}
