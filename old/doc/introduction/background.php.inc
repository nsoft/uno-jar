<h3>Background</h3>
<p/>    The Java Runtime Environment supports running a Jar file using the
following syntax:
<pre>
java -jar <i>jarname.jar</i>
</pre>
The only requirement of the <i>jarname.jar</i> file is that it contains a manifest attribute
with the key <code>Main-Class</code> a main class to run, for instance in the <code>META-INF/MANIFEST.MF</code>
file:

<pre>
Main-Class: com.mydomain.mypackage.Main
</pre>

Any non-trivial Java application 
is going to rely on any number of supporting Jar files.  For example, using the Apache
Commons Logging capabilty to do logging an application will need to have the <code>commons-logging.jar</code>
file on its classpath.
Most developers reasonably assume that putting a dependency Jar file into their own Jar file, and adding a <code>Class-Path</code>
attribute to the <code>META-INF/MANIFEST</code> will do the trick:

</p><pre>
jarname.jar
| /META-INF
| |  MANIFEST.MF
| |    Main-Class: com.mydomain.mypackage.Main
| |    <i>Class-Path: commons-logging.jar</i>
| /com/mydomain/mypackage
| |  Main.class
| commons-logging.jar
</pre>
Unfortunately this is does not work.  The Java <code>Launcher$AppClassLoader</code> does not know how to load classes from a Jar 
inside a Jar with this kind of <code>Class-Path</code>.  Trying to use <code>jar:file:jarname.jar!/commons-logging.jar</code> also 
leads down a dead-end.  This approach will only work if you install (i.e. scatter) the
supporting Jar files into the directory where the <code>jarname.jar</code> file is installed.  

<p> Another
approach is to unpack all dependent Jar files and repack them inside the <code>jarname.jar</code> file.
This approach tends to be fragile and slow, and can suffer from duplicate resource issues.

