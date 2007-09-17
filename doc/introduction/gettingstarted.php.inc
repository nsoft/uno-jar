<h2>Getting Started</h2>
To get started building an application using One-JAR, download the <a href="index.php?page=downloads&amp;file=downloads">One-JAR Software Developers Kit</a>.
The kit is a One-JAR executable, which contains all the pieces needed to assemble One-JAR applications (and can also re-assemble itself).  When 
run, should see the following output:
<pre>
$ java -jar one-jar-sdk-0.96.jar
Extracting SDK... done.  
To build the "Hello One-JAR" example: $ ant hello
To run the "Hello One-JAR" example:   $ java -jar hello.jar
To rebuild the SDK:                   $ ant sdk
</pre>
This extracts a development project (also an Eclipse JDT project) which contains a directory
tree suitable for building a One-JAR application.  
<p>Note: make sure to download an Ant distribution in order to be able to rebuild the <code>hello.jar</code>
or the <code>sdk</code>.  You can obtain the latest version of ant here:
<a href="http://ant.apache.org/bindownload.cgi">http://ant.apache.org/bindownload.cgi</a>.  Alternatively
run Ant from within an IDE such as Eclipse.  You will also need a Java Runtime JRE 1.4 or later.

</p><p><code>$ ant hello</code> builds a simple One-JAR Jar file containing a single Main class, and a single Hello
class.  The Main class is bundled into <i>main.jar</i>, the Hello class is bundled into <i>hello.jar</i>, 
and the requisite One-JAR jar-file is constructed.  Main invokes Hello(), which prints "Hello One-JAR".

</p><p>Use this project as a starting point for your own development by editing the <code>hello</code> target in the 
<code>build.xml</code> file extracted above.  Clone the <code>build.xml</code> file, and strip 
out the <code>sdk</code> target since you will not be rebuilding it as part of your application.

