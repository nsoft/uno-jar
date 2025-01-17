package com.needhamsoftware.unojar.gradle;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

public abstract class UnoJarExtension {

  public abstract Property<String> getVersion();

  public abstract Property<String> getArchiveBaseName();

  public abstract Property<String> getArchiveAppendix();

  public abstract Property<String> getArchiveVersion();

  public abstract Property<String> getArchiveClassifier();

  public abstract Property<String> getArchiveExtension();

  public abstract Property<Configuration> getArchives();

  @SuppressWarnings("UnstableApiUsage")
  public abstract MapProperty<String, String> getManifestAttributes();

  public abstract Property<String> getMainClass();

  public abstract ListProperty<ArtifactDetails> getArtifacts();
}