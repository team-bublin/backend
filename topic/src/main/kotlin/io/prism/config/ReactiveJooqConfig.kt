package io.prism.config

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ReactiveJooqConfig {

    @Bean
    fun dslContext() : DSLContext {
        val connectionFactory = ConnectionFactories.get(
            ConnectionFactoryOptions
                .parse("r2dbc:h2:mem:///test")
                .mutate()
                .option(ConnectionFactoryOptions.USER, "sa")
                .option(ConnectionFactoryOptions.PASSWORD, "")
                .build()
        )

        return DSL.using(connectionFactory)
    }

}