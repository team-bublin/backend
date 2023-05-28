import org.jooq.meta.jaxb.Property

description = "authentication module"

plugins {
    id("nu.studer.jooq") version "8.1"
    id("java")
}

repositories {
    mavenCentral()
}

buildscript {
    configurations["classpath"].resolutionStrategy.eachDependency {
        if (requested.group == "org.jooq") {
            useVersion("3.17.6")
        }
    }
}

jooq {
    version.set("3.17.6")
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(true)
            jooqConfiguration.apply {
                generator.apply {
                    database.apply {
                        name = "org.jooq.meta.extensions.jpa.JPADatabase"
                        properties = listOf(
                            Property()
                                .withKey("packages")
                                .withValue("io.prism.entity"),
                            Property()
                                .withKey("useAttributeConverters")
                                .withValue("true"),
                            Property()
                                .withKey("unqualifiedSchema")
                                .withValue("none"),
                            Property()
                                .withKey("hibernate.physical_naming_strategy")
                                .withValue("io.prism.naming.QuotedPhysicalNamingStrategy")
                        )
                    }

                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                        isJavaTimeTypes = true
                    }

                    target.apply {
                        packageName = "jooq.jooq_dsl"
                        directory = "src/generated"
                        encoding = "UTF-8"
                    }

                    strategy.name = "io.prism.generator.JPrefixGeneratorStrategy"
                }
            }
        }
    }
}

tasks.named<nu.studer.gradle.jooq.JooqGenerate>("generateJooq") { allInputsDeclared.set(true) }

dependencies {
    apply(plugin = "nu.studer.jooq")
    implementation(project(":jpa-entity"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

    runtimeOnly("mysql:mysql-connector-java:8.0.32")
    runtimeOnly("com.h2database:h2:2.1.214")
    runtimeOnly("io.r2dbc:r2dbc-h2")

    jooqGenerator(project(":jooq-dsl"))
    jooqGenerator("org.jooq:jooq-meta-extensions-hibernate:3.18.2")
    jooqGenerator("io.r2dbc:r2dbc-h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.6.1")
    testImplementation("io.kotest:kotest-assertions-core:5.6.1")
}