package de.nomagic.puzzler.xmlrpc;

import static org.junit.Assert.*;

import org.jdom2.Document;
import org.junit.Before;
import org.junit.Test;

import de.nomagic.puzzler.xmlrpc.XmlRpcGetter;

public class XmlRpcGetterTest
{
    private XmlRpcGetter dut = null;

    @Before
    public void setUp() throws Exception
    {
        dut = new XmlRpcGetter();
    }

    @Test
    public void testGetAsDocument_null()
    {
        Document res = dut.getAsDocument(null);
        assertNull(res);
    }

    @Test
    public void testGetAsDocument_emptyString()
    {
        Document res = dut.getAsDocument("");
        assertNull(res);
    }
}
