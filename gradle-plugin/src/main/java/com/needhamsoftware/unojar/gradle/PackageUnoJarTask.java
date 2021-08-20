package com.needhamsoftware.unojar.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.BasePluginConvention;
import org.gradle.api.plugins.JavaApplication;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;

public abstract class PackageUnoJarTask
    extends DefaultTask {

  private static final String DEFAULT_UNOJAR_VERSION = "1.0.2";
  private static final String DEFAULT_CLASSIFIER = "unojar";

  @Input
  @Optional
  public abstract Property<String> getArchiveBaseName();

  @Input
  @Optional
  public abstract Property<String> getArchiveClassifier();

  @Input
  @Optional
  public abstract Property<Configuration> getEmbedConfiguration();

  @Input
  @Optional
  public abstract Property<String> getMainClass();

  @Input
  @Optional
  public abstract MapProperty<String, String> getManifestAttributes();

  @TaskAction
  public void action()
      throws IOException {
    final Project project = getProject();

    final String archiveBaseName = doGetArchiveBaseName();
    final String archiveClassifier = doGetArchiveClassifier();
    final String mainClass = doGetMainClass();
    final Configuration unoJarConfiguration = doGetUnoJarConfiguration();
    final Configuration embedConfiguration = doGetEmbedConfiguration();
    final Map<String, String> manifestAttributes = doGetManifestAttributes();

    final BasePluginConvention basePluginConvention = project.getConvention().findPlugin(BasePluginConvention.class);
    if (basePluginConvention == null) {
      throw new GradleException("base plugin convention not found");
    }
    final File libsDir = basePluginConvention.getLibsDirectory().getAsFile().get();

    final Set<ResolvedArtifact> unoJarResolvedArtifacts = unoJarConfiguration.getResolvedConfiguration()
        .getResolvedArtifacts();
    final FileCollection jarOutputFiles = project.getTasks().named("jar", Jar.class).get().getOutputs()
        .getFiles();
    final Set<ResolvedArtifact> runtimeResolvedArtifacts = embedConfiguration.getResolvedConfiguration()
        .getResolvedArtifacts();

    libsDir.mkdirs();
    final File outputFile = new File(libsDir, String.format("%s-%s.jar", archiveBaseName, archiveClassifier));

    final Manifest manifest = new Manifest();
    for (final Map.Entry<String, String> entry : manifestAttributes.entrySet()) {
      manifest.getMainAttributes().putValue(entry.getKey(), entry.getValue());
    }

    try (final UnoJarPackager unoJarPackager = new UnoJarPackager(new FileOutputStream(outputFile), mainClass,
        manifest)) {
      for (final ResolvedArtifact resolvedArtifact : unoJarResolvedArtifacts) {
        getLogger().info("adding boot classes: {}", resolvedArtifact.getModuleVersion());
        unoJarPackager.addBootJar(resolvedArtifact.getFile());
      }

      for (final Object dependsOn : getDependsOn()) {
        Jar jar = null;
        if (dependsOn instanceof String) {
          final Task task = project.getTasks().findByName((String) dependsOn);
          if (task instanceof Jar) {
            jar = (Jar) task;
          }
        } else if (dependsOn instanceof Task) {
          if (dependsOn instanceof Jar) {
            jar = (Jar) dependsOn;
          }
        }
      }
      for (final File jarOutputFile : jarOutputFiles) {
        getLogger().info("adding main JAR: {}", jarOutputFile.getName());
        unoJarPackager.addMainJar(jarOutputFile);
      }

      for (final ResolvedArtifact resolvedArtifact : runtimeResolvedArtifacts) {
        getLogger().info("adding lib JAR: {}", resolvedArtifact.getModuleVersion());
        unoJarPackager.addLibJar(resolvedArtifact.getFile(),
            String.format("%s/%s/%s",
                resolvedArtifact.getModuleVersion().getId().getGroup(),
                resolvedArtifact.getModuleVersion().getId().getName(),
                resolvedArtifact.getFile().getName()));
      }
    }
  }

  private Configuration doGetUnoJarConfiguration() {
    Configuration configuration = getProject().getConfigurations().findByName("unojar");
    if (configuration == null) {
      configuration = getProject().getConfigurations().create("unojar");
      getProject().getDependencies().add("unojar",
          String.format("com.needhamsoftware.unojar:core:%s", doGetUnoJarVersion()));
    }
    return configuration;
  }

  private String doGetUnoJarVersion() {
    final UnoJarExtension unoJarExtension = getProject().getExtensions().findByType(UnoJarExtension.class);
    if (unoJarExtension == null) {
      throw new GradleException("unojar extension not found");
    }
    return unoJarExtension.getVersion().getOrElse(DEFAULT_UNOJAR_VERSION);
  }

  private String doGetArchiveBaseName() {
    if (getArchiveBaseName().isPresent()) {
      return getArchiveBaseName().get();
    }
    final UnoJarExtension unoJarExtension = getProject().getExtensions().findByType(UnoJarExtension.class);
    if (unoJarExtension == null) {
      throw new GradleException("unojar extension not found");
    }
    return unoJarExtension.getArchiveBaseName().getOrElse(getProject().getName());
  }

  private String doGetArchiveClassifier() {
    if (getArchiveClassifier().isPresent()) {
      return getArchiveClassifier().get();
    }
    final UnoJarExtension unoJarExtension = getProject().getExtensions().findByType(UnoJarExtension.class);
    if (unoJarExtension == null) {
      throw new GradleException("unojar extension not found");
    }
    return unoJarExtension.getArchiveClassifier().getOrElse(DEFAULT_CLASSIFIER);
  }

  private Configuration doGetEmbedConfiguration() {
    if (getEmbedConfiguration().isPresent()) {
      return getEmbedConfiguration().get();
    }
    final UnoJarExtension unoJarExtension = getProject().getExtensions().findByType(UnoJarExtension.class);
    if (unoJarExtension == null) {
      throw new GradleException("unojar extension not found");
    }
    return unoJarExtension.getEmbedConfiguration()
        .getOrElse(getProject().getConfigurations().getByName("runtimeClasspath"));
  }

  private String doGetMainClass() {
    if (getMainClass().isPresent()) {
      return getMainClass().get();
    }
    final JavaApplication javaApplication = getProject().getExtensions().findByType(JavaApplication.class);
    if (javaApplication == null) {
      throw new GradleException("mainClass not specified");
    }
    return javaApplication.getMainClass().get();
  }

  private Map<String, String> doGetManifestAttributes() {
    if (getManifestAttributes().isPresent()) {
      return getManifestAttributes().get();
    }
    final UnoJarExtension unoJarExtension = getProject().getExtensions().findByType(UnoJarExtension.class);
    if (unoJarExtension == null) {
      throw new GradleException("unojar extension not found");
    }
    return unoJarExtension.getManifestAttributes().getOrElse(new HashMap<>());
  }
}
