<h2>Quick Start</h2>

There are four alternate paths to getting started with One-JAR.

<h3>Application Generator Path</h3>
This path provides you with a complete Eclipse/Ant application directory, which you can 
use as a starting point for your own One-JAR application.  The application generator
is driven by a template built into the one-jar-appgen.jar file (see 
<?=hyperlink("getting-started", "one-jar-appgen", "one-jar-appgen")?>
<ol>
<li>Download <a href="https://sourceforge.net/projects/one-jar/files/one-jar/one-jar-0.97/one-jar-appgen-0.97.jar/download">one-jar-appgen-0.97.jar</a></li>
<li>Generate application, build, and run it.
<p/><code>$ java -jar one-jar-appgen-0.97.jar
<pre>
Enter project path (project name is last segment): c:/tmp/test-one-jar
Enter java package name: com.example.onejar
</pre>
<code>
$ cd c:/tmp/test-one-jar
<br/>$ ant
<br/>$ cd build 
<br/>$ java -jar test-one-jar.jar
<pre>
test_one_jar main entry point, args=[]
test_one_jar main is running
test_one_jar OK.
</pre>
</code>
</li>
</ol>

<h3>Maven Path</h3>
There is a Maven2 plugin for One-JAR.  It is easy to use for Maven projects.  Please
consult the documentation here: <a href="http://code.google.com/p/onejar-maven-plugin/">http://code.google.com/p/onejar-maven-plugin/</a>

<h3>Ant Taskdef Path</h3>
Use of the One-JAR Ant Taskdef is discussed
<?=hyperlink("getting-started", "ant", "here")?>

<h3>SDK Path</h3>
Use of the (deprecated) SDK is discussed 
<?=hyperlink("getting-started", "sdk", "here")?>

</h3>