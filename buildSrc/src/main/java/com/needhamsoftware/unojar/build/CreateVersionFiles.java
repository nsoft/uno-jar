package com.needhamsoftware.unojar.build;/*
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

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class CreateVersionFiles extends DefaultTask {
  @Input
  public abstract Property<String> getVersion();

  @Input
  public abstract Property<String> getPackageName();

  @OutputDirectory
  public abstract DirectoryProperty getSourcesOutputDirectory();

  @OutputDirectory
  public abstract DirectoryProperty getResourcesOutputDirectory();

  @TaskAction
  public void createVersionFile() throws IOException {
    Path outputFile = getResourcesOutputDirectory().getAsFile().get().toPath().resolve(".version");
    String version = getVersion().get();
    try (var writer = Files.newBufferedWriter(outputFile)) {
      writer.write(version);
    }
    Path outputSourceDir = getSourcesOutputDirectory().getAsFile().get().toPath();
    String packageName = getPackageName().get();
    Path packageDir = outputSourceDir.resolve(packageName.replace('.', '/'));
    Files.createDirectories(packageDir);
    Path sourceFile = packageDir.resolve("VersionInfo.java");
    try (var writer = Files.newBufferedWriter(sourceFile))
    {
      writer.write("package " + packageName + ";\n");
      writer.write("\n");
      writer.write("/**\n");
      writer.write(" * Version information about this module.\n");
      writer.write(" */\n");
      writer.write("public class VersionInfo {\n");
      writer.write("    public static String getVersion() {\n");
      writer.write("        return \"" + version + "\";\n");
      writer.write("    }\n");
      writer.write("}\n");
    }
  }
}