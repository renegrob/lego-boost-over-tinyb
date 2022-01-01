plugins {
    java
}

group = "com.github.renegrob"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    flatDir {
        dirs("/usr/lib/x86_64-linux-gnu/../lib/java/")
    }
}

dependencies {
    implementation(project(":jlwp"))
    implementation("intel-iot-devkit:tinyb:0.5.1")
    implementation("com.formdev:flatlaf:2.0-rc1")
    implementation("org.apache.logging.log4j:log4j-core:2.17.1")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}