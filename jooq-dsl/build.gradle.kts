import org.springframework.boot.gradle.tasks.bundling.BootJar

description = "jOOQ DSL Generator Module"

plugins {
    id("nu.studer.jooq") version "8.1"
}

dependencies {
    implementation (project(":jpa-entity"))
    implementation("org.jooq:jooq-codegen:3.17.6")
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}