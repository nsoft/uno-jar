package com.needhamsoftware.unojar.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class UnoJarPlugin
    implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getExtensions().create("unojar", UnoJarExtension.class);

    project.getTasks().register("packageUnoJar", PackageUnoJarTask.class,
        task -> task.dependsOn("jar"));
  }
}
