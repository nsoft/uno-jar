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
        def cp = project.buildscript.configurations.getByName('classpath')
        def find = cp.find {
          if (it.canonicalPath.matches('.*needhamsoftware/unojar/ant/.*/ant-.*\\.jar')) return it
        }
        def ujjar = new URL('file://' + find)
        ant.taskdef(name: "unojar", classpath: ujjar, classname: "com.needhamsoftware.unojar.ant.UnoJarTask")
        def mf = Manifest.getDefaultManifest()
        extension.manifestAttrs.each { key, value -> mf.addConfiguredAttribute(new Manifest.Attribute(key, value)) }
        mf.addConfiguredAttribute(new Manifest.Attribute("Uno-Jar-Main-Class", extension.unoMain))
        def mff = Files.createTempFile("unojar", "mf")
        mff.write "" + mf;
        ant.unojar(destFile: extension.destFile, manifest: mff) {
          main extension.main
          lib extension.lib
          // note that we can't use a mainfest {} closure here because gradle creates a Gradle Manifest not an Ant
          // manifest and then proceeds to throw it away. Many hours died to bring us this information...
        }
      }
    }
  }
}

class UnoJarExtension {
  Map manifestAttrs
  String unoMain
  String destFile
  Object main
  Object lib
}
