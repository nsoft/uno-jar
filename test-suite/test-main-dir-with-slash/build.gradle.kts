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
    implementation("com.needhamsoftware.unojar:core")
    implementation("junit:junit:4.13.2")
}

application {
    mainClass.set("TestMainDirWithSlash")
}

tasks.build {
    dependsOn("packageUnoJar")
}
