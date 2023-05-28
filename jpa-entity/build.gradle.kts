import org.springframework.boot.gradle.tasks.bundling.BootJar

description = "JPA Entity Module"

plugins {
    kotlin("plugin.jpa") version "1.8.10"
}

ext {
    set("schemaInitPath", "$rootDir/docker/init.d")
    set("schemaFileName", "0_init_schema.sql")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.hibernate.orm:hibernate-ant:6.1.4.Final")
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
    val generateSchemaTask = getTasksByName("generateSchemaSQL", false).first()
    dependsOn(generateSchemaTask)

    this.mustRunAfter(generateSchemaTask)
}

tasks.register<JavaExec>("generateSchemaSQL") {

    val schemaInitPath = ext.get("schemaInitPath") as String
    val schemaFileName = ext.get("schemaFileName") as String

    doFirst {
        file(schemaInitPath).mkdirs()
    }

    mainClass.set("io.prism.GenerateSchemaSQL")
    classpath = sourceSets["main"].runtimeClasspath

    systemProperty("hibernate.dialect.storage_engine", "innodb")
    args("$schemaInitPath/$schemaFileName")
}

tasks.register("runDockerCompose") {
    val generateSchemaTask = getTasksByName("generateSchemaSQL", false).first()
    val dockerComposeUpTask = getTasksByName("localComposeUp", false).first()

    dependsOn(generateSchemaTask)
    dependsOn(dockerComposeUpTask)

    dockerComposeUpTask.mustRunAfter(generateSchemaTask)
}
