import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  gradleApi()
  implementation(libs.gradle.publish.plugin)
  implementation(libs.palantir.git.plugin)
}

tasks.withType<JavaCompile>().configureEach {
  targetCompatibility = "11"
  sourceCompatibility = "11"
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "11"
  }
}