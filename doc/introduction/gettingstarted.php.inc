<h2>Getting Started</h2>
To get started building an application using One-Jar, download the <a href="http://prdownloads.sourceforge.net/one-jar/one-jar-sdk-0.96.jar?download">One-Jar Software Developers Kit</a>.
The kit is a One-Jar executable, which contains all the pieces needed to assemble One-Jar applications (and can also re-assemble itself).  When 
run, should see the following output:
<pre>
$ java -jar one-jar-sdk-0.96.jar
Extracting SDK... done.  
To build the "Hello One-Jar" example: $ ant hello
To run the "Hello One-Jar" example:   $ java -jar hello.jar
To rebuild the SDK:                   $ ant sdk
</pre>
This extracts a development project (also an Eclipse JDT project) which contains a directory
tree suitable for building a One-Jar application.  
<p>Note: make sure to download an Ant distribution in order to be able to rebuild the <code>hello.jar</code>
or the <code>sdk</code>.  You can obtain the latest version of ant here:
<a href="http://ant.apache.org/bindownload.cgi">http://ant.apache.org/bindownload.cgi</a>.  Alternatively
run Ant from within an IDE such as Eclipse.  You will also need a Java Runtime JRE 1.4 or later.

</p><p><code>$ ant hello</code> builds a simple One-Jar Jar file containing a single Main class, and a single Hello
class.  The Main class is bundled into <i>main.jar</i>, the Hello class is bundled into <i>hello.jar</i>, 
and the requisite One-Jar jar-file is constructed.  Main invokes Hello(), which prints "Hello One-Jar".

</p><p>Use this project as a starting point for your own development, by editing the <code>hello/build.xml</code> file.

