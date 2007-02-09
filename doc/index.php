<?php include("header.php"); ?>
<h2>Introduction</h2>
Java developers often come to a point where they wish to deliver their application 
as a single Jar file.  The Java Runtime Environment supports running a Jar file using the
following syntax:
<pre>
java -jar <i>jarname.jar</i>
</pre>
The only requirement of the <i>jarname.jar</i> file is that it contains a manifest attribute
with the key <code>Main-Class</code> a main class to run.  Suppose the application entry point is the class
<code>com.mydomain.mypackage.Main</code>.  Add the following line to the <code>META-INF/MANIFEST.MF</code>
file:

<pre>
Main-Class: com.mydomain.mypackage.Main
</pre>

So far so good.  But, here's where the problems usually start.  Any non-trivial Java application 
is going to rely on any number of supporting Jar files.  For example, using the Apache
Commons Logging capabilty to do logging an application will need to have the <code>commons-logging.jar</code>
file on its classpath.
<p>
Most developers reasonably assume that putting such a Jar file into their own Jar file, and adding a <code>Class-Path</code>
attribute to the <code>META-INF/MANIFEST</code> will do the trick:

</p><pre>
jarname.jar
| /META-INF
| | MANIFEST.MF
| |  Main-Class: com.mydomain.mypackage.Main
| |  Class-Path: commons-logging.jar
| /com/mydomain/mypackage
| | Main.class
| commons-logging.jar
</pre>
Unfortunately this is not the case.  The Java classloader does not know how to load classes from a Jar inside a Jar.
The entries on the <code>Class-Path</code> must be references to files outside the Jar file, defeating the goal of 
delivering an application in a single Jar file.

<a name="opening">
<h2>Opening the JAR  <a style="font-size:70%" href="#home">home</a></h2>
One-Jar uses a classloader which knows how to load classes and resources from Jar files inside a Jar file.  To help provide some
structure to the classloading process, the One-Jar <code>JarClassLoader</code> looks for a main program inside
a <code>main</code> directory in the Jar file, and looks for supporting Jar files inside a <code>lib</code> directory.
Here is what a candidate Jar file would look like set up to run under One-Jar:

<pre>
jarname.jar
| /META-INF
| | MANIFEST.MF
| | | Main-Class: com.simontuffs.onejar.Boot
| | | One-Jar-Main-Class: com.mydomain.mypackage.Main
| /main
| | main.jar
| | | com.mydomain.mypackage.Main.class
| /lib
| | commons-logging.jar
| /com.simontuffs.onejar
| | Boot.class
| | etc.
| 
</pre>
That's pretty much all there is to it.  Wrap the main class in a file called <code>main.jar</code>, put it in a JAR folder
called <code>main</code>, likewise put supporting Jar files under a folder called <code>lib</code>, 
change the top-level <code>Main-Class</code> to point to the bootstrap classloader from the One-Jar package, 
and then point to main class with a new attributes <code>One-Jar-Main-Class</code>.   Don't worry, there's
an Ant task that will do this, which I'll get to in a moment.  But first try running
an application build with One-Jar <a href="http://prdownloads.sourceforge.net/one-jar/one-jar-example-0.96.jar?download">here</a>.
Download this application and run it using <code>java -jar one-jar-example-0.96.jar</code>.  Then try it with 
verbose mode: <code>java -Done-jar.verbose=true -jar one-jar-example-0.96.jar</code> to get a detailed look at what's
going on in the classloader.

<p>The demo will produce an output similar to this:
</p><pre>
$ java -jar one-jar-example-0.96.jar
Main: com.simontuffs.onejar.example.main.Main.main()
Test: loaded by com.simontuffs.onejar.DetectClassLoader@7a84e4
Test: codesource is jar:file:/C:/work/eclipse-workspaces/workspace-simontuffs/one-jar/dist/...
Test: java.class.path=one-jar-example-0.96.jar
Util: loaded by com.simontuffs.onejar.DetectClassLoader@7a84e4
Util.sayHello()
... etc.
</pre>

<a name="options">
<h2>Options  <a style="font-size:70%" href="#home">home</a></h2>

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

<a name="gettingstarted"/>
<h2>Getting Started  <a style="font-size:70%" href="#home">home</a></h2>
To get started building an application using One-Jar, download the <a href="http://prdownloads.sourceforge.net/one-jar/one-jar-sdk-0.96.jar?download">One-Jar Software Developers Kit</a>.
The kit is a One-Jar executable, which contains all the pieces needed to assemble One-Jar applications (and can also re-assemble itself).  When 
run, should see the following output:
<pre>
$ java -jar one-jar-sdk-0.96.jar
Extracting SDK... done.  
To build the "Hello One-Jar" example: $ ant hello
To run the "Hello One-Jar" example:   $ java -jar hello.jar
To rebuild the SDK:                   $ ant sdk
</pre>
This extracts a development project (also an Eclipse JDT project) which contains a directory
tree suitable for building a One-Jar application.  
<p>Note: make sure to download an Ant distribution in order to be able to rebuild the <code>hello.jar</code>
or the <code>sdk</code>.  You can obtain the latest version of ant here:
<a href="http://ant.apache.org/bindownload.cgi">http://ant.apache.org/bindownload.cgi</a>.  Alternatively
run Ant from within an IDE such as Eclipse.  You will also need a Java Runtime JRE 1.4 or later.

</p><p><code>$ ant hello</code> builds a simple One-Jar Jar file containing a single Main class, and a single Hello
class.  The Main class is bundled into <i>main.jar</i>, the Hello class is bundled into <i>hello.jar</i>, 
and the requisite One-Jar jar-file is constructed.  Main invokes Hello(), which prints "Hello One-Jar".

</p><p>Use this project as a starting point for your own development, by editing the <code>hello/build.xml</code> file.

<a name="manifest"/>
<h2>Manifest Attributes  <a style="font-size:70%" href="#home">home</a></h2>
One-JAR uses manifest attributes to control its operation.  The purpose of these attributes
is described in the following table.  The manifest file paths are relative to the top of the One-JAR archive.
<p/>
<table border="1" cellspacing="0" width="90%" align="center" cellpadding="5" bgcolor="lightgoldenrodyellow">
<tr><th>Attribute</th><th>Manifest</th><th>Description</th></tr>
<tr><td>One-Jar-Main-Class</td><td>META-INF/MANIFEST.MF</td><td>Optional: specifies the main class instead of looking in <code>main/main.jar</code></td></tr>
<tr><td>Main-Class</td><td>/main/main.jar<br/>&nbsp;&nbsp;/META-INF/MANIFEST.MF</td><td>Specifies the main class inside <code>main.jar</code>.  You can use the One-Jar-Main-Class attribute instead. </td></tr>
<tr><td>One-Jar-Expand</td><td>META-INF/MANIFEST.MF</td><td>Optional: specifies which files and/or directories to expand into the filesystem.  Files
can only be expanded into a tree underneath the location where the One-JAR archive is executed.  This is a comma-separated
list of prefixes, e.g. <code>One-Jar-Expand: file.txt,doc/</code> will expand file.txt, and all files in the <code>doc</code> directory.</td></tr>
<tr><td>One-Jar-Show-Expand</td><td>META-INF/MANIFEST.MF</td><td><code>true</code>: show the file expansions.</td></tr>
</table>


<?php include("footer.php") ?>
