@file:Suppress("UnstableApiUsage")

plugins {
    application
    id("ca.cutterslade.analyze")
    id("com.needhamsoftware.unojar") version("2.0.0-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass.set("MainK1")
}

dependencies {
    runtimeOnly("org.slf4j:slf4j-simple:1.7.32")
}

unojar {
    manifestAttributes.set(mapOf("Test-Attribute" to "Kotlin-Extension"))
}

tasks {
    register<com.needhamsoftware.unojar.gradle.PackageUnoJarTask>("packageUnoJar2") {
        dependsOn("jar")
        mainClass.set("MainK2")
        archiveBaseName.set("test")
        embedConfiguration.set(configurations.getByName("runtimeClasspath"))
        manifestAttributes.set(mapOf("Test-Attribute" to "Kotlin-Register"))
    }
}

tasks.build {
    dependsOn("packageUnoJar", "packageUnoJar2")
}
