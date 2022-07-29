plugins {
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