import com.needhamsoftware.unojar.build.CreateVersionFiles

/**
 * Build logic for the Core project, which is a multi-release jar with some
 * more logic for testing
 */
plugins {
  id("unojar-mrjar-library")
}

// We need an incoming configuration which will contain the generated examples jar.
// This jar is generated when the Gradle plugin tests are run.

val examplesJar by configurations.creating {
  isCanBeResolved = true
  isCanBeConsumed = false
}

// This task generates a .version file, as well as a generated source file
// which allows getting the version from the jar at runtime.
val createVersions = tasks.register<CreateVersionFiles>("createVersionFiles") {
  version = project.version.toString()
  sourcesOutputDirectory = layout.buildDirectory.dir("generated/versions/src")
  resourcesOutputDirectory = layout.buildDirectory.dir("generated/versions/resources")
  packageName = "com.needhamsoftware.unojar"
}

sourceSets {
  main {
    // include the generated source files in the main source set
    java.srcDir(createVersions.map { it.sourcesOutputDirectory })
    // include the generated resources in the main resources set
    resources.srcDir(createVersions.map { it.resourcesOutputDirectory })
  }
  test {
    // include the generated examples jar in the test resources
    resources.srcDir(examplesJar.incoming.files)
  }
}