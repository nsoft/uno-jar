# Uno-Jar

Single jar packaging based on a JarClassLoader.
Unlike maven shade and gradle shadow, this form of packaging does not intermix classes into a single directory, and thereby maintains a degree of separation between libraries with distinct licensing concerns.
I am not a Lawyer, and can't give legal advice, but I feel that this method is much more consistent with LGPL license restrictions than shade/shadow because the original distributed binary jar is maintained intact as distributed.
The distributed Jar file simply acts as a compressed file system in Uno-Jar. Unpacking and fiddling with package names as done in shade or shadow changes the work and (in my own, not a lawyer opinion) is running the risk of creating a derived work for which source code would need to be published.

Unlike capsule ([dead since 2017](https://github.com/puniverse/capsule)), it does not want to extract jar files onto the user's filesystem, and is not normally hindered by a lack of write access to the filesystem.

This project is based on a fork of One-JAR<sup>(TM)</sup> by Simon Tuffs. Please be sure to see the NOTICE.txt file
for the restrictions on using his code.
Be especially aware that he trademarked the name One-Jar (which is why we don't use it for this project)
The original project can be found at:

http://one-jar.sourceforge.net/

## Documentation

User documentation can be found in our wiki: https://github.com/nsoft/uno-jar/wiki

## Goals

The original One-JAR project evolved in a time long before modern build frameworks. It has become
significantly out dated and is not actively maintained. For example, it has facilities for
creating an "SDK" that can be used to jump-start a project with it. Back in the early 2000's this
was a popular pattern, but In this day and age it has become redundant with modern
tools such as maven and gradle. These tools already have jump-starting capabilities via archetypes, and most IDE's
will lay out a standard project layout. Uno-jar will endeavor to carry forward the **basic** functionality
of One-JAR and leave behind the outdated features as much as possible.

The PRIMARY use case in all of this is to add an ant task, or gradle task to a build that packages an application
into an executable "fat jar" that loads its classes from distinct jar files contained within the final
single distributable jar, that runs with a simple `java -jar` invocation. Facilities for unpacking this automatically
or the like are not likely to be supported going forward. The focus must be kept narrow so that the tool can remain
light and hopefully be more easily maintained.


## Status

Uno-Jar is now fully ready to use, it works with Java 11, but does not have any particular support for the Java 9+ module system.
Modular jars are expected to generally work or fail as they would on any other classpath, though this has not been extensively tested, and distribution of a fully modular application via uno-jar is untested.
Uno-Jar does however support multi-release jars (a. k. a. MR jars) as libraries and correctly loads the appropriate classes for the JVM in use.
Core and ant jars can be found on maven central here:

https://search.maven.org/search?q=g:com.needhamsoftware.unojar

the gradle task can be found at

https://plugins.gradle.org/plugin/com.needhamsoftware.unojar

### Unsupported
Anything else. If you miss a One-Jar feature and wish to contribute to its resurrection and subsequent
maintenance, please file an issue.

### Versions
Versions of Uno-Jar will generally attempt to maintain semantic version expected patterns.
Starting with 1.1, major breaks in back compatibility will increment the major version.
Versions before 1.1 have a lot of changes related to the porting/fork/adoption from One-Jar
and may have some variation in back compatability. Also see our Java Version support policy on the wiki at:

https://github.com/nsoft/uno-jar/wiki/Java-Version-Support


## Disclaimer
Please note that I do not claim to have 100% fully understood Simon's code, so I may be breaking things
in subtle ways, but so far the fixes taI have applied seem to have worked for me. Very happy to have
comments/suggestions.

## Naming
The name "uno-jar" is due to Simon's license which forbids endorsement of my code
with the trade marked name of his project (he trademarked One-JAR). Therefore, aside
from the discussion of differences from his work, attributions to his antecedent work
and required licenses his name and the one-jar name will be removed from the project.
