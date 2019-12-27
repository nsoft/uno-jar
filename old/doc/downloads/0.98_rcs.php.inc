<h2>0.98 Pre Release</h2>

The CVS Head has now switched to development of 0.98.  The focus of this release is
refactoring, cleanup, and simplification.  Over time, the One-JAR classloader has
developed many special cases that need to be reduced.  One-JAR started out with a goal
of not relying on URLClassLoader, to keep things tight and simple.  Support for 
frameworks such as Spring and Guice has driven this goal into oblivion, and it is
time to refactor the JarClassLoader so that it is a URLClassLoader, which should
substantially reduce the code-size and number of special cases.
<p/>
You can access pre-release downloads here: <a href="https://sourceforge.net/projects/one-jar/files/one-jar/0.98%20RCs/">https://sourceforge.net/projects/one-jar/files/one-jar/0.98%20RCs/</a>

<p/>If you find a problem with the 0.97 release, you can try the 0.98 pre-releases.  However, 
0.98 will be developed in a "release early, release often" style, and will be somewhat unstable
some of the time.  Hopefully the regression suite will keep things honest.
</p>Since the goal is refactoring, the functionality change will be minimal, and the
regression suites should be untouched (except perhaps for extension to cover new 
code). 
