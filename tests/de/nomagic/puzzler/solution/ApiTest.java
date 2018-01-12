package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.jdom2.Element;
import org.junit.Test;

public class ApiTest
{

    @Test
    public void testToString_invalid()
    {
        Api dut = new Api(null, null);
        assertEquals("invalid Api", dut.toString());
    }

    @Test
    public void testToString()
    {
        Element root = new Element("bla");
        root.setAttribute(Api.API_NAME_ATTRIBUTE_NAME, "Bob");
        Api dut = new Api(root, null);
        assertEquals("Api Bob", dut.toString());
    }

    @Test
    public void testGetFromFile_null()
    {
        assertNull(Api.getFromFile(null, null));
    }

    @Test
    public void testGetRequiredFunctions()
    {
        Element root = new Element("bla");
        Api dut = new Api(root, null);
        Function[] res = dut.getRequiredFunctions();
        assertNotNull(res);
        assertEquals(0, res.length);
    }

}
