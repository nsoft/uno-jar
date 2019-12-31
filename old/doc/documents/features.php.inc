<h2>One-JAR Key Features</h2>
The following features differentiate One-JAR from other similar solutions or alternative
approaches to delivering Java applications in a single file.

<ul>
	<li>Self-contained archives.
		<div class="listindent">Conventional installers unpack supporting Jar-files into
		the file-system, then rely on the Java Classpath to run an application with shell script wrappers.  
		One-JAR archives are self-contained, with no potential Classpath problems or
		versioning problems on target systems</div>
	</li>
	<li>No temporary files are created during runtime execution. 
		<div class="listindent">Unlike other single Jar-file approaches (which unpack supporting Jar files into
		temporary files in the file-system), One-JAR reads nested Jar files directly from the One-JAR archive.  This allows
		One-JAR applications to run from read-only filesystems, and provides much faster
		startup and shutdown times.</div>
	</li>
	<li>Duplicate class/resource safety.
		<div class="listindent">One-JAR checks classes and resources from supporting Jar 
		files and will warn if duplicates are detected.  Such warnings can save hours
		of debugging puzzling NoSuchMethodError problems etc.</div>
	</li>
	<li>Supports Jar-local resources.
		<div class="listindent">One-JAR allows each supporting Jar file to read resources 
		as if it were the only definer of such information.
		Other approaches take a "first on the classpath wins" approach to the definition of
		resources, which can cause problems when resources attributes are used to
		define commonly used information such as the state of logging mechanisms.</div>
	</li>
</ul>