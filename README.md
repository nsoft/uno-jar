# Uno-Jar

Uno-Jar provides single executable jar ("fat-jar") packaging based on a JarClassLoader. Unlike maven shade and 
gradle shadow, this form of packaging does not intermix classes into a single directory, and thereby maintains 
a degree of separation between libraries with distinct licensing concerns. I am not a Lawyer, and can't give 
legal advice, but I feel that this method is much more consistent with LGPL license restrictions than shade/shadow 
because the original distributed binary jar is maintained intact as distributed. Unpacking and fiddling with 
package names  changes the work and (in my own, not a lawyer opinion) is clearly creating a derived work 
for which source code would need to be published. 

Unlike capsule it does not want to extract jar files onto the user's filesystem, and is not normally hindered by
a lack of write access to the filesystem.

This project is based on a fork of One-JAR by Simon Tuffs. Please be sure to see the NOTICE.txt file
for the restrictions on using his code. The original project can be found at:

http://one-jar.sourceforge.net/

## Documentation

User documentation can be found in our wiki: https://github.com/nsoft/uno-jar/wiki

## Goals

The original One-JAR project evolved in a time long before modern build frameworks. It has become 
significantly out dated and is not actively maintained. For example it has facilities for
creating an "SDK" that can be used to jump-start a project with it. Back in the early 2000's this
was a popular pattern, but In this day and age it has become redundant with modern
tools such as maven and gradle. These tools already have jump-starting capabilities via archetypes, and most IDE's 
will layout a standard project layout. Uno-jar will endeavor to carry forward the **basic** functionality
of One-JAR and leave behind the outdated features as much as possible. 

The PRIMARY use case in all of this is to add an ant task, or gradle task to a build that packages an application
into an executable "fat jar" that loads it's classes from distinct jar files contained within the final
single distributable jar, that runs with a simple `java -jar` invocation. Facilities for unpacking this automatically 
or the like are not likely to be supported going forward. The focus must be kept narrow so that the tool can remain 
light and hopefully be more easily maintained. 


## Status

Uno-Jar is now fully ready to use, it works with Java 11, but does not have any particular support for the Java 9+ 
module system. Modular jars are expected to generally work or fail as they would on any other classpath, though
this has not been extensively tested, and distribution of a fully modular application via uno-jar is untested.
Uno-Jar does however support multi-release jars (a. k. a. MR jars) as libraries and correctly loads the appropriate
classes for the JVM in use. Core and ant jars can be found on maven central here: 

https://search.maven.org/search?q=g:com.needhamsoftware.unojar

the gradle task can be found at 

https://plugins.gradle.org/plugin/com.needhamsoftware.unojar

the maven plugin can be found at

https://github.com/greening/unojar-maven-plugin

## Naming
The name "uno-jar" is due to Simon's license which forbids endorsement of my code 
with the trade marked name of his project (he trademarked One-JAR). Therefore aside
from the discussion of differences from his work, attributions to his antecedent work 
and required licenses his name and the one-jar name will be removed from the project.
