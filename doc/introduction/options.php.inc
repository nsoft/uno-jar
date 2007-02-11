<h2>Options</h2>

<p>To see what options are supported by One-Jar use the <i>--one-jar-help</i> command line option on a One-Jar file:
</p><pre>
$ java -jar one-jar-example-0.96.jar --one-jar-help
One-Jar uses the following command-line arguments
    --one-jar-help    Shows this message, then exits.
    --one-jar-version Shows the version of One-JAR, then exits.
    
One-Jar uses the following VM properties (-D&lt;property&gt;=&lt;true|false|string&gt;)
    one-jar.main-class Specifies the name of the class which should be executed...
    one-jar.record     true:  Enables recording of the classes loaded by the application
    one-jar.jar-names  true:  Recorded classes are kept in directories corresponding to their jar names.
                       false: Recorded classes are flattened into a single directory...
    one-jar.verbose    true:  Print verbose classloading information
    one-jar.info       true:  Print informative classloading information
</pre>

<p>To see what the <code>JarClassLoader</code> is behind the scenes, enable verbose output using the
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

