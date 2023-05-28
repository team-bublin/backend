import com.avast.gradle.dockercompose.ComposeExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

java.sourceCompatibility = JavaVersion.VERSION_17

plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.spring") version "1.8.10"

    id("org.springframework.boot") version "3.0.4" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
    id("nu.studer.jooq") version "8.1" apply false
    id("com.avast.gradle.docker-compose") version "0.16.12" apply false
}

allprojects {
    group = "io.prism"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    ext {
        set("mysqlDockerComposePath", "$rootDir/docker/docker-compose.yml")
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    apply(plugin = "kotlin-spring")
    apply(plugin = "nu.studer.jooq")

    apply(plugin = "com.avast.gradle.docker-compose")

    val dockerComposePath = ext.get("mysqlDockerComposePath") as String

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-webflux")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    configure<ComposeExtension> {
        includeDependencies.set(true)

        createNested("local").apply {
            executable.set("/usr/local/bin/docker-compose-v1")
            dockerExecutable.set("/usr/local/bin/docker")
            dockerComposeWorkingDirectory.set(file("$rootDir"))

            stopContainers.set(false)
            setProjectName("mysql-local")
            projectNamePrefix = "mysql-stack"

            useComposeFiles.set(listOf(dockerComposePath))
        }
    }
}
