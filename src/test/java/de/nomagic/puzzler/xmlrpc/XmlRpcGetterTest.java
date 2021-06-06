package de.nomagic.puzzler.xmlrpc;

import static org.junit.Assert.*;

import org.jdom2.Document;
import org.junit.Test;

public class XmlRpcGetterTest
{
    private XmlRpcGetter dut = null;

    @Test
    public void testGetAsDocument_null()
    {
        dut = new XmlRpcGetter("");
        Document res = dut.getAsDocument(null);
        assertNull(res);
    }

    @Test
    public void testGetAsDocument_emptyString()
    {
        dut = new XmlRpcGetter("");
        Document res = dut.getAsDocument("");
        assertNull(res);
    }

    @Test
    public void testGetAsDocument_url_emptyString()
    {
        dut = new XmlRpcGetter("http://127.0.0.1:8080/");
        Document res = dut.getAsDocument("");
        assertNull(res);
    }
}
