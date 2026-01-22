import com.needhamsoftware.unojar.build.UnoJarBuildExtension

plugins {
  id("maven-publish")
  id("signing")
  id("unojar-base")
}

/*

 Maven Publishing stuff to publish to central. This also allows you to publish
 to a local repository on your system byt setting the version to -LOCAL instead
 of -SNAPSHOT. Specifically:

  To test pom and artifact generation locally...

  1. Ensure GPG keyring in .gnupg dir
  2. Check that the value (above) for ext.
  3. Change version to remove -SNAPSHOT and add -LOCAL
  4. Run gradle uploadArchives
  5. observe archives uploaded to ext.uploadRepo (adjust location as needed)


  To release to central

  1. Ensure GPG keyring in .gnupg dir
  2. Ensure passwords for sonatype in gradle.properties
  3. Test the production of artifacts locally with -LOCAL (see above)
  4. No really, test it, and read everything...
  5. Seriously, don"t skip #3!
  6. Change version to remove -SNAPSHOT
  7. Run gradle uploadRelease
  8. Hope to hell you didn"t miss anything in step 3....
  9. you skipped #3? OMG you suck... *sigh*

 */

val isSnapshot = version.toString().endsWith("-SNAPSHOT")
val isLocal = version.toString().endsWith("-LOCAL")
val isRelease = !(isLocal || isSnapshot)

val releaseRepo = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
val snapshotRepo = "https://oss.sonatype.org/content/repositories/snapshots"
val testingRepo = rootProject.layout.buildDirectory.dir("myRepo")
val uploadRepo = uri(if (isRelease) {
  releaseRepo
} else {
  if (isSnapshot) {
    snapshotRepo
  } else {
    testingRepo
  }
})

publishing {
  publications {
    if (!pluginManager.hasPlugin("java-gradle-plugin")) {
      create<MavenPublication>("mavenJava") {
        println("Creating MavenJava publication")
        groupId = group.toString()
        artifactId = project.name
        version = project.version.toString()
        from(components["java"])
      }
    }
    configureEach {
      if (this is MavenPublication) {
        pom {
          name = extensions.getByType<UnoJarBuildExtension>().shortDesc
          description = project.description
          url = "https://github.com/nsoft/uno-jar"
          packaging = "jar"
          licenses {
            license {
              name = "MIT/One-JAR"
              url = "https://github.com/nsoft/uno-jar/blob/master/LICENSE.txt"
              distribution = "repo"
            }
          }
          scm {
            connection = "scm:git:git@github.com:nsoft/uno-jar.git"
            developerConnection = "scm:git:git@github.com:nsoft/uno-jar.git"
            url = "git@github.com:nsoft/uno-jar.git"
          }
          developers {
            developer {
              id = "nsoft"
              name = "Patrick Heck"
              email = "gus@needhamsoftware.com"
            }
          }
        }
      }
    }
  }
  repositories {
    maven {
      name = "sonatype"
      if (!isLocal) {
        credentials(PasswordCredentials::class)
      }
      url = uploadRepo
    }
    maven {
      name = "build"
      url = uri(testingRepo)
    }
  }
}

val userName = providers.gradleProperty("sonatypeUsername")
if (userName.orNull == "nsoft") {
  // no point in signing unless able to upload
  signing {
    publishing.publications.all {
      sign(this)
    }
  }
} else {
  println("Upload user not found, not signing Jars")
}