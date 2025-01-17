plugins {
  id("unojar-library")
}

val java9 by sourceSets.creating {
  java {
    srcDir("src/main/java9")
  }
}

val java11 by sourceSets.creating {
  java {
    srcDir("src/main/java11")
  }
}

tasks.named<JavaCompile>("compileJava9Java") {
  sourceCompatibility = "9"
  targetCompatibility = "9"
  javaCompiler = javaToolchains.compilerFor {
    languageVersion = JavaLanguageVersion.of(9)
  }
}
tasks.named<JavaCompile>("compileJava11Java") {
  sourceCompatibility = "11"
  targetCompatibility = "11"
  javaCompiler = javaToolchains.compilerFor {
    languageVersion = JavaLanguageVersion.of(11)
  }
}

tasks.named<Jar>("sourcesJar") {
  from(java9.allJava)
  from(java11.allJava)
}

tasks.named<Jar>("jar") {
  into("META-INF/versions/9") {
    from(java9.output)
  }
  into("META-INF/versions/11") {
    from(java11.output)
  }
}

tasks.withType<Jar>().configureEach {
  manifest.attributes("Multi-Release" to "true")
}