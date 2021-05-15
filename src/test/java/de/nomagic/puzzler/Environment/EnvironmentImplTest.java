package de.nomagic.puzzler.Environment;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jdom2.Element;

import de.nomagic.puzzler.ContextImpl;
import de.nomagic.puzzler.configuration.Configuration;

public class EnvironmentImplTest
{
    Configuration cfg = new Configuration();
    ContextImpl ctx = new ContextImpl(cfg);
    Environment dut;

    @Before
    public void setUp()
    {
        dut = new EnvironmentImpl(ctx);
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testEnvironment()
    {
        assertNotNull(dut);
    }

    @Test
    public void testloadFromNull()
    {
        assertFalse(dut.loadFromElement(null));
    }

    @Test
    public void testloadFromEmptyElement()
    {
        Element e = new Element("environment");
        assertFalse(dut.loadFromElement(e));
    }

    @Test
    public void testloadFrom_cpu_no_name_only()
    {
        Element e = new Element("environment");
        Element t = new Element("tool");
        e.addContent(t);
        assertFalse(dut.loadFromElement(e));
    }

    @Test
    public void testloadFrom_element()
    {
        Element e = new Element("environment");
        Element t = new Element("tool");
        t.setAttribute("name", "1bitcpu/two");
        e.addContent(t);
        assertTrue(dut.loadFromElement(e));
    }

    @Test
    public void testPlatformParts_empty()
    {
        String[] res = dut.getPlatformParts();
        assertEquals(0, res.length);
    }

    @Test
    public void testPlatformParts()
    {
        Element e = new Element("environment");
        Element t = new Element("tool");
        t.setAttribute("name", "1bitcpu/two");
        e.addContent(t);
        assertTrue(dut.loadFromElement(e));
        String[] res = dut.getPlatformParts();
        assertEquals(2, res.length);
        assertEquals("1bitcpu", res[0]);
        assertEquals("two", res[1]);
    }

    @Test
    public void testgetBuildSystemType_empty()
    {
        String res = dut.getBuldSystemType();
        assertNull(res);
    }

    @Test
    public void testgetBuildSystemType_null()
    {
        Element e = new Element("environment");
        Element t = new Element("tool");
        t.setAttribute("name", "1bitcpu/two");
        e.addContent(t);
        assertTrue(dut.loadFromElement(e));
        String res = dut.getBuldSystemType();
        assertNull(res);
    }

    @Test
    public void testgetBuildSystemType_()
    {
        Element e = new Element("environment");
        Element t = new Element("build");
        t.setAttribute("type", "qmake");
        e.addContent(t);
        assertFalse(dut.loadFromElement(e));
        String res = dut.getBuldSystemType();
        assertNotNull(res);
        assertEquals("qmake", res);
    }

    @Test
    public void testgetRootApi_empty()
    {
        String res = dut.getRootApi();
        assertNull(res);
    }

    @Test
    public void testprovides_empty()
    {
        assertFalse(dut.provides("value"));
    }

    @Test
    public void testgetAlgorithmCfg_empty()
    {
        Element res = dut.getAlgorithmCfg("myAlgo");
        assertNull(res);
    }

    @Test
    public void testgetConfigFile_empty()
    {
        Element[] res = dut.getConfigFile("postfix", "rootElement");
        assertNull(res);
    }
}
