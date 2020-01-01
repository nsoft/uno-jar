#Building
This project is difficult to build because it includes a gradle plugin that 
depends on other modules and thus has chicken/egg issues. The current 
bootstrapping process when things get out of sync is:

1. Edit settings.gradle to comment out the tests module
1. Run publishToMavenLocal
1. Edit settings.gradle to uncomment the tests module
1. Now you should be able to run until you manage do publish a broken/bugged 
   version of something to mavenlocal, or blow away your maven caches... 
   
You will need to publish to mavenLocal to see changes in one module in
when running another module.

I am definitely not pleased with the situation, and looking to improve it.