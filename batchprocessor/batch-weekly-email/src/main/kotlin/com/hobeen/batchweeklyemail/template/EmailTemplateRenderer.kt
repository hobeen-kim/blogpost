package com.hobeen.batchweeklyemail.template

import com.hobeen.batchweeklyemail.dto.RecommendedPost
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class EmailTemplateRenderer(
    @Value("\${site.url}") private val siteUrl: String,
) {
    private val template: String by lazy {
        ClassPathResource("templates/weekly-email.html").inputStream.bufferedReader().readText()
    }

    fun render(posts: List<RecommendedPost>): String {
        val postsHtml = posts.joinToString("\n") { post ->
            val thumbnailHtml = if (!post.thumbnail.isNullOrBlank()) {
                """<img src="${post.thumbnail}" alt="${escapeHtml(post.title)}" style="width:100%;max-height:200px;object-fit:cover;border-radius:8px 8px 0 0;" />"""
            } else {
                """<div style="width:100%;height:120px;background:#f0ecf9;border-radius:8px 8px 0 0;display:flex;align-items:center;justify-content:center;">
                    <span style="color:#7c3aed;font-size:24px;font-weight:bold;">DevelopTag</span>
                </div>"""
            }

            val descriptionHtml = if (!post.description.isNullOrBlank()) {
                val desc = if (post.description.length > 120) post.description.take(120) + "..." else post.description
                """<p style="margin:0 0 12px 0;color:#6b7280;font-size:14px;line-height:1.5;">${escapeHtml(desc)}</p>"""
            } else ""

            val tagsHtml = post.tags.take(5).joinToString(" ") { tag ->
                """<span style="display:inline-block;background:#f0ecf9;color:#7c3aed;font-size:12px;padding:2px 8px;border-radius:12px;margin-bottom:4px;">${escapeHtml(tag)}</span>"""
            }

            """
            <div style="background:#ffffff;border-radius:8px;margin-bottom:16px;border:1px solid #e5e7eb;overflow:hidden;">
                $thumbnailHtml
                <div style="padding:16px;">
                    <a href="${post.url}" style="text-decoration:none;color:#111827;font-size:16px;font-weight:600;line-height:1.4;display:block;margin-bottom:8px;">${escapeHtml(post.title)}</a>
                    <span style="display:inline-block;background:#7c3aed;color:#ffffff;font-size:11px;padding:2px 8px;border-radius:4px;margin-bottom:8px;">${escapeHtml(post.source)}</span>
                    $descriptionHtml
                    <div style="margin-top:4px;">$tagsHtml</div>
                </div>
            </div>
            """.trimIndent()
        }

        return template
            .replace("{{POSTS}}", postsHtml)
            .replace("{{SITE_URL}}", siteUrl)
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}
