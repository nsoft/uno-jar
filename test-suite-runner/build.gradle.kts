import com.needhamsoftware.unojar.build.PrepareAntTask

/**
 * This project is used to run the test suite builds in an isolated
 * context, using Gradle's TestKit.
 */
plugins {
  id("unojar-java-base")
}

dependencies {
  testImplementation(projects.core)
  testImplementation(gradleTestKit())
  testImplementation(libs.junit.jupiter)
  testRuntimeOnly(libs.junit.launcher)
  testRuntimeOnly(projects.ant)
}

val prepareAnt = tasks.register<PrepareAntTask>("prepareAnt") {
  group = "build"
  description = "Prepare the Ant build for the test suite"
  antVersion = libs.versions.ant
  installDirectory = layout.buildDirectory.dir("ant")
  mirror = "https://dlcdn.apache.org"
}

tasks.withType<Test>().configureEach {
  dependsOn(prepareAnt)
  useJUnitPlatform()
  systemProperty("ant.home", prepareAnt.flatMap { it.installDirectory.dir("bin") }.get().asFile.absolutePath)
  inputs.files("test-suite", fileTree("../test-suite"))
}

java {
  toolchain {
    // This is the version that the test will be started with, and it must
    // match what the UnoJar Build requires, but the tests themselves will
    // use whatever is defined in the test suite's build.gradle.kts
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}