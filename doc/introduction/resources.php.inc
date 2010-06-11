<h2>Resource Loading</h2>
There are a number of ways that Java supports finding and opening resources:
<ul>
    <li>URL findResource(String resource): returns a resource, or null if not found.  </li>
    <li>Enumeration findResources(String resource): returns an enumeration of resource URL's which
    match the given string </li>
    <li>URL.openConnection(): opens a connection to a resource by its URL.</li>
</ul>

One-Jar supports all of these mechanisms within the context of a One-Jar file, which may 
contain Jar files that have duplicate resources.  

<h3>Resource Ordering</h3>
One-Jar defines a predictable order for classloading:
<ol>
  <li>The one-jar file</li>
  <li>main/main.jar</li>
  <li>lib/*.jar</li>
</ol>
Note: this assumes that the &lt;one-jar&gt; Ant task is used to construct the One-Jar.  Otherwise, embedded Jar resources are loaded
in the order they appear in the "jar -tvf" listing. 

<h3>Duplicate Resources</h3>
Every Jar file has a required <code>META-INF/MANIFEST</code> file.  With more than one Jar 
inside a One-Jar executable there will therefore be duplicates.  Fortunately, this file is not a problem, 
since a normal Java application usually has many Jar files (or other codebases) on its classpath, 
meaning that it must use scanning to locate its own <code>MANIFEST</code> resources.  One-Jar will
have no impact in this situation.

<h3>An Example: Log4j</h3>
<p/>When multiple Jar files contain a resource with the 
same name, e.g. a <code>log4j.properties</code> configurator for the Log4J logging subsystem,
there may be problems.  Log4j is designed to process only the first log4j.properties file which it finds on the
classpath, and ignores all others. 

<p/>
Consider a One-Jar which is composed of main.jar, library-a.jar, and library-b.jar.  Suppose
that each of these Jar files contains a log4j.properties file.  Whichever Jar file initializes
the Log4j subsystem by invoking Logger.getLogger(Class) will trigger the loading of log4j.properties.
Which one will be chosen depends on the order that the JarClassLoader encountered them
during the One-Jar bootstrap process.  Fortunately this is well defined, the One-Jar order is:

<code>
    main/main.jar, lib/library-a.jar, lib/library-b.jar
</code>

assuming that library-a.jar was ahead of library-b.jar in the Jar entry list (also the Ant &lt;lib&gt; list)

<p>
Consequently, to ensure proper initialization, just make sure that there is a <code>log4j.properties</code>
file in the <code>main/main.jar</code>.  Or, if main.jar is already assembled and you don't
wish to rebuild it, simply put a log4j.properties file in the top-level One-Jar like so:

<pre>
    &lt;one-jar destfile="build/${test}-one-jar-log4j.jar">
        &lt;file include="log4j.properties"/>
        &lt;manifest>
    ... etc.
</pre>

Since One-Jar delegates to the parent classloader which loads the One-Jar file, any top-level resources
(or classes) will take precedence over those in the bundled Jar files.
 