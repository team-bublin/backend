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
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-jooq")

    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    runtimeOnly("mysql:mysql-connector-java:8.0.32")
    runtimeOnly("com.h2database:h2:2.1.214")

    jooqGenerator(project(":jooq-dsl"))
    jooqGenerator("org.jooq:jooq-meta-extensions-hibernate:3.18.2")
    jooqGenerator("com.h2database:h2:2.1.214")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
