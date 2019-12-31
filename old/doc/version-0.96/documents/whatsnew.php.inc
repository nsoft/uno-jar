<h2>What's New?</h2>
<h3>One-JAR version 0.96</h3>
<ul>
<li>Build automation with Ant build taskdef and scripts</li>
<li>Streamlined SDK for quick start</li>
<li>Support for MIME content-type based on file-types for URL connections</li>
<li>Support for JNI native librarys (e.g. SWT).</li>
<li>Support for Java Security Manager</li>
<li>Extension of the runtime classpath to allow external JAR files using <?=hyperlink("introduction", "options", "one-jar.class.path")?> VM property</li>
<li>Extensive JUnit regression tests</li>
</ul>

<h3>Release Notes</h3>
One-JAR is pre-release software, hence the release version of 0.96 which is slightly less than 1.0.  
Consequently, some minor changes have occurred, which may cause minor inconvenience to developers
who have used the 0.95 release.  

<p>One-JAR, being open-source software, is better suited by a continuous-release
model to ensure timely updates.  This is handled by building in version information into the One-JAR archives, including the SDK
and examples jar files.  Any One-JAR executable can be made to dump its release information using a
command-line switch:
<pre>
$ java -jar one-jar-example-0.96.jar --one-jar-version
One-JAR version 0.96-rc5-20070916-1909</pre>

<p>If you encounter a problem with One-JAR the first step should be to make sure you have the latest version of the 
software.  The release string corresponds to the release-candidate version (rc5) and the build-time
(September 16, 2007, 7:09pm) in this example.
<p>Note: the version you download will not have the release-candidate or build-time in its filename, it will contain 
(as shown here) just the overall version (0.96).

<h3>Changes since 0.95</h3>

<ul>
<li>Some VM properties (which were not publically documented) have changed names to make them
more consitent. These are now publically documented, and should not change in future releases.
<ul>
<li>one-jar.jar-names => one-jar.jar.names</li>
<li>one-jar.main-class => one-jar.main.class</li>
</ul>
