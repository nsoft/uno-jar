<h2>Build Tree</h2>

There are a number of One-JAR Projects in the CVS repository that illustrate various capabilities 
of the product.  The CVS repository can be accessed using the following credentials: 

<pre>
:pserver:anonymous@one-jar.cvs.sourceforge.net:/cvsroot/one-jar
</pre>

You should checkout the top-level <code>one-jar</code> project, and then the projects next to it to study them, 
as siblings in a working directory.

<p/>It is recommended that you checkout the tag <code>version_0_9_7_RC11</code> which corresponds 
to the latest release.

<h3>Alternative to CVS</h3>
If you wish to bypass CVS, you can access the repository using your web-browser here: 

<a href="http://one-jar.cvs.sourceforge.net/viewvc/one-jar/?pathrev=version_0_9_7_RC11">one-jar 0.97-RC11</a>

<p/>You can checkout the entire project tree using the <a href="http://one-jar.cvs.sourceforge.net/viewvc/one-jar/?view=tar&pathrev=version_0_9_7_RC11">Download GNU tarball</a> option.  Size approx 8MB.

<h3>Projects</h3>
Each of the following projects can be built using Ant.  The one-jar project is pre-built, you should
be able to build the other projects without rebulding it.
<ul>
<li>one-jar -- The main one-jar project, containing all source and build code for the product.</li>
<li>one-jar-appgen -- The application generator: use this to create your own One-JAR project starting-points</li>
<li>one-jar-args -- A simple example showing the use of main-program arguments in the MANIFEST</li>
<li>one-jar-dll -- An example (Windows only) using native libraries</li>
<li>one-jar-guice -- A simple example running the Guice framework</li>
<li>one-jar-inversion -- An example which exercises classloader "inversion", where an external Jar 
calls back into the One-JAR classloader</li>
<li>one-jar-javehelp -- An example using Javahelp.</li>
<li>one-jar-jdbc -- An example showing the use of external JDBC drivers</li>
<li>one-jar-log4j -- An example showing how to use an external log4j configuration file</li>
<li>one-jar-maven -- An example showing the use of the one-jar-maven plugin</li>
<li>one-jar-spring -- An example showing the Spring framework</li>
</ul>