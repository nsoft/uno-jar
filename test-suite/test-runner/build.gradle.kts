import org.gradle.api.tasks.testing.logging.*

plugins {
    java
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

tasks.test {
    dependsOn(
        ":groovy-dsl:build",
        ":kotlin-dsl:build",
        ":test-main:build",
        ":test-main-dir-with-slash:build",
        ":test-log4j-plugin:build",
        ":test-log4j-mr-jar:build"
    )
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
    }
}
