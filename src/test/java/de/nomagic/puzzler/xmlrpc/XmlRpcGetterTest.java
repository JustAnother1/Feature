package de.nomagic.puzzler.xmlrpc;

import static org.junit.Assert.*;

import java.util.List;

import org.jdom2.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class XmlRpcGetterTest
{
    private final Logger testedLog = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private ListAppender<ILoggingEvent> listAppender;
    private XmlRpcGetter dut = null;

    @Before
    public void setupTestedLogger()
    {
        listAppender = new ListAppender<>();
        listAppender.start();
        testedLog.setLevel(Level.WARN);
        testedLog.addAppender(listAppender);
    }

    @After
    public void teardownTestedLogger()
    {
    	testedLog.detachAppender(listAppender);
    }

    @Test
    public void testGetAsDocument_null()
    {
        dut = new XmlRpcGetter("");
        Document res = dut.getAsDocument(null);
        assertNull(res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());
        assertEquals("Malformed URL when trying :  !", logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertTrue((logsList.get(1).getMessage()).startsWith("Details:Exception [no protocol: \njava.net.MalformedURLException: no protocol: \n"));
        assertEquals(Level.ERROR, logsList.get(1).getLevel());
    }

    @Test
    public void testGetAsDocument_emptyString()
    {
        dut = new XmlRpcGetter("");
        Document res = dut.getAsDocument("");
        assertNull(res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());
        assertEquals("Malformed URL when trying :  !", logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertTrue((logsList.get(1).getMessage()).startsWith("Details:Exception [no protocol: \njava.net.MalformedURLException: no protocol: \n"));
        assertEquals(Level.ERROR, logsList.get(1).getLevel());
    }

    @Test
    public void testGetAsDocument_url_emptyString()
    {
        dut = new XmlRpcGetter("http://127.0.0.1:8080/");
        Document res = dut.getAsDocument("");
        assertNull(res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertTrue((logsList.get(0).getMessage()).startsWith("Exception [Failed to read server's response:"));
        assertEquals(Level.ERROR, logsList.get(0).getLevel());        
    }
}
