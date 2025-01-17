plugins {
  id("unojar-base")
  id("com.palantir.git-version")
  id("java")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
  withSourcesJar()
  withJavadocJar()
}

tasks.withType<Jar>().configureEach {
  duplicatesStrategy = DuplicatesStrategy.WARN
  if (providers.gradleProperty("forceManifest").isPresent || !version.toString().endsWith("-SNAPSHOT")) {
    val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
    val details = versionDetails()
    manifest {
      attributes(
          "Implementation-Version" to version,
          "Build-Tool" to "Gradle ${gradle.gradleVersion}",
          "Build-Revision" to details.gitHashFull + if (details.isCleanTag) "" else " (with uncommitted files)",
          "Created-By" to "Gradle ${gradle.gradleVersion}",
          "Build-Jdk" to "${providers.systemProperty("java.version").get()} (${providers.systemProperty("java.vendor").get()} ${providers.systemProperty("java.vm.version").get()})",
          "Build-OS" to "${providers.systemProperty("os.name").get()} ${providers.systemProperty("os.arch").get()} ${providers.systemProperty("os.version").get()}"
      )
    }
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.encoding = "UTF-8"
  sourceCompatibility = "8"
  targetCompatibility = "8"
}

val javaToolchains = extensions.getByType(JavaToolchainService::class.java)

// Expose path to JDKs for tests
tasks.withType<Test>().configureEach {
  listOf(8, 9, 11).forEach {
    val jdkDir = javaToolchains.compilerFor {
      languageVersion = JavaLanguageVersion.of(it)
    }.get().executablePath.asFile.parentFile.parentFile
    systemProperty("unojar.jdk.$it", jdkDir.absolutePath)
  }
  testLogging {
    events("passed", "skipped", "failed")
  }
}