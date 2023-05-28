package io.prism.infrastructure.db

import org.h2.tools.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener


@Profile("h2")
@Configuration
class H2ServerConfig(
    @Value("\${webclient.h2-console-port}")
    private val port : Int
) {

    private lateinit var h2ConsoleServer : Server


    @EventListener(ContextRefreshedEvent::class)
    fun start() {
        this.h2ConsoleServer = Server.createWebServer("-webPort", port.toString()).start()
    }

    @EventListener(ContextClosedEvent::class)
    fun stop() {
        this.h2ConsoleServer.stop()
    }
}
