package com.needhamsoftware.unojar.gradle

import org.apache.tools.ant.taskdefs.Manifest
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.nio.file.Files

class UnoJarPlugin implements Plugin<Project> {

  void apply(Project project) {
    def extension = project.extensions.create('unojar', UnoJarExtension)
    project.task('packUnoJar') {
      group 'unojar'
      doLast {
        // TODO: This entire process of getting the ant task installed seems like complete hooey.
        //  There must be a better way...
        def cp = project.buildscript.configurations.getByName('classpath')
        def find = cp.find {
          if (it.canonicalPath.matches('.*needhamsoftware/unojar/ant/.*/ant-.*\\.jar')) return it
        }
        def ujjar = new URL('file://' + find)

        // the above seems to work for mavenLocal() but not mavenCentral()... so... (issue #
        if (!new File(find.toString()).exists()) {

          def pd = project.projectDir
          def ujd = new File(pd, ".unojar")
          ujd.mkdirs()

          // relies on the manifest attributes for the jar containing our plugin!
          def pkg = UnoJarPlugin.class.getPackage()
          def version = pkg.implementationVersion

          def repoLoc = extension.repoUrl + "/com/needhamsoftware/unojar/ant/" + version + "/ant-" + version + ".jar";
          def ujAnt = new File(ujd, "ant-" + version + ".jar")
          if (!ujAnt.exists()) {
            new URL(repoLoc).withInputStream{ i -> ujAnt.withOutputStream{ it << i }}
          }

          ujjar = ujAnt.toURI().toURL()
        }

        ant.taskdef(name: "unojar", classpath: ujjar, classname: "com.needhamsoftware.unojar.ant.UnoJarTask")
        def mf = Manifest.getDefaultManifest()
        extension.manifestAttrs.each { key, value -> mf.addConfiguredAttribute(new Manifest.Attribute(key, value)) }
        mf.addConfiguredAttribute(new Manifest.Attribute("Uno-Jar-Main-Class", extension.appMainClass))
        def mff = Files.createTempFile("unojar", "mf")
        mff.write "" + mf;
        ant.unojar(destFile: extension.unoJar, manifest: mff) {
          main extension.appFiles
          lib extension.depLibs
          // note that we can't use a mainfest {} closure here because gradle creates a Gradle Manifest not an Ant
          // manifest and then proceeds to throw it away. Many hours died to bring us this information...
        }
      }
    }
  }
}

class UnoJarExtension {
  Map manifestAttrs
  String appMainClass
  String unoJar
  Object appFiles
  Object depLibs
  String repoUrl = 'https://repo.maven.apache.org/maven2' // default
}
