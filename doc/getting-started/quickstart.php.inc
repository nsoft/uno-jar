<h2>Quick Start</h2>

There are various approaches to getting started with One-JAR.  Ant users will find the "Application Generator Approach" most useful, 
Maven users the "Maven Approach", command-line tool users may prefer the "Command Line Approach".

<h3>Application Generator Approach</h3>
This approach provides you with a complete Eclipse/Ant application directory, which you can 
use as a starting point for your own One-JAR application.  The application generator
is driven by a template built into the one-jar-appgen.jar file (see 
<?=hyperlink("getting-started", "one-jar-appgen", "one-jar-appgen")?>)
<ol>
<li>Download <a href="https://sourceforge.net/projects/one-jar/files/one-jar/one-jar-0.97/one-jar-appgen-0.97.jar/download">one-jar-appgen-0.97.jar</a></li>
<li>Generate application, build, and run it.
<p/><code>$ java -jar one-jar-appgen-0.97.jar</code>
<pre>
Enter project path (project name is last segment): c:/tmp/test-one-jar
Enter java package name: com.example.onejar
</pre>
<code>
$ cd c:/tmp/test-one-jar
<br/>$ ant
<br/>$ cd build 
<br/>$ java -jar test-one-jar.jar
</code>
<pre>
test_one_jar main entry point, args=[]
test_one_jar main is running
test_one_jar OK.
</pre>

</li>
</ol>
Add source code to the <code>src</code> directory, library jars to the <code>lib</code> directory, 
and rebuild.
<h3>Command-Line Approach</h3>
The use of Ant is not required: a One-JAR archive is simple to build using just the
<code>jar</code> tool using the following steps.
<ol>
<li>Create a working directory to act as the "root" of the one-jar with <code>main</code>, <code>lib</code> sub-directories.</li>
<li>Copy your main application jar file into <code>root/main</code> and library dependencies into <code>root/lib</code></li>
<li>Unjar the <code>one-jar-boot-0.97.jar</code> file into the root directory, and delete the "src" tree</li>
<li>Edit the boot-manifest.mf file and add a new line: <code>One-Jar-Main-Class: your-main-class</code>
<li>cd root; jar -cvfm ../one-jar.jar boot-manifest.mf .</li>
</ol>
You should end up with a One-JAR archive which mirrors the "root" tree:
<pre>
one-jar.jar
|  META-INF/MANIFEST.MF
|  .version
|  com/simontuffs/onejar
   |  Boot.class, ...etc.
|  doc/one-jar-license.txt
|  main/main.jar
|  lib/a.jar ...etc.
</pre>

Thats it: no code to write, just a directory tree, some copy operations, and a file edit.
The One-JAR classloader discovers the libraries and main code based on their position
in the archive, and ignores any other Jar files should you need to embed archives which should
not be on the classpath.  Embedding the <code>one-jar-license.txt</code> ensures compliance
with the BSD-style license.

<h3>Maven Approach</h3>
There is a Maven2 plugin for One-JAR.  It is easy to use for Maven projects.  Please
consult the documentation here: <a href="http://code.google.com/p/onejar-maven-plugin/">http://code.google.com/p/onejar-maven-plugin/</a>

<h3>Ant Taskdef Approach</h3>
Detailed use of the One-JAR Ant Taskdef is discussed
<?=hyperlink("getting-started", "ant", "here")?>.  Note that the <code>one-jar-appgen</code> approach
uses the Ant taskdef.

<h3>SDK Approach</h3>
Use of the (deprecated) SDK is discussed 
<?=hyperlink("getting-started", "sdk", "here")?>

</h3>