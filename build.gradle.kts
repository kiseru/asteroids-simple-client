plugins {
    kotlin("jvm") version "2.1.10"
    application
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.kiseru.asteroids.client.SimpleClient")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
