// experimental kotlin build... take nothing here seriously, see README

buildscript {
  repositories {
    mavenLocal()
    mavenCentral()
  }
}

plugins {
  id("java")
  id("com.needhamsoftware.unojar") version "2.0.0-SNAPSHOT"
}

group = "org.example"
version = "2.0.0-SNAPSHOT"

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  implementation("com.needhamsoftware.unojar:core:2.0.0-SNAPSHOT")
  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
}

unojar {
  archiveBaseName.set("sandbox")
  manifestAttributes.set(mapOf("Relaunch-Increment" to "2"))
  mainClass.set("com.needhamsoftware.sandbox.relaunch.Main")
}

tasks.test {
  useJUnitPlatform()
}