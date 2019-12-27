<h2>Manifest Attributes</h2>
One-JAR uses manifest attributes to control its operation.  The purpose of these attributes
is described in the following table.  The manifest file paths are relative to the top of the One-JAR archive.
<br/><br/>
<table border="1" cellspacing="0" width="90%" align="center" cellpadding="5" style="background-color:lightgoldenrodyellow">
<tr><th width="20%">Attribute</th><th width="30%">Manifest</th><th>Description</th></tr>
<tr><td>One-Jar-Main-Class</td><td>META-INF/MANIFEST.MF</td><td>Optional: specifies the main class instead of looking in <code>main/main.jar</code></td></tr>
<tr><td>One-Jar-Main-Args</td><td>META-INF/MANIFEST.MF</td><td>Optional: specifies default arguments to be passed to the main program, in the absence of any commmand-line args.</td></tr>
<tr><td>Main-Class</td><td>/main/main.jar<br/>&nbsp;&nbsp;/META-INF/MANIFEST.MF</td><td>Specifies the main class inside <code>main.jar</code>.  You can use the One-Jar-Main-Class attribute instead. </td></tr>
<tr><td>One-Jar-Expand</td><td>META-INF/MANIFEST.MF</td><td>Optional: specifies which files and/or directories to expand into the filesystem. This is a comma-separated
list of prefixes, e.g. <code>One-Jar-Expand: file.txt,doc/</code> will expand file.txt, and all files in the <code>doc</code> directory.</td></tr>
<tr>
<td>One-Jar-Expand-Dir</td><td>META-INF/MANIFEST.MF</td><td>Optional: specifies where to expand files.  Default is in the current working directory.  Files can be expanded anywhere: system properties are expanded, e.g. ${user.dir}.  
</td>
</tr>
<tr><td>One-Jar-Show-Expand</td><td>META-INF/MANIFEST.MF</td><td><code>true</code>: show the file expansions.</td></tr>
<tr><td>One-Jar-Confirm-Expand</td><td>META-INF/MANIFEST.MF</td><td><code>true</code>: confirm file expansions by the user.</td></tr>
</table>
