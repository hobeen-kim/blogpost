plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

description = "adapter-common"

dependencies {
    implementation(project(":collector-common"))
    implementation(project(":collector-engine"))
    implementation(project(":collector-outport"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //web
    implementation("org.springframework.boot:spring-boot-starter-web")

    //jsoup
    implementation("org.jsoup:jsoup:1.21.2")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}