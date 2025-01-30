plugins {
    kotlin("jvm") version "2.1.10"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
}

application {
    mainClass.set("com.kiseru.asteroids.client.SimpleClientKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
