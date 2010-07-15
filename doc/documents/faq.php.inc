<h2>Frequently Asked Questions</h2>
OK, so I'm making some of these up.  But some of them were real questions.  Can you tell
the difference?
<ul>
<li>
Q: My application reads its configuration out of a file.  How do I make it into a One-JAR?
<p class="half-line">A: Naughty application!  But many are.  Put your file into the 
One-JAR archive (e.g. <code>config.properties</code>), then add the following line to the <code>META-INF/MANIFEST.MF</code> file:
<pre>One-Jar-Expand: config.properties.</pre>
<p class="half-line">The first time you run the One-JAR archive, this file will be expanded.  If you update the
archive, it will be overwritten on the next expansion.  The expansion mechanism can handle
both files and folders.  
</li>
<li>
Q: I don't understand what One-JAR is doing?
<p class="half-line">A: Turn on diagnostics using <code>java -Done-jar.verbose=true -jar &lt;one-jar.jar&gt;</code>
</li>
<li>
Q: But I still can't understand why my application is failing!
<p class="half-line">A: Look at the <code>JarClassLoader.java</code> file which is distributed with the downloads.
It holds all the secrets.
</li>
<li>
Q: Where's the source code?
<p class="half-line">A: The essential source code is in the distribution.  The extended source code and a full Eclipse development
environment is located at the SourceForge CVS site with anonymous access (no password required): <code>:pserver:anonymous@one-jar.cvs.sourceforge.net:/cvsroot/one-jar</code>.
This site contains a master Ant <code>build.xml</code> file which can rebuild the distribution and perform extensive regression testing, 
so if you want to submit a patch it is highly recommended that you (a) add a new test for the patch, and (b) run the existing tests.
</li>
<li>
Q: Can I modify the source code for my application?
<p class="half-line">A: Yes, the One-JAR <a href="index.php?page=documents&amp;file=license">license</a> expressly permits this.
The <u>only</u> requirement is that you must redistribute the copyright notice and disclaimer contained in the license.
There are different ways you can satisfy this requirement, for example:
<ul>
<li>Include the One-JAR license agreement inside your One-JAR archive.  This is automatic when
you use the Ant and Maven2 build tools, which put the license in <code>doc/one-jar-license.txt</code></li>
<li>Include the One-JAR license agreement in your documentation</li>
<li>Expand the One-JAR license agreement into a directory when your application is executed</li>
<li>Put a button on your GUI installer which displays the One-JAR license</li>
</ul>
<p>
Our goal with this requirement is to (a) ensure that you tell your customers who created One-JAR, so
that they can find out more about it and (b) make it clear to your customers that we are 
providing no warranty or support for the One-JAR code, either modified or unmodified.
</li>
<li>
Q: Should I send my modifications back to you?
<p class="half-line">A: Absolutely.  Send patches and they will be integrated into future releases, and tested for regressions.
Better still, become a contributor to the One-JAR community.
</li>


</ul>
Seriously though, since its release in Summer 2004 there have been very few issues: 
basically, One-JAR just works.  The recent release has extended One-JAR to work with frameworks
such as SpringFramework and Guice which did have specific requirements of the ClassLoader that 
One-JAR was not supplying.

<h3>Bugs and Support</h3>

<p>There will always be some issues with any software. If you find one please
report it to the <a href="http://sourceforge.net/tracker/?group_id=111153&amp;atid=658457">SourceForge Bug Tracker</a>.
Better still, fix it and submit a patch and it will get integrated into the next release.

<p>Please submit new FAQ questions (and answers if you have them) to the <a href="http://sourceforge.net/forum/forum.php?forum_id=380844">SourceForge Discussion List</a>.  The
growing One-JAR community is good at supporting itself.  But please don't expect a real-time response: One-JAR
really is Open-Source, you should be able to build and support yourself with ease.  If you find yourself
struggling, please contact us for <?=hyperlink("more", "support", "professional support")?>

