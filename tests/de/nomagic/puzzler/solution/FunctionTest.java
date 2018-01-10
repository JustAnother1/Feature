package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.jdom2.Element;
import org.junit.Test;

import de.nomagic.puzzler.FileGroup.FunctionHandler;

public class FunctionTest
{

    @Test
    public void testSameAs_null()
    {
        Function dut = new Function(null);
        assertFalse(dut.sameAs(null));
    }

    @Test
    public void testIsRequired_null()
    {
        Function dut = new Function(null);
        assertFalse("", dut.isRequired());
    }

    @Test
    public void testGetName_null()
    {
        Function dut = new Function(null);
        assertEquals("", dut.getName());
    }

    @Test
    public void testGetComment()
    {
        Function dut = new Function(null);
        assertEquals(null, dut.getComment());
        dut.addComment("foo");
        assertEquals("foo", dut.getComment());
        dut.addComment("bar");
        assertEquals("foo bar", dut.getComment());
    }

    @Test
    public void testGetCode_null()
    {
        Function dut = new Function(null);
        assertNull(dut.getCode(FunctionHandler.TYPE_DECLARATION, "bla"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION, "bla"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION+1, "bla"));
    }

    @Test
    public void testGetCode_emptyTag()
    {
        Element tag = new Element("a");
        Function dut = new Function(tag);
        assertEquals("void null(void);", dut.getCode(FunctionHandler.TYPE_DECLARATION, "bla"));
        assertEquals("void null(void)blabla", dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION, "bla"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION+1, "bla"));
    }
}
