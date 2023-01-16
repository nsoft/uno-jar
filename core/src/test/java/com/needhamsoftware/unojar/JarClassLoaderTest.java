package com.needhamsoftware.unojar;

import com.copyright.easiertest.Mock;
import com.copyright.easiertest.ObjectUnderTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.net.URLClassLoader;

import static com.copyright.easiertest.EasierMocks.*;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
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
        URLClassLoader ucl = new URLClassLoader(new URL[]{},jclMock);
        expect(object.isJarClassLoaderAParent(jclMock)).andReturn(true);
        replay();
        assertTrue(object.isJarClassLoaderAParent(ucl));
    }

    @Test
    public void testLoaderIsNull() {
        replay();
        assertFalse(object.isJarClassLoaderAParent(null));
    }
}