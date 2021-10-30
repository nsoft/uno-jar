# Building

This project contains two builds. The first is a usual sort of build, the second is a composite build relying on
artifacts from the main build. This should work out of the box with the ./gradlew command, however if you wish to use
an ide such as intellij to work on this code it may be necessary to add /test-suite/build.gradle to the project
manually to see it in the gralde side bar. This is entirely optional however as the main build runs the test-suite
build as a dependency for the test target. 
