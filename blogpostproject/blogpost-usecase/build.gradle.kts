plugins {
    kotlin("jvm")
}

group = "com.hobeen"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":blogpost-domain"))
    implementation(project(":blogpost-common"))


    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}