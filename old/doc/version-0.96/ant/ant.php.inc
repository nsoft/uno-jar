<h2>Building Using Ant</h2>
Creating a One-JAR file requires no special tools: the Java <code>jar</code> command is 
all that is necessary.  However, the hierarchical nature of One-JAR files can prove complex to
construct, even when automated within an Ant script.

<p>There is a simple way: an Ant task called <code>&lt;one-jar&gt;</code> has been created.
This task extends the Ant <code>&lt;jar&gt;</code> task, so it should be immediately familiar
and usable to those experienced with Ant.  To construct a One-JAR file using this task, 
simply include the XML fragment that defines the taskdef, and invoke the target.  

<p>An outline of the <code>&lt;one-jar&gt;</code> task is shown below:

<pre>
    &lt;one-jar destfile=... manifest=...&gt;
    	&lt;main&gt;
    		<span class="note">&lt;filesets&gt; which comprise the source-code of the main application.  
    		Do not put JAR files in here.</span>
    	&lt;/main&gt;
    	&lt;lib&gt;
    		<span class="note">&lt;filesets&gt; containing Jar-files which support the main application.</span>
    	&lt;/lib&gt;
    	&lt;binlib&gt;
    		<span class="note">&lt;filesets&gt; containing native libraries.  Native libraries are expanded into a 
    		temporary directory at runtime: these files are deleted on exit.</span>
    	&lt;/binlib&gt;
    	&lt;wrap&gt;
    		<span class="note">&lt;filesets&gt; containing class-loader wrappers.  These check classloaders 
    		that are loaded from the supporting Jar files to make sure they are compatible 
    		with the One-JAR classloader. This is a complex subject, contact <a href="index.php?page=support&amp;file=support">one-jar support</a> for
    		more information and consulting services if you need to wrap a classloader.
    		You will know you need this if your application does not load classes when
    		you expect it should, from JAR files inside the &lt;lib&gt; directory.</span>
    	&lt;/wrap&gt;
    	&lt;fileset&gt;
    		<span class="note">Any other stuff you want in the top-level One-JAR file.  Remember though, any
    		.class files you put in here will not be able to "see into" JAR files loaded
    		from /main and /lib because their classloader will be the top-level Java 
    		Application Loader, which does not understand One-JAR files (this is how One-JAR 
    		came about in the first place). Contact <a href="index.php?page=support&amp;file=support	">one-jar support</a> if you find 
    		yourself having problems with this kind of issue.</span>
    	&lt;/fileset&gt;
    &lt;/one-jar&gt;
</pre>

