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

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractFunctionalTest {
  @TempDir
  protected Path testProjectDir;

  protected abstract String getProjectName();

  protected BuildResult packageUnoJar() {
    return GradleRunner.create()
        .withProjectDir(testProjectDir.toFile())
        .withArguments("packageUnoJar")
        .withPluginClasspath()
        .withDebug(true)
        .forwardOutput()
        .run();
  }

  @BeforeEach
  public void setup() throws IOException {
    // recursive copy of the test project
    Path sourceDir = Paths.get("../" + getProjectName());
    Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path targetFile = testProjectDir.resolve(sourceDir.relativize(file));
        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path targetDirPath = testProjectDir.resolve(sourceDir.relativize(dir));
        if (Files.notExists(targetDirPath)) {
          Files.createDirectories(targetDirPath);
        }
        return FileVisitResult.CONTINUE;
      }

    });
    URL pluginClasspathUrl = Thread.currentThread().getContextClassLoader().getResource("plugin-under-test-metadata.properties");
    Properties props = new Properties();
    try (InputStream stream = pluginClasspathUrl.openStream()) {
      props.load(stream);
    }
    List<String> files = Arrays.asList(((String) props.get("implementation-classpath")).split(":"));
    Path coreJar = files.stream()
        .filter(f -> f.contains("libs/core"))
        .findFirst()
        .map(Paths::get)
        .orElseThrow(() -> new IllegalStateException("Core jar not found"));
    // append to build.gradle
    String repositories = "repositories {\n" +
        "    ivy {\n" +
        "        url '" + coreJar.getParent() + "'\n" +
        "        patternLayout {\n" +
        "            artifact '[artifact]-[revision](-[classifier]).[ext]'\n" +
        "        }\n" +
        "        metadataSources {\n" +
        "            artifact()\n" +
        "        }\n" +
        "    }\n" +
        "}\n";
    Path buildFile = testProjectDir.resolve("build.gradle");
    List<String> strings = Files.readAllLines(buildFile);
    Files.write(buildFile, Stream.concat(strings.stream(), Stream.of(repositories)).collect(Collectors.toList()));
  }

}