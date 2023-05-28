package io.prism.usecase

import io.prism.entity.Topic
import jooq.jooq_dsl.tables.JTopic
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Repository
class TopicRepository(private val dsl: DSLContext) {
    fun save(topic: Topic) : Mono<Int> {

        return dsl.insertInto(JTopic.TOPIC)
            .set(JTopic.TOPIC.TITLE, topic.title)
            .set(JTopic.TOPIC.TOTALAVERAGE, topic.totalAverage)
            .set(JTopic.TOPIC.SELECTIONS, topic.selections!!.toStringWithDelimiter())
            .toMono()
    }
}