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

dependencies {
    implementation(project(":test-lib"))
}

application {
    mainClass.set("TestMain")
}

tasks.build {
    dependsOn("packageUnoJar")
}
