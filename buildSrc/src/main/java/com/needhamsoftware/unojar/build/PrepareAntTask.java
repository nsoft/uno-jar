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
package com.needhamsoftware.unojar.build;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ArchiveOperations;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

@CacheableTask
public abstract class PrepareAntTask extends DefaultTask {
  @Input
  public abstract Property<String> getAntVersion();

  @Internal
  public abstract Property<String> getMirror();

  @OutputDirectory
  public abstract DirectoryProperty getInstallDirectory();

  @Inject
  protected abstract ArchiveOperations getArchiveOperations();

  @Inject
  protected abstract FileOperations getFileOperations();

  @TaskAction
  public void installAnt() throws IOException {
    String version = getAntVersion().get();
    String mirror = getMirror().get();
    // download Ant zip
    var url = new URL(mirror + "/ant/binaries/apache-ant-" + version + "-bin.zip");
    // download zip to tmp dir
    var tmpDir = getTemporaryDir().toPath();
    var zipFile = tmpDir.resolve("apache-ant-" + version + "-bin.zip");
    try {
      try (InputStream inputStream = url.openStream()) {
        Files.copy(inputStream, zipFile);
      } catch (IOException e) {
        throw new RuntimeException("Failed to download Ant", e);
      }
      // extract zip to install dir
      getFileOperations().copy(spec -> {
        spec.from(getArchiveOperations().zipTree(zipFile));
        spec.into(getInstallDirectory());
      });
      // At this stage, Ant is installed under a subdirectory, we need to move it up
      var explodedDir = "apache-ant-" + version;
      getFileOperations().copy(spec -> {
        spec.from(getInstallDirectory().dir(explodedDir));
        spec.into(getInstallDirectory());
      });
      getFileOperations().delete(getInstallDirectory().dir(explodedDir));
    } finally {
      // delete zip file on exit
      Files.deleteIfExists(zipFile);
    }
  }

}