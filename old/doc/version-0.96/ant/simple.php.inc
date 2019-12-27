<h2>It's Simple. Really!</h2>

For a more detailed example look a the build.xml file in the One-JAR SDK, available through the <a href="index.php?page=downloads&amp;file=downloads">Downloads</a> page.
The key Ant target to learn from is shown below:

<pre>
    &lt;import file="one-jar-ant-task.xml"/&gt;   
 
    &lt;target name="hello" depends="init"&gt;
        &lt;!-- Build lib.jar --&gt;   
        &lt;javac destdir="${classes.dir}/lib"&gt;
            &lt;src path="${lib.dir}" /&gt;
        &lt;/javac&gt;
        &lt;jar destfile="${build.dir}/lib.jar" &gt;
            &lt;fileset dir="${classes.dir}/lib"/&gt;
        &lt;/jar&gt;   
        &lt;!-- Build classes for main.jar --&gt;   
        &lt;javac destdir="${classes.dir}/src"&gt;
            &lt;src path="${src.dir}" /&gt;
            &lt;classpath path="${build.dir}/lib.jar"/&gt;   
        &lt;/javac&gt;
        &lt;!-- Construct the One-JAR file --&gt;   
        &lt;one-jar destfile="hello.jar" manifest="hello.mf"&gt;
            &lt;main&gt;
                &lt;!-- Construct main.jar from classes and source code --&gt;
                &lt;fileset dir="${classes.dir}/src"/&gt;
            &lt;/main&gt;
            &lt;lib&gt;
                &lt;fileset file="${build.dir}/lib.jar" /&gt;
            &lt;/lib&gt;
        &lt;/one-jar&gt;
        &lt;echo&gt;
          Now you can run the Hello One-JAR example using 
          $ java -jar hello.jar
        &lt;/echo&gt;   
           
    &lt;/target&gt;
</pre>

