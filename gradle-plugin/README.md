# Gradle plugin

## Basic usage

Kotlin DSL (`build.gradle.kts`)
```
plugins {
    application
    id 'com.needhamsoftware.unojar'
}

application {
    mainClass.set("org.gradle.sample.Main")
}
```

Groovy DSL (`build.gradle`)
```
plugins {
    application
    id("com.needhamsoftware.unojar")
}

application {
    mainClass = 'org.gradle.sample.Main'
}
```

Invocation
```
./gradlew packageUnoJar
```

The output file can be found at `${project.buildDir}/${project.libsDirName}/[archiveBaseName]-[archiveAppendix]-[archiveVersion]-[archiveClassifier].[archiveExtension]`

With default settings: `build/libs/${project.archivesBaseName}-${project.version}-unojar.jar`

## Tasks

### Added tasks

The plugin adds the following tasks to the project.

#### `packageUnoJar` - `com.needhamsoftware.unojar.gradle.PackageUnoJarTask`

* Depends on: `jar`
* Packages an uno-jar.

### Task definitions

#### `com.needhamsoftware.unojar.gradle.PackageUnoJarTask`

Packages an uno-jar.

Properties specified at the task level overrides properties specified in the `unojar` and `application` extensions.

Main artifacts are taken from the output of the direct dependencies (of type `Jar`) of this task.

Kotlin DSL (`build.gradle.kts`)
```
tasks {
    register<com.needhamsoftware.unojar.gradle.PackageUnoJarTask>("packageUnoJar2") {
        dependsOn("jar")
        mainClass.set("Main2")
        archiveBaseName.set("test")
        embedConfiguration.set(configurations.getByName("runtimeClasspath"))
        manifestAttributes.set(mapOf("Test-Attribute" to "Test-Value"))
    }
}
```

Groovy DSL (`build.gradle`)
```
tasks.register("packageUnoJar2", com.needhamsoftware.unojar.gradle.PackageUnoJarTask) {
  dependsOn "jar"
  mainClass = "Main2"
  archiveBaseName = "test"
  embedConfiguration = configurations.getByName("runtimeClasspath")
  manifestAttributes = ["Test-Attribute": "Test-Value"]
}
```


| Name | Type | Required | Description |
| --- | --- | --- | --- |
| `mainClass` | `String` | No | Main class name. | 
| `archiveBaseName` | `String` | No | Archive base name. Default: `project.archivesBaseName` |
| `archiveAppendix` | `String` | No | Archive appendix. |
| `archiveVersion` | `String` | No | Archive version. Default: `project.version` |
| `archiveClassifier` | `String` | No | Archive classifier. Default: `"unojar"` |
| `archiveExtension` | `String` | No | Archive extension. Default: `"jar"` |
| `embedConfiguration` | `org.gradle.api.artifacts.Configuration` | No | Embed configuration. Library artifacts to include in the uno-jar. Default: `configurations.getByName("runtimeClasspath")` |
| `manifestAttributes` | `Map<String, String>` | No | Manifest attributes. |

## Extension

Kotlin DSL (`build.gradle.kts`)
```
# sample configuration, using all properties (including optional ones) 
unojar {
    archiveBaseName.set("myjar")
    archiveClassifier.set("unojar")
    embedConfiguration.set(configurations.getByName("runtimeClasspath")) 
    manifestAttributes.set(mapOf("Test-Attribute" to "Test-Value"))
}
```

Groovy DSL (`build.gradle`)
```
# sample configuration, using all properties (including optional ones) 
unojar {
    archiveBaseName = "myjar"
    archiveClassifier = "unojar"
    embedConfiguration = configurations.getByName("runtimeClasspath")
    manifestAttributes = ["Test-Attribute": "Test-Value"]
}
```

| Name | Type | Required | Description |
| --- | --- | --- | --- |
| `archiveBaseName` | `String` | No | Archive base name. Default: `project.archivesBaseName` |
| `archiveAppendix` | `String` | No | Archive appendix. |
| `archiveVersion` | `String` | No | Archive version. Default: `project.version` |
| `archiveClassifier` | `String` | No | Archive classifier. Default: `"unojar"` |
| `archiveExtension` | `String` | No | Archive extension. Default: `"jar"` |
| `embedConfiguration` | `org.gradle.api.artifacts.Configuration` | No | Embed configuration. Library artifacts to include in the uno-jar. Default: `configurations.getByName("runtimeClasspath")` |
| `manifestAttributes` | `Map<String, String>` | No | Manifest attributes. |