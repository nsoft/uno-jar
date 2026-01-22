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
package com.needhamsoftware.unojar.suite;

import com.needhamsoftware.unojar.VersionInfo;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnoJarTestSuite {
  @TempDir
  Path testProjectDir;

  @BeforeEach
  public void setup() throws IOException {
    // recursive copy of the test project
    Path sourceDir = Paths.get("../test-suite");
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

    File antJar = new File("../ant/build/libs/ant-" + VersionInfo.getVersion() + ".jar").getCanonicalFile();
    // substitute Ant unojar task classpath since there's no such thing as composite builds for Ant
    List<String> antConfig = Files.readAllLines(testProjectDir.resolve("ant-unojar.xml"))
        .stream()
        .map(line -> {
          if (line.contains("<path location=\"${basedir}/../../ant/build/libs/ant-${unojar.version}.jar\"/>")) {
            return "<path location=\"" + antJar.getAbsolutePath() + "\"/>";
          } else {
            return line;
          }
        })
        .collect(Collectors.toList());
    Files.write(testProjectDir.resolve("ant-unojar.xml"), antConfig);
  }

  @Test
  @DisplayName("Executes the Gradle test suite")
  public void runTestSuite() {
    // Run the Gradle build
    BuildResult result = GradleRunner.create()
        .withProjectDir(testProjectDir.toFile())
        .withArguments("-Dgradle.include.dir=" + new File("..").getAbsolutePath(), "-Pant.home=" + System.getProperty("ant.home"), "build")
        .forwardOutput()
        .run();

    for (File file : testProjectDir.toFile().listFiles()) {
      Path maybeDir = file.toPath();
      if (Files.isDirectory(maybeDir)) {
        if (hasBuildFile(maybeDir)) {
          // Verify the result
          String taskPath = ":" + file.getName() + ":build";
          System.out.println("Checking outcome of " + taskPath);
          assertEquals(SUCCESS, result.task(taskPath).getOutcome());
        }
      }
    }

  }

  private static boolean hasBuildFile(Path dir) {
    return Files.isRegularFile(dir.resolve("build.gradle")) ||
        Files.isRegularFile(dir.resolve("build.gradle.kts"));
  }
}