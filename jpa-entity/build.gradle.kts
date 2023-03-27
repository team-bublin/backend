import org.springframework.boot.gradle.tasks.bundling.BootJar

description = "JPA Entity Module"

plugins {
    kotlin("plugin.jpa") version "1.8.10"
}

dependencies {
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}