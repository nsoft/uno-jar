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
