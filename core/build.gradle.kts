plugins {
  id("unojar-core")
}

unoJarBuild {
  shortDesc = "Uno-Jar Core"
}

description = "Libraries for packaging FatJars with a JarClassLoader"

dependencies {
  testImplementation(libs.easier.mock)
  testImplementation(libs.junit4)
  java9Implementation(files(sourceSets.main.get().output.classesDirs) {
    builtBy(tasks.compileJava)
  })
  java11Implementation(files(sourceSets.main.get().output.classesDirs) {
    builtBy(tasks.compileJava)
  })
  java11Implementation(files(sourceSets.java9.get().output.classesDirs) {
    builtBy(tasks.compileJava9Java)
  })
  examplesJar(project("path" to ":gradle-plugin", "configuration" to "examplesJar"))
}

tasks {
  jar {
    from("../") {
      include("LICENSE.txt")
      include("NOTICE.txt")
    }
  }
}