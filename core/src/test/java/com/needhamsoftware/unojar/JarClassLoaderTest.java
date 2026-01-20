package com.needhamsoftware.unojar;

import com.copyright.easiertest.Mock;
import com.copyright.easiertest.ObjectUnderTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

import static com.copyright.easiertest.EasierMocks.prepareMocks;
import static com.copyright.easiertest.EasierMocks.replay;
import static com.copyright.easiertest.EasierMocks.reset;
import static com.copyright.easiertest.EasierMocks.verify;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JarClassLoaderTest {
  @ObjectUnderTest
  private JarClassLoader object;
  @Mock
  private JarClassLoader jclMock;

  public JarClassLoaderTest() {
    prepareMocks(this);
  }

  @Before
  public void setUp() {
    reset();
  }

  @After
  public void tearDown() {
    verify();
  }

  @Test
  public void testLoaderIsInstanceOfJarClassLoader() {
    replay();
    assertTrue(object.isJarClassLoaderAParent(jclMock));
  }

  @Test
  public void testParentLoaderIsInstanceOfJarClassLoader() {
    URLClassLoader ucl = new URLClassLoader(new URL[]{}, jclMock);
    expect(object.isJarClassLoaderAParent(jclMock)).andReturn(true);
    replay();
    assertTrue(object.isJarClassLoaderAParent(ucl));
  }

  @Test
  public void testLoaderIsNull() {
    replay();
    assertFalse(object.isJarClassLoaderAParent(null));
  }

  @Test
  public void testDelegateToExt() {
    // TODO: In replacing simon's anonymous subclass of UrlClassLoader in JarClassLoader for
    //  handling externally loaded classes a few questions remain and I don't see any good tests
    //  of this functionality. Reading https://sourceforge.net/p/one-jar/bugs/32/ seems to indicate
    //  that at least one use case is for database implementation jars (probably GPL ones such as
    //  mysql etc. that can't be included for license reasons). In this class we should write a
    //  simple test verifying that the external classloader is consulted. In other tests (probably in
    //  the test-suite area) that demonstrates an infinite loop when a simple UrlClassLoader is used
    //  (as per the code prior to the above linked issue) and then verify it doesn't fail with the code
    //  used for that issue, and then finally replace it with our UnoJarDependencyLoader class to show that
    //  that too solves the infinite loop.
    replay();
  }

  @Test
  public void findResourcesShouldIgnoreTrailingSlash() throws IOException, URISyntaxException {
    URI jarFile = this.getClass().getResource("/uno-jar-examples-unojar.jar").toURI();
    JarClassLoader loader = new JarClassLoader(this.getClass().getClassLoader(), jarFile.toURL().toExternalForm());
    // populate caches
    assertNotNull(loader.load("META-INF/foo/hello.txt", null));
    replay();
    assertNotNull(loader.findResource("META-INF/foo/"));
    assertNotNull(loader.findResource("META-INF/foo"));
    assertTrue(loader.findResources("META-INF/foo/").hasMoreElements());
    assertTrue(loader.findResources("META-INF/foo").hasMoreElements());
  }
}