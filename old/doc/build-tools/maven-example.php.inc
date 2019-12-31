<h2>One-JAR Maven Example Project</h2>

There is a One-JAR Maven Example project available from CVS which can be viewed and downloaded here: 
<a href="http://one-jar.cvs.sourceforge.net/one-jar/one-jar-maven">http://one-jar.cvs.sourceforge.net/one-jar/one-jar-maven</a>

This project is basically "Hello World" for One-JAR built using Maven, using log4j as the dependency jar.  The following steps demonstrate:

<pre>
$ mvn package
$ cd target
$ java -jar one-jar-maven-0.0.1-SNAPSHOT.one-jar.jar
Hello from one-jar-maven
2010-07-14 20:33:13,135 [main] INFO  test.maven.ConfiguredLogging - info from log4j
2010-07-14 20:33:13,139 [main] WARN  test.maven.ConfiguredLogging - warn from log4j
</pre>

Note that there is also a <code>one-jar-maven-0.0.1-SNAPSHOT.jar</code> which is the "normal" executable jar file, minus
dependencies which must be present in the target directory. Try deleting log4j-1.2.14.jar and running both of
the jar files to demonstrate this.

<pre>
$ java -jar one-jar-maven-0.0.1-SNAPSHOT.jar
Exception in thread "main" java.lang.NoClassDefFoundError: org/apache/log4j/Logger
        at com.simontuffs.onejar.test.maven.ConfiguredLogging.<clinit>(ConfiguredLogging.java:13)
Caused by: java.lang.ClassNotFoundException: org.apache.log4j.Logger
        at java.net.URLClassLoader$1.run(URLClassLoader.java:200)
        
</pre>
The "normal" jar fails to run, but the One-JAR still works correctly.
<pre>
$ java -jar one-jar-maven-0.0.1-SNAPSHOT.one-jar.jar
Hello from one-jar-maven
2010-07-14 20:35:15,719 [main] INFO  test.maven.ConfiguredLogging - info from log4j
2010-07-14 20:35:15,723 [main] WARN  test.maven.ConfiguredLogging - warn from log4j        
</pre>