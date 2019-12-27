<?php 
	#$downloads="http://prdownloads.sourceforge.net/one-jar";
	#$downloads="downloads";

	$root="http://one-jar.cvs.sourceforge.net/*checkout*/one-jar/dist";
	$root="http://one-jar.cvs.sourceforge.net/*checkout*/one-jar/one-jar/dist";
	
	$version = "-0.96";
	
	function download($root, $file) {
		global $version;
		return "<a href='$root/$file$version.jar'>$file$version.jar</a>";
	}
	

?>
<h2>Downloads</h2>
The following downloads are available in the distribution:
<br/><br/>
<table border="1" cellspacing="0" width="90%" align="center" cellpadding="5" style="background-color:lightgoldenrodyellow">
	<tr><th>Download</th><th>Description</th><th>Executable?</th>
	<tr>
		<td width='30%'><?= download($root, "one-jar-sdk")?></td> <td>The Software Developer Kit, delivered as a self-extracting One-JAR application.  Requires Ant for rebuild.  Contains source-code for the example application, and everything Ant that you need to start building One-JAR applications.</td><td>Yes</td>
	</tr>
	<tr>
		<td width='30%'><?= download($root, "one-jar-example")?></td> <td>A test-case for various scenarios using One-JAR to load resources, URL's and classes.  Contains source code for the examples.</td><td>Yes</td>
	</tr>
	<tr>	
		<td width='30%'><?= download($root, "one-jar-dll")?></td> <td>An example of using Windows Native Libraries (DLL's) with One-JAR.  Contains C/C++ source code and a mingw make setup.</td><td>Yes</td>
	</tr>
	<tr>
		<td width='30%'><?= download($root, "one-jar-ant-task")?></td> <td>The Ant taskdef code to support a &lt;one-jar&gt; task.  Also contains XML definitions for the Ant taskdef, and a macro version of a one-jar builder.  Contains source code for the Ant task.</td><td>No</td>
	</tr>
	<tr>
		<td width='30%'><?= download($root, "one-jar-boot")?></td> <td>The low-level <code>JarClassLoader</code> and other One-JAR bootstrap mechanisms  Contains source-code for the One-JAR bootstrap classes.</td><td>No</td>
	</tr>
		
</table>
<p class="caution">
Note: the executable Jar files are command-line tools: do not try to execute them on Windows by double-clicking
the file since you may get unexpected results.  Open a DOS command shell and execute them
using the <code>java -jar</code> command.
</p>
<h3>Which One Do I Need?</h3>
<ul>
<li>For a quick demo of One-JAR in action, download and run <?=download($root, "one-jar-example")?>.
Run using <code>java -jar one-jar-example.jar</code></li>
<li>If you are ready to start developing a One-JAR archive, download and run <?=download($root, "one-jar-sdk")?>.  This is 
a self-extracting archive, so choose carefully where you run it.  It contains everything you need to build and test
a simple "Hello One-JAR" example that demonstrates the use of <code>/main/main.jar</code> and <code>/lib</code>
libraries.  You will also need to have access to Ant 1.6.5 or later to build the example.
The SDK can rebuild itself.</li>
<li>Once you are up-to-speed with One-JAR, the <?=download($root, "one-jar-ant-task")?> 
contains just the Java class for the <code>&lt;one-jar&gt;</code> Ant task, as well as a 
taskdef.  (This file is also delivered with the SDK).</li>
<li>The minimum set of Java sources and pre-compiled JDK 1.4 classes needed to build a One-JAR archive by hand are contained in 
<?=download($root, "one-jar-boot")?>.
One-JAR is very compact: the class files have an uncompressed footprint of around 40KB.
</ul>

