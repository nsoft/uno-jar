<h2>Options and VM Properties</h2>

<p>To see what options are supported by One-JAR use the <i>--one-jar-help</i> command line option on a One-JAR file:
</p><pre>
$ java -jar one-jar-example-0.96.jar --one-jar-help
One-Jar uses the following command-line arguments
    --one-jar-help    Shows this message, then exits.
    --one-jar-version Shows the version of One-JAR, then exits.

One-Jar uses the following VM properties (-D<property>=<true|false|string>)
    one-jar.main.class      Specifies the name of the class which should be executed
                            (via public static void main(String[])
    one-jar.record          true:  Enables recording of the classes loaded by the application
    one-jar.jar.names       true:  Recorded classes are kept in directories corresponding to their jar names.
                            false: Recorded classes are flattened into a single directory.
                            Duplicates are ignored (first wins)
    one-jar.verbose         true:  Print verbose classloading information
    one-jar.info            true:  Print informative classloading information
    one-jar.statistics      true:  Shows statistics about the One-Jar Classloader
    one-jar.jar.path        Full path of the one-jar file being executed.
                            Only needed if java.class.path does not contain the path to the jar, e.g. on Max OS/X.
    one-jar.class.path      Extra classpaths to be added to the execution environment.
                            Use platform independent path separator '|'
    one-jar.expand.dir      Directory to use for expanded files.
    one-jar.show.properties true:  Shows the JVM system properties.</pre>

<p>Version information is presented as a single line response.  For example: <code>One-JAR version 0.96-rc4-20070308-2022</code>
The major version number is 0.96, the minor version number is rc4 (release-candidate 4), built on March 8, 2007 at 2022.

<p>To see what the <code>JarClassLoader</code> is doing behind the scenes, enable verbose output using the
one-jar.verbose system property:
</p><pre>
$ java -Done-jar.verbose=true -jar one-jar-example-0.96.jar
JarClassLoader: One-Jar-Expand=expand,doc,file.txt
JarClassLoader: Info: resource: one-jar-example-0.96.jar!META-INF/MANIFEST.MF
JarClassLoader: cached bytes for class OneJar.class
JarClassLoader: cached bytes for class com.simontuffs.onejar.Boot.class
JarClassLoader: cached bytes for class com.simontuffs.onejar.Handler$1.class
JarClassLoader: cached bytes for class com.simontuffs.onejar.Handler.class
JarClassLoader: cached bytes for class com.simontuffs.onejar.IProperties.class
JarClassLoader: cached bytes for class com.simontuffs.onejar.JarClassLoader$1.class
JarClassLoader: cached bytes for class com.simontuffs.onejar.JarClassLoader$ByteCode.class
JarClassLoader: cached bytes for class com.simontuffs.onejar.JarClassLoader.class
JarClassLoader: Info: resource: one-jar-example-0.96.jar!OneJar.java
JarClassLoader: Info: resource: one-jar-example-0.96.jar!boot-manifest-external.mf
... many more lines until the first line of output from the Main class.
</pre>
This diagnostic output can prove useful when trying to debug class-loading issues.

