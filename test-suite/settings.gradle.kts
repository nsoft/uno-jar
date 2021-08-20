rootDir.listFiles()?.filter { f ->
    f.isDirectory && (f.name != "buildSrc")
            && (File(f, "build.gradle").isFile || File(f, "build.gradle.kts").isFile)
}?.forEach { f ->
    include(f.name)
}

pluginManagement {
    includeBuild("..")
}
