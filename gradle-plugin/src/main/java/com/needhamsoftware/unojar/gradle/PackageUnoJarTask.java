package com.needhamsoftware.unojar.gradle;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.artifacts.result.ResolvedVariantResult;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class PackageUnoJarTask extends DefaultTask {

  private static final String DEFAULT_CLASSIFIER = "unojar";
  private static final String DEFAULT_EXTENSION = "jar";
  public static final String BLANK = "\\s*";

  public static final Pattern BLANK_PAT = Pattern.compile(BLANK);

  @Input
  @SuppressWarnings("UnstableApiUsage")
  public String getArchiveFileName() {
    final List<Provider<String>> archiveNamePartProviders = new ArrayList<>();
    archiveNamePartProviders.add(getArchiveBaseName());
    archiveNamePartProviders.add(getArchiveAppendix());
    archiveNamePartProviders.add(getArchiveVersion());
    archiveNamePartProviders.add(getArchiveClassifier().orElse(DEFAULT_CLASSIFIER));

    final List<String> archiveNameParts = archiveNamePartProviders.stream().filter(Provider::isPresent).map(Provider::get).filter((part) -> !BLANK_PAT.matcher(part).matches()).collect(Collectors.toList());

    final String extension = getArchiveExtension().getOrElse(DEFAULT_EXTENSION);

    return StringUtils.join(archiveNameParts, "-") + "." + extension;
  }

  @Classpath
  public abstract ConfigurableFileCollection getUnoJarClasspath();

  @Input
  @Optional
  public abstract Property<String> getArchiveBaseName();

  @InputFile
  @Optional
  public abstract RegularFileProperty getMainJar();

  @Input
  @Optional
  public abstract Property<String> getArchiveAppendix();

  @Input
  @Optional
  public abstract Property<String> getArchiveVersion();

  @Input
  @Optional
  public abstract Property<String> getArchiveClassifier();

  @Input
  @Optional
  public abstract Property<String> getArchiveExtension();

  @Internal
  public abstract ListProperty<ArtifactDetails> getArtifacts();

  @Input
  protected Provider<List<ResolvedVariantResult>> getVariants() {
    return getArtifacts().map(l -> l.stream().map(ArtifactDetails::getVariant).collect(Collectors.toList()));
  }

  @InputFiles
  protected Provider<List<File>> getArtifactFiles() {
    return getArtifacts().map(l -> l.stream().map(ArtifactDetails::getFile).collect(Collectors.toList()));
  }

  @InputFiles
  @Optional
  public abstract ConfigurableFileCollection getExtraJars();

  @Input
  @Optional
  public abstract Property<String> getMainClass();

  @SuppressWarnings("UnstableApiUsage")
  @Input
  @Optional
  public abstract MapProperty<String, String> getManifestAttributes();

  @Internal
  public abstract DirectoryProperty getOutputDirectory();

  @OutputFile
  public Provider<RegularFile> getOutputFile() {
    return getOutputDirectory().file(getArchiveFileName());
  }

  public void from(Provider<Configuration> configuration) {
    new ArtifactsAdapter(getArtifacts()).from(configuration.get());
  }

  public void from(Configuration configuration) {
    new ArtifactsAdapter(getArtifacts()).from(configuration);
  }

  public void fromProvider(Provider<Set<ResolvedArtifactResult>> artifacts) {
    new ArtifactsAdapter(getArtifacts()).fromProvider(artifacts);

  }

  @TaskAction
  public void action() throws IOException {
    final TaskHandler taskHandler = new TaskHandler();
    taskHandler.action();
  }

  @Override
  @Input
  public String getGroup() {
    return "build";
  }

  private class TaskHandler {

    private void action() throws IOException {
      final String mainClass = getMainClass().get();
      final Map<String, String> manifestAttributes = getManifestAttributes().get();
      final Set<File> unoJarResolvedArtifacts = getUnoJarClasspath().getFiles();
      final List<ArtifactDetails> runtimeResolvedArtifacts = getArtifacts().get();

      final File outputFile = getOutputFile().get().getAsFile();

      final Manifest manifest = new Manifest();
      for (final Map.Entry<String, String> entry : manifestAttributes.entrySet()) {
        manifest.getMainAttributes().putValue(entry.getKey(), entry.getValue());
      }

      try (final UnoJarPackager unoJarPackager = new UnoJarPackager(Files.newOutputStream(outputFile.toPath()), mainClass, manifest)) {
        for (final File resolvedArtifact : unoJarResolvedArtifacts) {
          getLogger().info("adding boot classes: {}", resolvedArtifact);
          unoJarPackager.addBootJar(resolvedArtifact);
        }

        if (getMainJar().isPresent()) {
          final File jarOutputFile = getMainJar().get().getAsFile();
          getLogger().info("adding main JAR: {}", jarOutputFile.getName());
          unoJarPackager.addMainJar(jarOutputFile);
        }

        for (final ArtifactDetails resolvedArtifact : runtimeResolvedArtifacts) {
          getLogger().info("adding lib JAR: {}", resolvedArtifact.getId());
          String group = resolvedArtifact.getGroup();
          String name = resolvedArtifact.getName();
          if (group != null && name != null) {
            unoJarPackager.addLibJar(resolvedArtifact.getFile(), String.format("%s/%s/%s", resolvedArtifact.getGroup(), resolvedArtifact.getName(), resolvedArtifact.getFile().getName()));
          } else {
            unoJarPackager.addLibJar(resolvedArtifact.getFile());
          }
        }
        for (File jarFile : getExtraJars().getFiles()
            .stream()
            .filter(f -> f.getName().endsWith(".jar"))
            .collect(Collectors.toList())) {
          getLogger().info("adding extra JAR: {}", jarFile.getName());
          unoJarPackager.addLibJar(jarFile);
        }
      }
    }
  }

}