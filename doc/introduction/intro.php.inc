<h3>What is One-JAR?</h3>
<p/>One-JAR lets you package a Java application together with its dependency Jars into a single executable Jar file.  
<h3>Who uses One-JAR?</h3>
A number of <a href="http://www.google.com/search?hl=en&q=%22One-JAR%22+P.+Simon+Tuffs+%28simon%40simontuffs.com%29.+++All+rights+reserved.&btnG=Search&aq=f&aqi=&aql=&oq=&gs_rfai=">commercial and open-source projects</a> have chosen One-JAR as their
packaging mechanism.  Since inception One-JAR has had over <a href="http://sourceforge.net/project/stats/?group_id=111153&ugn=one-jar&type=&mode=alltime">75,000 downloads</a> and shows 
a consistent download profile over time, and is used across a variety of <a href="http://sourceforge.net/downloads/one-jar/stats_os?dates=2010-02-01%20to%202010-07-16">operating-systems</a>

<h3>Where is One-JAR?</h3>
<ul><li><?=hyperlink("downloads", "downloads", "Download it through this page")?></li>
<li><a href="http://sourceforge.net/projects/one-jar/forums">Discussion Forums</a></li>
<li><a href="https://sourceforge.net/tracker/?group_id=111153">Bug Tracker</a></li>
</ul>

<h3>How does One-JAR Work?</h3>
One-JAR provides custom classloader that knows how to load classes and resources from 
a jars inside an archive, instead of from jars in the filesystem.  It discovers dependency jar files based on 
the internal structure of the archive, there is no custom code required to do this.  
One-JAR archives can be constructed using Ant or Maven2.  Your application can be run using 
<code>java -jar my-app.jar</code>

<h3>Support for One-JAR</h3>
An open-source community and professional support are <?=hyperlink("more", "support", "available")?>

<h3>Key Features</h3>
<ul>
    <li>Stateless/Install Free: One-JAR can contain everything needed by an application.</li>
    <li>Friendly: BSD license, no impediments to commercial integration</li>
    <li>Easy: One-JAR tools for Ant & Maven: an SDK and an application generator</li>
    <li>Supports: Spring 3.0.2 and Guice 2.0 application frameworks</li>
    <li>Invisible: One-JAR works transparently, intercepting resource and class loading and redirecting them back inside the One-JAR</li>
    <li>Clean delegation model, allows classes (including main) to be at the top-level of the One-JAR, supports external jars.</li>
    <li>No pollution: dependency jars are not expanded into the filesystem at runtime</li>
    <li>Fast: One-JAR pre-loads all bytecode into memory on startup, making for improved runtime performance.</li>
    <li>Flexible: One-JAR can expand data-files into the file-system, and can be an installer if you want one</li>
    <li>Native: can support Native libraries (does require expansion into a temporary directory)</li>
    <li><?=hyperlink("documents","junit-noframes","Tested")?>: over 140 regression tests with 1500+ lines of test-code in the repository.</li>
</ul>

