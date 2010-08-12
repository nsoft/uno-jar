<h2>Spring Framework Example</h2>
There is a One-JAR Spring example in the CVS repository.

<h3>With CVS</h3>

Attach to the CVS repository using the following credentials:

<pre>
:pserver:anonymous@one-jar.cvs.sourceforge.net:/cvsroot/one-jar
</pre>

Checkout the <code>one-jar</code> and <code>one-jar-spring</code> projects, as siblings in a working directory.

<h3>Without CVS</h3>

Download and expand the following items as siblings in a working directory:

<ul>
<li><a href="http://one-jar.cvs.sourceforge.net/viewvc/one-jar/one-jar/?view=tar&pathrev=version_0_9_7_RC11">one-jar 0.97-RC11</a></li>
<li><a href="http://one-jar.cvs.sourceforge.net/viewvc/one-jar/one-jar-spring/?view=tar&pathrev=version_0_9_7_RC11">one-jar-spring 0.97-RC11</a></li>
</ul>  

Note: these are .tar.gz files, so will need to be unpacked twice to get to the contents.

<h3>Building and Running</h3>
<ol>
<li>Change to the <code>one-jar</code> directory and build using <code>$ ant</code></li>
<li>Change to the <code>one-jar-spring</code> directory and build using <code>$ ant</code></li>
<li>Run the example using <code>$ ant run.one-jar-spring-scan</code></li>
<li>Run a JUnit test case using <code>$ ant test.one-jar-spring-scan</code></li>
<li>Generate an HTML report for the JUnit test using <code>$ ant junit.report</code>. Examine
the report in a web-browser from the <code>test-reports</code> directory.</li>
</ol>

<h3>More Information</h3>
Look over the one-jar-spring project, with attention to the build.xml file.
