# Uno-Jar

Single jar packaging based on a JarClassLoader. Unlike maven shade and gradle shadow, this form of packaging
does not intermix classes into a single directory, and thereby maintains a degree of separation between
libraries with distinct licensing concerns. I am not a Lawyer, and can't give legal advice, but I feel that
this method is much more consistent with LGPL license restrictions than shade/shadow because the original
distributed binary jar is maintained intact as distributed. Unpacking and fiddling with package names 
changes the work and (in my own, not a lawyer opinion) is clearly creating a derived work 
for which source code would need to be published. 

Unlike capsule it does not want to extract jar files onto the user's filesystem, and is not normally hindered by
a lack of write access to the filesystem.

This project is based on a fork of One-JAR by Simon Tuffs. Please be sure to see the NOTICE.txt file
for the restrictions on using his code. The original project can be found at:

http://one-jar.sourceforge.net/

## Goals

The original One-JAR project evolved in a time long before modern build frameworks. It has become 
significantly out dated and is not actively maintained. For example it has facilities for
creating an "SDK" that can be used to jump-start a project with it. Back in the early 2000's this
was a popular pattern, but In this day and age it has become redundant with modern
tools such as maven and gradle. These tools already have jump-starting capabilities via archetypes, and most IDE's 
will layout a standard project layout. Uno-jar will endeavor to carry forward the **basic** functionality
of One-JAR and leave behind the outdated features as much as possible. Support for modern JVM's and new features such as 
Multi-release jars (MRJars) will be added. 

The PRIMARY use case in all of this is to add an ant task, or gradle task to a build that packages an application
into an executable "fat jar" that loads it's classes from distinct jar files contained within the final
single distributable jar, that runs with a simple `java -jar` invocation. Facilities for unpacking this automatically 
or the like are not likely to be supported going forward. The focus must be kept narrow so that the tool can remain 
light and hopefully be more easily maintained. 

Updating of builds, tests, code-style, and use of modern java constructs where beneficial will also be 
conducted over time.

## Status

### Working
1. Core libraries for simple executable case.
1. Ant task
1. Gradle task
1. Unit test validating execution of Uno-Jar-Main-Class, and access to lib
1. Example of invoking the ant task from gradle

### Unsupported
Anything else. If you miss a One-Jar feature and wish to contribute to its resurrection and subsequent 
maintenance, please file an issue.

### Versions
Version number will carry forward from One-Jar's numbers, and 0.99 will be next, as soon as the WIP/TODO
sections above are complete, followed by 1.0 after I fully cull things that won't be supported going 
forward and can eliminate the old directory.

## Disclaimer 
Please note that I do not claim to have 100% fully understood Simon's code, so I may be breaking things 
in subtle ways, but so far the fixes I have applied seem to have worked for me. Very happy to have 
comments/suggestions. 

## Naming
The name "uno-jar" is due to Simon's license which forbids endorsement of my code 
with the trade marked name of his project (he trademarked One-JAR). Therefore aside
from the discussion of differences from his wokr, attributions to his antecedent work 
and required licenses his name and the one-jar name will be removed from the project.
