package io.prism.usecase

import org.springframework.stereotype.Service

@Service
class TopicService(private val topicRepository: TopicRepository) {

    fun save(request : TopicCreateRequest) {
//        topicRepository.save();
    }
}