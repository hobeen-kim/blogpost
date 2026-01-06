import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.gradle.api.plugins.JavaPluginExtension

plugins {
    kotlin("jvm") version "1.9.25" apply false
    kotlin("plugin.spring") version "1.9.25" apply false
    kotlin("plugin.jpa") version "1.9.25" apply false
    id("org.springframework.boot") version "3.5.8" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "com.hobeen"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/hobeen-kim/blogpost-collector")
            credentials {
                username = (findProperty("gpr.user") as String?) ?: System.getenv("GITHUB_ACTOR")
                password = (findProperty("gpr.key") as String?) ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    configure<KotlinJvmProjectExtension> {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    dependencies {
        "implementation"("org.jetbrains.kotlin:kotlin-reflect")

        //batch
        "implementation"("org.springframework.boot:spring-boot-starter-batch")

        //db
        "implementation"("org.springframework.boot:spring-boot-starter-data-jdbc")
        "implementation"("org.springframework.boot:spring-boot-starter-data-jpa")
        "runtimeOnly"("org.postgresql:postgresql")

        //dataformat
        "implementation"("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
        "implementation"("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
        "implementation"("com.fasterxml.jackson.module:jackson-module-kotlin")

        //custom package
        "implementation"("com.hobeen:blogpost-common:1.2.5")

        //test
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5")
        "testImplementation"("org.springframework.batch:spring-batch-test")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    configure<org.jetbrains.kotlin.allopen.gradle.AllOpenExtension> {
        annotation("jakarta.persistence.Entity")
        annotation("jakarta.persistence.MappedSuperclass")
        annotation("jakarta.persistence.Embeddable")
    }
}
