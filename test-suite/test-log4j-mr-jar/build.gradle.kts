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
    implementation(project(":test-lib-log4j"))
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
}

application {
    mainClass.set("TestMainLog4jMRJar")
}

tasks.build {
    dependsOn("packageUnoJar")
}
