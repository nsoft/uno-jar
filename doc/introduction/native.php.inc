<h2>Native Libraries</h2>
One-JAR supports the delivery of applications which require native-library (JNI) support.
Native libraries are placed in a <code>/binlib</code> folder inside the One-JAR: these are 
auto-expanded to a temporary working directory, and the One-JAR <code>JarClassLoader</code>
loads them automatically.  That's all there is to it.  There is an example (Windows 32) 
example project on the Downloads page.

