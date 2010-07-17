<?php 
	#$downloads="http://prdownloads.sourceforge.net/one-jar";
	#$downloads="downloads";

	#$root="http://one-jar.cvs.sourceforge.net/*checkout*/one-jar/dist";
	#$root="http://one-jar.cvs.sourceforge.net/*checkout*/one-jar/one-jar/dist";
	$root="https://sourceforge.net/projects/one-jar/files/one-jar/one-jar-0.97";
	
	$version = "-0.97";
	
	function download($root, $file) {
		global $version;
		return "<a href='$root/$file$version.jar/download'>$file$version.jar</a>";
	}
	

?>
<h2>Downloads</h2>
The following downloads are available in the distribution:
<br/><br/>
<table border="1" cellspacing="0" width="90%" align="center" cellpadding="5" style="background-color:lightgoldenrodyellow">
	<tr><th>Download</th><th>Description</th><th>Executable?</th>
    <tr>
        <td width='30%'><?= download($root, "one-jar-appgen")?></td> <td>The One-JAR Application Generator, delivered as a runnable One-JAR application.  Requires Ant for rebuild.  Contains source-code for the example application, and everything Ant that you need to start building testable One-JAR applications with/without Eclipse.</td><td>Yes</td>
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
    <tr>
        <td width='30%'><?= download($root, "one-jar-sdk")?></td> <td>(Deprecated) The Software Developer Kit, delivered as a self-extracting One-JAR application.  Requires Ant for rebuild.  Contains source-code for the example application, and everything Ant that you need to start building One-JAR applications.</td><td>Yes</td>
    </tr>
		
</table>
<p/>
<div class="caution">
Note: these executable Jar files are command-line tools: do not try to execute them on Windows by double-clicking
the file since you may get unexpected results (silent execution).  Open a command shell and execute them
using the <code>java -jar</code> command.  
<p>If you wish to double-click and launch a 
<code>.jar</code> file as a command-line window you can apply the following file-association to your Windows system.  With Administrator privileges:
<pre>
C:\> assoc .jar=jarfile
C:\> ftype jarfile=cmd.exe /k java.exe -jar "%1" %*
</pre>
This basically associates .jar files with java.exe through a command window which will remain
on exit.  Alternatively, to bypass the command window:
<pre>
C:\> assoc .jar=jarfile
C:\> ftype jarfile=javaw.exe -jar "%1" %*
</pre>
  
<p/>You might also find the <a href="http://www.jonelo.de/java/jarfix/">JarFix</a> tool to be useful.
</div>

<h3>Which One Do I Need?</h3>
<ul>
<li>For a quick demo of One-JAR in action, download and run <?=download($root, "one-jar-example")?>.
Run using <code>java -jar</code></li>
<li>If you are ready to start developing a One-JAR archive, download and run <?=download($root, "one-jar-appgen")?>.  Run using <code>java -jar</code>. This 
will generate an outline One-JAR Eclipse/Ant project. You will also need to have access to Ant 1.8.0 or later to build the example.
</li>
<li>Once you are up-to-speed with One-JAR, the <?=download($root, "one-jar-ant-task")?> 
contains just the Java class for the <code>&lt;one-jar&gt;</code> Ant task, as well as a 
taskdef.  (This file is also delivered with the One-JAR Appgen).</li>
<li>The minimum set of Java sources and pre-compiled JDK 1.4 classes needed to build a One-JAR archive by hand are contained in 
<?=download($root, "one-jar-boot")?>.
One-JAR is very compact: the class files have an uncompressed footprint of around 70KB.
</ul>

