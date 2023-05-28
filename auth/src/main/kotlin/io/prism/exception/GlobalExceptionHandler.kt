package io.prism.exception

import org.slf4j.LoggerFactory
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.ResolvableType
import org.springframework.core.annotation.Order
import org.springframework.core.codec.Hints
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

//@Component
//@Order(-1)
class GlobalExceptionHandler : ErrorWebExceptionHandler {
    private val log = LoggerFactory.getLogger(this::class.java)
    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val response = exchange.response
        response.headers.contentType = MediaType.APPLICATION_JSON
        log.error("{} - {}", ex.javaClass.name, ex.message)
        val errorResponse = when (ex) {
            is BusinessException       -> ErrorResponse(exception = ex)
            is ResponseStatusException -> exchange.response.statusCode?.let {
                ErrorResponse(message = ex.message, status = ex.statusCode.value())
            } ?: ErrorResponse()

            else -> ErrorResponse()
        }
        return response.writeWith(
            Jackson2JsonEncoder().encode(
                Mono.just(errorResponse),
                response.bufferFactory(),
                ResolvableType.forInstance(errorResponse),
                MediaType.APPLICATION_JSON,
                Hints.from(Hints.LOG_PREFIX_HINT, exchange.logPrefix)
            )
        )
    }
}
