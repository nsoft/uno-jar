<h2>One-JAR Appgen</h2>
<i>Caution -- Advanced Topic Ahead: this information is not required to use the Appgen capability. </i>
<p/>
The One-JAR Application Generator (one-jar-appgen) creates a fully-functional One-JAR Eclipse/Ant
project, based on the structure of a template project.  This is similar in nature to the way that
Grails lets you build projects using code-generators that can be customized by changing template 
documents.

<p/>The default project  template is contained inside the <code>one-jar-appgen-0.97.jar/one-jar-$project$.jar</code> file
and it has the following directory structure: 
<p/><img src="one-jar-project.jpg"/>
<p/>The key elements of this template are:
<ul>
<li>src -- Source-code for the main program</li>
<li>test -- Source code for a stand-alone test </li>
<li>junit -- Source code to bridge between JUnit and the stand-alone test</li>
<li>one-jar -- Distribution of the One-JAR software for use building and testing the project: the project is self-contained</li>
<li>lib -- A directory into which dependent libraries can be placed for building into a One-JAR</li>
<li>build.xml -- A build script that can build a project into the various artifacts (runnable jars)</li>
</ul>
one-jar-appgen processes this directory structure (and the files inside it), performing
token substitution with the data entered by the user.  The substituted tokens are:
<ul>
<li>$project$ -- the path of the project: the last element of which is used as a project name.</li>
<li>$package$ -- the package name for the project (Java package, '.' separators)</li>
</ul>
For example, when one-jar-appgen creates a project named <code>path/test</code> in a package called <code>foo.bar</code>, 
<code>$project$</code> will expand to <code>test</code>, and <code>$package$</code> will expand to <code>foo.bar</code>.  The output files
will be written under <code>path</code>.
<p>The template build.xml file starts out as: 
<pre>
&lt;?xml version="1.0" encoding="UTF-8"?>
&lt;project name="one-jar-$project$" basedir="." default="build">
</pre>
and is transformed to:
<pre>
&lt;?xml version="1.0" encoding="UTF-8"?>
&lt;project name="one-jar-test" basedir="." default="build">
</pre>
etc.  The end result is a new project which builds two One-JAR archives: 
<ul>
<li>build/$project$.jar -- A simple main program example</li>
<li>build/test-$project$.jar -- A stand-alone test program that can be hooked into JUnit</li>
</ul>
The test-$project$.jar is special: it has a main.jar with a Test class
that acts as the main entry point.  This class extends <code>com.simontuffs.onejar.test.Testable</code>, which can be used by the <code>$package$.test.$project$Suite</code>
class to run a JUnit test suite.  This is demonstrated by the <code>junit.$project$</code> target in the build.xml file.

<h3>Roll Your Own Project Template</h3>
one-jar-appgen is extensible: if you supply your own <code>one-jar-$project$.jar</code> as a third
argument to the program it will be used for template expansion instead of the built-in template.
<p/>The best way to roll-your-own template is to first extract and unpack the <code>one-jar-$project$.jar</code>
file from inside the one-jar-appgen jar.  You can then build it in-place (it does build itself) using Ant, 
and start adding in your own new files (with the tokens <code>$project$</code> and <code>$package</code>
in the appropriate places in file-content, filenames and directories.

<p/>Once the appgen results are what you want, running with the extra third argument, you can repack
the <code>one-jar-appgen</code> jar file with the new template, and start using it (and sending it 
to your customers).

<h3>Roll Your Own Appgen</h3>
If you need more information, unpack the <code>one-jar-appgen</code> jar file and look at the Appgen source code 
which is under main.jar/src in the distribution.  There is no significant magic there, but there is
some logic which might not be obvious surrounding camel-case and conversion of '-' to '_' in various
places that you might wish to modify as you "roll-your-own appgen" too.