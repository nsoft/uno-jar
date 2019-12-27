<h3>Ant Taskdef</h3>
This path downloads the minimal environment needed to work with One-JAR using Ant.  An
advantage to this path is that the source-code for the Ant &lt;one-jar> taskdef is provided for education
purposes.

<ol>
<li>Download <a href="https://sourceforge.net/downloads/one-jar/one-jar/one-jar-0.97/one-jar-ant-task-0.97.jar">one-jar-ant-task-0.97.jar</a></li>
<li>Unpack into a working directory (e.g. c:/one-jar-ant): 
<code>
<br/>
<br/>$ mkdir c:/one-jar-ant
<br/>$ cd c:/one-jar-ant
<br/>$ jar -xvf .../one-jar-ant-task-0.97.jar
</code>
</li>
<li>Add the following to your build.xml script:
<code>
<br/>
<br/>&lt;property name="one-jar.dist.dir" value="c:/one-jar-ant"/>
<br/>&lt;import file="${one-jar.dist.dir}/one-jar-ant-task.xml" optional="true" />
<br/>&lt;one-jar destfile="your.one.jar" 
<br/>...etc.
</code>
</li>
</ol>

