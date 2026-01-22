# Test suite project

This test suite is executed directly by Gradle as part of the "test-suite-runner"
subproject, which is going to deal with the setup of the project.

It is not intended to be run directly, but it can, at your own risks. If you do
this, make sure to clean the output directories after testing, or you will break
up-to-date checking of the test suite since you're going to introduce new files
in that directory.