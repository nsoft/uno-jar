<h3>What is One-JAR?</h3>
<p/>One-JAR provides a comprehensive solution to the problem of shipping a Java application, 
along with all of its dependency Jars, in a single Jar file without polluting the filesystem
at runtime. It provides build-tools for Ant and Maven, 
and can support framework-based applications such as Spring and Guice, or frameworkless applications.

<h3>Who uses One-JAR?</h3>
A number of <a href="http://www.google.com/search?hl=en&as_q=neither+copyright+P-Simon-Tuffs&as_epq=one+jar+&as_oq=&as_eq=introduction+jar-plug+soap-stone&lr=&as_filetype=&ft=i&as_sitesearch=&as_qdr=all&as_rights=&as_occt=any&cr=&as_nlo=&as_nhi=&safe=images">commercial and open-source projects</a> have chosen One-JAR as their
packaging mechanism.  Since inception One-JAR has had over <a href="http://sourceforge.net/project/stats/?group_id=111153&ugn=one-jar&type=&mode=alltime">75,000 downloads</a> and shows 
a consistent download profile over time, and is used across a variety of <a href="http://sourceforge.net/downloads/one-jar/stats_os?dates=2010-02-01%20to%202010-07-16">operating-systems</a>

<h3>Where can I get it?</h3>
<?=hyperlink("downloads", "downloads", "Download it through this page")?>.

<h3>Key Features</h3>
<ul>
    <li>Stateless/Install Free: One-JAR can contain everything needed by an application.</li>
    <li>Friendly: BSD-style license, no impediments to commerical integration</li>
    <li>Easy: One-JAR tools for Ant & Maven: an SDK and an application generator</li>
    <li>Invisible: One-JAR works transparently, intercepting resource and class loading and redirecting them back inside the One-JAR</li>
    <li>Clean delegation model, allows classes (including main) to be at the top-level of the One-JAR, supports external jars.</li>
    <li>No pollution: dependency jars are not expanded into the filesystem at runtime</li>
    <li>Fast: One-JAR pre-loads all bytecode into memory on startup, making for improved runtime performance.</li>
    <li>Flexible: One-JAR can expand data-files into the file-system, and can be an installer if you want one</li>
    <li>Native: can support Native libraries (does require expansion into a temporary directory)</li>
    <li><?=hyperlink("documents","junit-noframes","Tested")?>: over 140 regression tests with 1500+ lines of test-code in the repository.</li>
</ul>

