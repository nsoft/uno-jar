plugins {
  id("unojar-gradle-plugin")
}

dependencies {
  implementation(projects.core)
  implementation(libs.commons.io)
  implementation(libs.commons.lang3)

  implementation(gradleApi())

  testImplementation(gradleTestKit())
  testImplementation(libs.junit.jupiter)
  testRuntimeOnly(libs.junit.launcher)

}

gradlePlugin {
  website = "https://github.com/nsoft/uno-jar"
  vcsUrl = "https://github.com/nsoft/uno-jar.git"

  plugins {
    create("unojar") {
      id = "com.needhamsoftware.unojar"
      displayName = "Uno-Jar"
      description = "Single jar packaging based on a JarClassLoader. Unlike maven shade and gradle shadow, this form of packaging does not intermix classes into a single directory, and thereby maintains a degree of separation between libraries with distinct licensing concerns. Does not require write access to the filesystem like capsule"
      implementationClass = "com.needhamsoftware.unojar.gradle.UnoJarPlugin"
      description = "Single jar packaging based on a JarClassLoader. Unlike maven shade and gradle shadow, " +
          "this form of packaging does not intermix classes into a single directory, and thereby maintains a " +
          "degree of separation between libraries with distinct licensing concerns. Does not require write " +
          "access to the filesystem like capsule"
      tags = listOf("jar", "executable-jar", "onejar", "one-jar", "shade", "shadow", "fatjar", "uberjar", "capsule")
    }
  }
}

val exampleJarDir = layout.buildDirectory.dir("examples-jar")

tasks.named<Test>("test") {
  useJUnitPlatform()
  inputs.files("test-project", fileTree("../jdk8"))
  inputs.files("examples", fileTree("../examples"))
  systemProperty("export.example.jar.dir", exampleJarDir.get().asFile.canonicalPath)
  outputs.dir(exampleJarDir.get().asFile.canonicalPath)
}

val examplesJar by configurations.creating {
  isCanBeConsumed = true
  isCanBeResolved = false
  outgoing.artifact(exampleJarDir) {
    builtBy(tasks.named("test"))
  }
}