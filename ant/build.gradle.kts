plugins {
  id("unojar-library")
}

unoJarBuild {
  shortDesc = "Uno-Jar Ant Task"
}
description = "Ant Task for packaging FatJars with a JarClassLoader"

dependencies {
  implementation(projects.core)
  implementation(libs.ant)
}

tasks {
  jar {
    from(configurations.runtimeClasspath.get().incoming.artifactView {
      attributes {
        attribute(Attribute.of("artifactType", String::class.java), "jar")
      }
      componentFilter {
        it is ProjectComponentIdentifier && it.projectName == "core"
      }
    }.files) {
      into("com/needhamsoftware/unojar/ant")
      rename { "core.jar" }
    }
  }
}