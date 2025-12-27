description = "collector-common"

dependencies {
    implementation(kotlin("stdlib"))

    //custom package
    api("com.hobeen:blogpost-common:1.2.3")

    //dataformat
    api("com.fasterxml.jackson.core:jackson-databind:2.19.2")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}
