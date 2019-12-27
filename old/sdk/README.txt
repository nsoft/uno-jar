One-Jar SDK.
------------

This is the One-Jar SDK, it shows how to layout and build a simple One-Jar project.
The SDK is delivered as a self-expanding One-Jar: run "java -jar one-jar-sdk-<version>.jar"
in a working location, the SDK will be expanded into a directory tree:

  one-jar-sdk
  + com/simontuffs/onejar  -- The One-Jar bootstrap and classloader .class files
  + src/lib                -- Source code for an example library class
  + src/main               -- Source code for an example main class
  + src/install            -- Source code for the SDK installer itself.  This just
                              prints out that the SDK has been installed, since the 
                              actual installation is handled by the One-Jar Expand Mechanism.
                              
  - build.xml              -- A build script which can rebuild an example "hello" and also
                              the original SDK one-jar.
  
Take some time to read and understand the build.xml file, in particular the "hello" target
which builds. You can use this as a starting point for your own One-Jar.

Also look at the "sdk" target, which shows how the one-jar-sdk was built.

After reading build.xml, verify that everything worked as follows:

  $ ant hello
  $ java -jar hello.jar
  
Then rebuild the SDK and re-expand it:

  $ ant sdk
  $ java -jar sdk.jar
  
A new copy of the SDK should be present under one-jar-sdk-<version>.