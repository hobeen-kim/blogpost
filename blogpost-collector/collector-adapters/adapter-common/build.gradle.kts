plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

description = "adapter-common"

dependencies {
    api(project(":collector-common"))
    api(project(":collector-engine"))
    api(project(":collector-outport"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //jsoup
    api("org.jsoup:jsoup:1.21.2")

    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}