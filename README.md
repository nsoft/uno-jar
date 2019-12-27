# uno-jar

Single jar packaging based on a JarClassLoader. Unlike maven shade and gradle shadow, this form of packaging
does not intermix classes into a single directory, and thereby maintains a degree of separation between
libraries with distinct licensing concerns. I am not a Lawyer, and can't give legal advice, but I feel that
this method is much more consistent with LGPL license restrictions than shade/shadow because the original
distributed binary jar is maintained intact as distributed.

This project is based on a fork of One-JAR by Simon Tuffs. Please be sure to see the NOTICE.txt file
for the restrictions on using his code. The original project can be found at:

http://one-jar.sourceforge.net/

## Status

Please note that I do not claim to have 100% fully understood Simon's code, so I may be breaking things 
in subtle ways, but so far the fixes I have applied seem to have worked for me. Very happy to have 
comments/suggestions. The name "uno-jar" is due to Simon's license which forbids endorsement of my code 
with the trade marked name of his project. 
