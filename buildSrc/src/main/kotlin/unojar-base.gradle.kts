import com.needhamsoftware.unojar.build.UnoJarBuildExtension

/**
 * Common configuration for UnoJar projects
 */
group = "com.needhamsoftware.unojar"

repositories {
  mavenCentral()
}

extensions.create("unoJarBuild", UnoJarBuildExtension::class)