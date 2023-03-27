package io.prism.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import java.util.*
import java.util.stream.Stream


@RestController
class AuthenticationController {


    @GetMapping("/test")
    fun hello(): Mono<Map<String, Any>> {

        val keyPairGenerator = KeyPairGenerator.getInstance("EC")
        keyPairGenerator.initialize(ECGenParameterSpec("secp256r1"))

        val keyPair = keyPairGenerator.generateKeyPair()
        val publicKey = Base64.getEncoder().encodeToString(keyPair.public.encoded)
        val privateKey = Base64.getEncoder().encodeToString(keyPair.private.encoded)
        val mapOf = mapOf("public-key" to publicKey, "private-key" to privateKey)
        return Mono.just(mapOf)
    }

    @GetMapping("/stream")
    fun stream(): Flux<Map<String, Int>> {
        val stream = Stream.iterate(0) { i -> i + 1 } // Java8의 무한 Stream
        return Flux.fromStream(stream) // Limit 제외
            .map { i -> mapOf("value" to i) }
    }
}
