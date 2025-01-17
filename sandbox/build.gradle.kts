// experimental kotlin build... take nothing here seriously, see README
plugins {
  id("java")
  id("com.needhamsoftware.unojar") version "2.0.0-SNAPSHOT"
}

group = "org.example"
version = "2.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.needhamsoftware.unojar:core:2.0.0-SNAPSHOT")
  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
}

unojar {
  archiveBaseName.set("sandbox")
  manifestAttributes.set(mapOf(
      "Relaunch-Increment" to "2",
      "UnoJar-SystemClassLoaderClass" to "com.needhamsoftware.unojar.TestClassLoader"
  ))
  mainClass.set("com.needhamsoftware.sandbox.relaunch.CustomCLRelaunch")
}

tasks.test {
  useJUnitPlatform()
}