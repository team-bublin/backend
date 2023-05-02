package io.prism

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TopicApplication

fun main(args:Array<String>)
{
    runApplication<TopicApplication>(*args)
}