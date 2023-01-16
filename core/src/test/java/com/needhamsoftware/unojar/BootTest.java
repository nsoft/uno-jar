package com.needhamsoftware.unojar;

import com.copyright.easiertest.Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.jar.Attributes;

import static com.copyright.easiertest.EasierMocks.*;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

public class BootTest {

    public BootTest() {
        prepareMocks(this);
    }

    @Mock Attributes attributesMock;

    @Before
    public void setUp() {
        reset();
    }

    @After
    public void tearDown() {
        verify();
    }

    @Test
    public void testMainArgsUnmodifiedIfCommandLine() {
        replay();
        String[] strings = Boot.parseMainArgs(new String[]{"Foo BAR", "baz\\ bam"}, attributesMock);
        assertEquals("Foo BAR", strings[0]);
        assertEquals("baz\\ bam", strings[1]);
    }

    @Test
    public void testMainArgsParsing() {
        expect(attributesMock.getValue("Uno-Jar-Main-Args")).andReturn("Foo BAR baz\\ bam");
        replay();
        String[] strings = Boot.parseMainArgs(new String[0], attributesMock);
        assertEquals("Foo", strings[0]);
        assertEquals("BAR", strings[0]);
        assertEquals("baz bam", strings[1]);
    }
}
