/*
 * Copyright 2003-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.needhamsoftware.unojar.gradle;

import org.gradle.api.artifacts.component.ComponentArtifactIdentifier;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.artifacts.result.ResolvedVariantResult;

import java.io.File;

/**
 * Represents a resolved artifact, required for Gradle configuration
 * cache support.
 */
public class ArtifactDetails {
  private final ComponentArtifactIdentifier id;
  private final ResolvedVariantResult variant;
  private final File file;

  public ArtifactDetails(ComponentArtifactIdentifier id, ResolvedVariantResult variant, File file) {
    this.id = id;
    this.variant = variant;
    this.file = file;
  }

  public ComponentArtifactIdentifier getId() {
    return id;
  }

  public ResolvedVariantResult getVariant() {
    return variant;
  }

  public File getFile() {
    return file;
  }

  public String getGroup() {
    if (id.getComponentIdentifier() instanceof ModuleComponentIdentifier) {
      return ((ModuleComponentIdentifier) id.getComponentIdentifier()).getGroup();
    } else {
      return null;
    }
  }

  public String getName() {
    if (id.getComponentIdentifier() instanceof ModuleComponentIdentifier) {
      return ((ModuleComponentIdentifier) id.getComponentIdentifier()).getModule();
    } else {
      return null;
    }
  }
}