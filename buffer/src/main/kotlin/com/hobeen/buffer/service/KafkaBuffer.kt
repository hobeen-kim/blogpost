package com.hobeen.buffer.service

import com.hobeen.buffer.CollectBufferProps
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

@Component
class KafkaBuffer(
    private val consumerFactory: ConsumerFactory<String, ByteArray>,
    private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
    private val props: CollectBufferProps = CollectBufferProps(),
): BufferExecutor {

    private val consumer: Consumer<String, ByteArray> = consumerFactory.createConsumer(props.groupId, null, null)
    private lateinit var running: AtomicBoolean
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun run(runningMark: AtomicBoolean) {

        running = runningMark

        val tps = getTopicPartitions()

        //컨슈머에 파티션 할당
        consumer.assign(tps)

        // 파티션별 커밋된 오프셋 세팅 (없으면 처음부터)
        setCommit(tps)

        // 파티션별 버퍼 큐
        val buffers = tps.associateWith { ArrayDeque<ConsumerRecord<String, ByteArray>>() }
            .toMap()

        var rrIndex = 0
        var sendCount = 0

        while (running.get()) {
            //record 가져오기 (최대 10개)
            val records = try {
                consumer.poll(Duration.ofMillis(props.pollMs))
            } catch (e: Exception) {
                if (!running.get()) break
                continue
            }

            //records 가 있으면 buffered 에 추가
            addRecordsToBuffer(records, buffers)

            // 라운드로빈으로 "딱 1개"만 전달 (엄격 RR)
            val sent = sendOneRoundRobin(tps, buffers, rrIndex)

            //하나라도 보냈다면 index 다음걸로
            if(sent.sentAny) {
                rrIndex = sent.nextIndex
                sendCount++

                //2초 휴식 및 sendCount 초기화
                if(sendCount >= 5) {
                    log.info("5개 처리 완료")
                    Thread.sleep(2000)
                    sendCount = 0
                }
            } else {
                // 읽을 것도/보낼 것도 없으면 잠깐 쉼
                Thread.sleep(props.idleSleepMs)
            }
        }

        try {
            consumer.close()
        } catch (_: Exception) {}
    }

    private fun getTopicPartitions(): List<TopicPartition> {
        // A 토픽 파티션 목록 조회 후 assign
        val partitionInfos = consumer.partitionsFor(props.sourceTopic)
        require(partitionInfos.isNotEmpty()) { "No partitions found for topic=${props.sourceTopic}" }

        val tps = partitionInfos
            .map { TopicPartition(props.sourceTopic, it.partition()) }
            .sortedBy { it.partition() }
        return tps
    }

    override fun beforeStop() {
        consumer.wakeup()
    }

    private data class SendResult(val sentAny: Boolean, val nextIndex: Int)

    private fun sendOneRoundRobin(
        tps: List<TopicPartition>,
        buffers: Map<TopicPartition, ArrayDeque<ConsumerRecord<String, ByteArray>>>,
        startIndex: Int,
    ): SendResult {
        var idx = startIndex
        var checked = 0

        while (checked < tps.size) {
            val tp = tps[idx]
            idx = getNextIndex(idx, tps.size)
            checked++

            val q = buffers[tp] ?: continue
            val rec = q.removeFirstOrNull() ?: continue

            // B 토픽은 파티션 1개 → 어떤 key를 쓰든 결과는 partition 0
            // (향후 B 파티션 늘어날 가능성까지 “무조건 1파티션” 유지하려면 key를 상수로 두는 걸 추천)
            val sinkKey = rec.key() // 또는 "constant-key"

            // 전송 성공 후 커밋(간단/안전: 동기 대기)
            kafkaTemplate.send(props.sinkTopic, sinkKey, rec.value()).get()

            // 성공했으니 offset  커밋
            consumer.commitSync(mapOf(tp to OffsetAndMetadata(rec.offset() + 1)))

            return SendResult(sentAny = true, nextIndex = idx)
        }

        return SendResult(sentAny = false, nextIndex = idx)
    }

    private fun setCommit(tps: List<TopicPartition>) {
        // 커밋된 오프셋이 있으면 그 위치부터, 없으면 beginning
        val committed = consumer.committed(tps.toSet())
        val noCommitted = mutableListOf<TopicPartition>()
        for (tp in tps) {
            val meta = committed[tp]
            if (meta != null) consumer.seek(tp, meta.offset())
            else noCommitted.add(tp)
        }
        if (noCommitted.isNotEmpty()) consumer.seekToBeginning(noCommitted)
    }

    private fun addRecordsToBuffer(records: ConsumerRecords<String, ByteArray>, buffers: Map<TopicPartition, ArrayDeque<ConsumerRecord<String, ByteArray>>>) {
        if (!records.isEmpty) {
            for (r in records) {
                val tp = TopicPartition(r.topic(), r.partition())
                buffers[tp]?.addLast(r)
            }
        }
    }

    private fun getNextIndex(currentIndex: Int, totalSize: Int): Int {
        return (currentIndex + 1) % totalSize
    }
}