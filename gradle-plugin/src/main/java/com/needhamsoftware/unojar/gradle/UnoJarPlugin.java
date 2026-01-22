package com.needhamsoftware.unojar.gradle;

import com.needhamsoftware.unojar.VersionInfo;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.BasePluginExtension;
import org.gradle.api.plugins.JavaApplication;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.bundling.Jar;

public class UnoJarPlugin
    implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    UnoJarExtension extension = createExtension(project);

    Configuration unojar = createUnoJarConfiguration(project, extension);
    PluginManager pluginManager = project.getPluginManager();
    pluginManager.withPlugin("java", unused -> {
      TaskProvider<PackageUnoJarTask> packageJar = project.getTasks().register("packageUnoJar", PackageUnoJarTask.class);
      ProviderFactory providers = project.getProviders();
      project.getTasks().withType(PackageUnoJarTask.class).configureEach(task -> {
        task.setDescription("Packages the application as a fat jar");
        task.setGroup("build");
        task.getMainJar().convention(project.getTasks().named("jar", Jar.class).flatMap(AbstractArchiveTask::getArchiveFile));
        task.getUnoJarClasspath().from(unojar);
        task.getArtifacts().convention(extension.getArtifacts());
        task.getMainClass().convention(extension.getMainClass());
        task.getManifestAttributes().convention(extension.getManifestAttributes());
        BasePluginExtension basePluginExtension = project.getExtensions().getByType(BasePluginExtension.class);
        task.getOutputDirectory().convention(basePluginExtension.getLibsDirectory());

        task.getArchiveBaseName().convention(extension.getArchiveBaseName().orElse(basePluginExtension.getArchivesName()));
        task.getArchiveAppendix().convention(extension.getArchiveAppendix());
        String projectVersion = project.getVersion().toString();
        task.getArchiveVersion().convention(extension.getArchiveVersion().orElse(providers.provider(() -> "unspecified".equals(projectVersion) ? null : projectVersion)));
        task.getArchiveClassifier().convention(extension.getArchiveClassifier());
        task.getArchiveExtension().convention(extension.getArchiveExtension());
      });
    });
    pluginManager.withPlugin("application", unused2 -> {
      JavaApplication application = project.getExtensions().getByType(JavaApplication.class);
      extension.getMainClass().convention(application.getMainClass());
    });
    project.getTasks().named("assemble", task -> task.dependsOn("packageUnoJar"));
  }

  private static UnoJarExtension createExtension(Project project) {
    UnoJarExtension extension = project.getExtensions().create("unojar", UnoJarExtension.class);
    extension.getVersion().convention(VersionInfo.getVersion());
    new ArtifactsAdapter(extension.getArtifacts()).from(project.getConfigurations().getByName("runtimeClasspath"));
    return extension;
  }

  private static Configuration createUnoJarConfiguration(Project project, UnoJarExtension extension) {
    Configuration unojar = project.getConfigurations().create("unojar", conf -> {
      conf.setCanBeConsumed(false);
      conf.setCanBeResolved(true);
    });
    unojar.getDependencies().addLater(extension.getVersion().map(v ->
        project.getDependencies().create("com.needhamsoftware.unojar:core:" + v + "@jar")
    ));
    return unojar;
  }
}