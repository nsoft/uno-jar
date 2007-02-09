<?php 
	include("header.php");
	#$downloads="http://prdownloads.sourceforge.net/one-jar/";
	$downloads="downloads/";
?>
<h2>Downloads</h2>
The following downloads are available in the distribution:
<p/>
<table border="1" cellspacing="0" width="90%" align="center" cellpadding="5" bgcolor="lightgoldenrodyellow">
	<tr><th>Download</th><th>Description</th><th>Executable?</th>
	<tr>
		<td width="30%"><a href="<?=$downloads?>one-jar-sdk-0.96.jar">one-jar-sdk-0.96.jar</a></td><td>The Software Developer Kit, delivered as a self-extracting One-JAR application.  Requires Ant for rebuild.</td><td>Yes</td>
	</tr>
	<tr>
		<td width="30%"><a href="<?=$downloads?>one-jar-example-0.96.jar">one-jar-example-0.96.jar</a></td><td>A test-case for various scenarios using One-JAR to load resources, URL's and classes.  Contains source-code for the One-JAR bootstrap classes</td><td>Yes</td>
	</tr>
	<tr>
		<td width="30%"><a href="<?=$downloads?>one-jar-ant-task-0.96.jar">one-jar-ant-task-0.96.jar</a></td><td>The Ant taskdef code to support a &lt;one-jar&gt; task.  Also contains XML definitions for the Ant taskdef, and a macro version of a one-jar builder.</td><td>No</td>
	</tr>
	<tr>
		<td width="30%"><a href="<?=$downloads?>one-jar-boot-0.96.jar">one-jar-boot-0.96.jar</a></td><td>The low-level <code>JarClassLoader</code> and other one-jar bootstrap mechanisms (source included).</td><td>No</td>
	</tr>
		
</table>
<p class="caution">
Note: these are command-line tools: do not try to execute them on Windows by double-clicking
the file since you may get unexpected results.  Open a DOS command shell and execute them
using the <code>java -jar</code> command.
</p>
<h3>Which One Do I Need?</h3>
<ul>
<li>For a quick demo of One-JAR in action, download and run <a href="http://prdownloads.sourceforge.net/one-jar/one-jar-examples-0.96.jar">one-jar-examples-0.96.jar</a>.
Run using <code>java -jar one-jar-examples-0.96.jar</code></li>
<li>If you are ready to start developing a One-JAR archive, download and run <a href="http://prdownloads.sourceforge.net/one-jar/one-jar-sdk-0.96.jar">one-jar-sdk-0.96.jar</a>.  This is 
a self-extracting archive, so choose carefully where you run it.  It contains everything you need to build and test
a simple "Hello One-JAR" example that demonstrates the use of <code>/main/main.jar</code> and <code>/lib</code>
libraries.  You will also need to have access to Ant 1.6.5 or later to build the example.
The SDK can rebuild itself.</li>
<li>Once you are up-to-speed with One-JAR, the <a href="http://prdownloads.sourceforge.net/one-jar/one-jar-ant-task-0.96.jar">one-jar-ant-task-0.96.jar</a> 
contains just the Java class for the <code>&lt;one-jar&gt;</code> Ant task, as well as a 
taskdef.  (This file is also delivered with the SDK).</li>
<li>The minimum set of Java sources and pre-compiled JDK 1.4 classes needed to build a One-JAR archive by hand are contained in <a href="http://prdownloads.sourceforge.net/one-jar/one-jar-boot-0.96.jar">one-jar-boot-0.96.jar</a>.
One-JAR is very compact: the class files have an uncompressed footprint of around 40KB.
</ul>

<?php include("footer.php") ?>
