<h2>Build Tools</h2>
A One-JAR Archive can be built using only the Java jar tool, if you spend the necessary 
effort to create a directory tree that follows the required structure.  But it is simpler
and easier to use one of the build-tools that know how to build a One-JAR archive.  There are
currently two alternatives.

<h3>Ant: The &lt;one-jar> taskdef</h3>

The &lt;one-jar> Ant task supports building a One-JAR Archive, creating a <code>main/main.jar</code>
from a &lt;main> section, and adding in libraries from a &lt;lib> section.  

See the following sections in this documentation for details. 

<h3>Maven</h3>

There is a one-jar Maven2 SNAPSHOT plugin available at the following repository:

<pre>
&lt;pluginRepository>
    &lt;id>onejar-maven-plugin.googlecode.com&lt;/id>
    &lt;url>http://onejar-maven-plugin.googlecode.com/svn/mavenrepo-snapshot&lt;/url>
&lt;/pluginRepository>
</pre>

There is a one-jar Maven2 release plugin available at the following repository:

<pre>
&lt;pluginRepository>
    &lt;id>onejar-maven-plugin.googlecode.com&lt;/id>
    &lt;url>http://onejar-maven-plugin.googlecode.com/svn/mavenrepo&lt;/url>
&lt;/pluginRepository>
</pre>

The plugin embeds the one-jar-boot-0.97.jar file as a resource.  To determine which version of One-JAR
the plugin is using, examine the contents of the ".version" file in the embedded one-jar-boot
file.  Or alternatvely, build a One-JAR archive and run it with the --one-jar-version argument.

See the following sections in this documentation for details. 
