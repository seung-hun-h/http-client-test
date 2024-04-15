plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.httpcomponents.client5:httpclient5-fluent:5.3.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("org.mock-server:mockserver-client-java:5.15.0")

    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.mock-server:mockserver-netty:5.15.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}