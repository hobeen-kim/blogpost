import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.gradle.api.plugins.JavaPluginExtension

plugins {
	kotlin("jvm") version "2.0.21" apply false
	kotlin("plugin.spring") version "2.0.21" apply false
	id("org.springframework.boot") version "4.0.1" apply false
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
}

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")

	configure<JavaPluginExtension> {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(21))
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
}
