plugins {
    kotlin("jvm") version "2.2.21"
    `maven-publish`
}

group = "com.hobeen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/<OWNER>/<REPO>")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"]) // kotlin이면 components["java"]가 맞는지 프로젝트에 따라 조정
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}