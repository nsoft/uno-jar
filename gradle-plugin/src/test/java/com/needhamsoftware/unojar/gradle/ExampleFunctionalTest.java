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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleFunctionalTest extends AbstractFunctionalTest {
  @Override
  protected String getProjectName() {
    return "examples";
  }

  @Test
  @DisplayName("Creates the example jar")
  public void testCreateExampleJar() throws IOException {
    BuildResult result = packageUnoJar();

    assertEquals(SUCCESS, result.task(":packageUnoJar").getOutcome());
    Path unojarFile = testProjectDir.resolve("build/libs/uno-jar-examples-unojar.jar");
    assertTrue(Files.exists(unojarFile));

    String exportDirectory = System.getProperty("export.example.jar.dir");
    if (exportDirectory != null) {
      System.out.println("Copying example jar to " + exportDirectory);
      Files.createDirectories(Paths.get(exportDirectory));
      Path targetJar = Paths.get(exportDirectory).resolve(unojarFile.getFileName());
      Files.deleteIfExists(targetJar);
      Files.copy(unojarFile, targetJar);
    }
  }

}