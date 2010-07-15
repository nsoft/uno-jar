<h2>Opening the JAR</h2>
One-JAR uses a classloader which knows how to load classes and resources from Jar files inside a Jar file.  To help provide some
structure to the classloading process, the One-JAR <code>JarClassLoader</code> looks for a main program inside
a <code>main</code> directory in the Jar file, and looks for supporting Jar files inside a <code>lib</code> directory.
Here is what a candidate Jar file would look like set up to run under One-JAR:

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
change the top-level <code>Main-Class</code> to point to the bootstrap classloader from the One-JAR package, 
and then point to main class with a new attributes <code>One-Jar-Main-Class</code>.   Don't worry, there's
an Ant task that will do this, which I'll get to in a moment.  

<p>But first try running
an application build with One-JAR here: <?=hyperlink("downloads", "downloads", "one-jar-example-0.96.jar")?>.
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

