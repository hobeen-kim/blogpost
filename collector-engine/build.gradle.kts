description = "collector-engine"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":collector-common"))

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
