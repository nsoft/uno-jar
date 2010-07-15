<h2>Frameworks</h2>

Application frameworks present a challenge to tools like One-JAR because they are written with
certain assumed behaviors about classloading which may not always be valid when running under the
One-JAR ClassLoader.  Because of extensive input from the One-JAR user community, release 0.97 now
has extensive (possibly full) support for the following frameworks:

<ol>
<li>SpringFramework 3.0.2</li>
<li>Google Guice 2.0</li>
</ol>

Because of the different assumptions about classloading (and in particular, what appears to
be a minor omission in the Guice URL resolvers),  One-JAR must be configured differently depending
on which of the frameworks you wish to use.
<p/>The configuration is controlled by a META-INF/MANIFEST.MF setting at the One-JAR top level which
names the factory implementation class that implements <code>IURLFactory</code>:

<pre>
One-Jar-URL-Factory: com.simontuffs.onejar.JarClassLoader$OneJarURLFactory
</pre>

One-JAR supports custom generation of URL's based on a factory interface in the JarClassLoader:

<pre>
// Injectable URL factory.
public static interface IURLFactory {
    public URL getURL(String codebase, String resource) throws MalformedURLException;
    public URL getCodeBase(String jar) throws MalformedURLException;
}
</pre>
A factory must support <code>getUrl(String codebase, String resource)</code>, returning a URL for a resource
at a given codebase.  It must also support <code>getCodeBase(String jar)</code>, returning a URL for a given codebase.
<p>One-JAR comes pre-configured with two factories which return different kinds of URLs:

<ul>
<li>com.simontuffs.onejar.JarClassLoader$OneJarURLFactory -- returns: <code>onejar:/&lt;codebase>/&lt;resource></li>
<li>com.simontuffs.onejar.JarClassLoader$FileURLFactory -- returns: <code>jar:file:&lt;path-to-onejar>!/&lt;codebase>/&lt;resource</li>
</ul>

The <code>FileURL</code> URL's are injected with a <code>java.net.URLStreamHandler</code> which 
knows how to tear-apart the "jar:" protocol.  FileURL is the best choice for SpringFramework 
applications, which use "jar:" protocol URL's in their annotation scanners.
<p/>The OneJarUrlFactory URL's rely on the global <code>com.simontuffs.onejar.Handler</code> which
One-JAR adds to the <code>java.protocol.handler.pkgs</code> system property on startup, so that 
the URL resolving mechanisms know where to delegate the "onejar:" protocol URL requests.
<p/>
You can inject your own implementation of <code>IURLFactory</code> into the One-JAR ClassLoader by setting
the <code>One-Jar-URL-Factory</code> property to your own implementation of <code>IURLFactory</code>

