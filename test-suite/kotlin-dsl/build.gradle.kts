plugins {
    application
    id("ca.cutterslade.analyze")
    id("com.github.ben-manes.versions")
    id("com.needhamsoftware.unojar")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass.set("Main")
}

dependencies {
    implementation("io.javalin:javalin:3.13.10")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.32")
}

tasks {
    register<com.needhamsoftware.unojar.gradle.PackageUnoJarTask>("packageUnoJar2") {
        dependsOn("jar")
        mainClass.set("Main2")
        archiveBaseName.set("test")
        embedConfiguration.set(configurations.getByName("runtimeClasspath"))
        manifestAttributes.set(mapOf("Test-Attribute" to "TestValue"))
    }
}

tasks.build {
    dependsOn("packageUnoJar", "packageUnoJar2")
}
