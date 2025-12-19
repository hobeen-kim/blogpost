description = "collector-common"

dependencies {
    implementation(kotlin("stdlib"))

    //custom package
    api("com.hobeen:blogpost-common:1.1.3")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}
