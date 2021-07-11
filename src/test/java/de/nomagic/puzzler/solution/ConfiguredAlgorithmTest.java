package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;
import org.junit.Test;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.ContextStub;

public class ConfiguredAlgorithmTest
{
    @Test
    public void testConfiguredAlgorithm()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null, null);
        assertEquals("dut", dut.getName());
    }

    @Test
    public void testHasApiNull()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null, null);
        assertEquals("dut", dut.getName());
        assertFalse(dut.hasApi("bla"));
    }
/*
    @Test
    public void testHasApiNoApi()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null, null);
        assertEquals("dut", dut.getName());
        assertFalse(dut.hasApi("bla"));
    }

    @Test
    public void testHasApiWrongApi()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        root.setAttribute(Algorithm.ALGORITHM_API_ATTRIBUTE_NAME, "foo");
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        assertEquals("dut", dut.getName());
        assertFalse(dut.hasApi("bla"));
    }

    @Test
    public void testHasApiApi()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        root.setAttribute(Algorithm.ALGORITHM_API_ATTRIBUTE_NAME, "bla");
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        assertEquals("dut", dut.getName());
        assertTrue(dut.hasApi("bla"));
    }

    @Test
    public void testToString()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        assertEquals("dut(null)", dut.toString());
    }

    @Test
    public void testAddAlgorithmNull()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        dut.addChild(null);
    }

    @Test
    public void testAddAlgorithmNull2()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        dut.addChild(dut);
    }

    @Test
    public void testGetAlgorithmNull()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        assertNull(dut.getChild("bla"));
    }

    @Test
    public void testGetAlgorithmWrongName()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        ConfiguredAlgorithm algo = new ConfiguredAlgorithm("algo", null, null, null);
        dut.addChild(algo);
        assertNull(dut.getChild("bla"));
    }

    @Test
    public void testGetAlgorithmHasOne()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        ConfiguredAlgorithm algo = new ConfiguredAlgorithm("bla", null, null, null);
        dut.addChild(algo);
        assertSame(algo, dut.getChild("bla"));
    }

    @Test
    public void testGetAllAlgorithmsNone()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        Iterator<String> it = dut.getAllChildren();
        assertNotNull(it);
        assertFalse(it.hasNext());
    }

    @Test
    public void testGetAllAlgorithmsOne()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        ConfiguredAlgorithm algo = new ConfiguredAlgorithm("algo", null, null, null);
        dut.addChild(algo);
        Iterator<String> it = dut.getAllChildren();
        assertNotNull(it);
        assertTrue(it.hasNext());
        assertEquals("algo", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testGetAllAlgorithmsTwo()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        ConfiguredAlgorithm algo = new ConfiguredAlgorithm("algo", null, null, null);
        dut.addChild(algo);
        ConfiguredAlgorithm algo2 = new ConfiguredAlgorithm("algo2", null, null, null);
        dut.addChild(algo2);
        Iterator<String> it = dut.getAllChildren();
        assertNotNull(it);
        assertTrue(it.hasNext());
        assertEquals("algo2", it.next());
        assertTrue(it.hasNext());
        assertEquals("algo", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testGetBuildInBla()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        assertNull(dut.getBuildIn("bla"));
    }

    @Test
    public void testGetBuildInNumChilds()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        assertEquals("0", dut.getBuildIn(ConfiguredAlgorithm.BUILD_IN_NUM_OF_CHILDS));
    }

    @Test
    public void testDumpParameter()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        assertEquals("Parameter: <empty>", dut.dumpParameter());
    }

    @Test
    public void testGetParameter()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        assertNull(dut.getParameter("bla"));
    }

    @Test
    public void testDumpProperty()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        assertEquals("Properties: <empty>", dut.dumpProperty());
    }

    @Test
    public void testGetProperty()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        assertNull(dut.getProperty("bla"));
    }

    @Test
    public void testGetDescription_Null()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut(no algorithm attached)", dut.getDescription());
    }

    @Test
    public void testGetDescription()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        root.setAttribute(Algorithm.ALGORITHM_API_ATTRIBUTE_NAME, "someApi");
        root.setAttribute(Algorithm.ALGORITHM_NAME_ATTRIBUTE_NAME, "someAlgorithm");
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, null, null);
        assertEquals("dut (Algorithm someAlgorithm implementing someApi)", dut.getDescription());
    }

    @Test
    public void testGetApis_Null()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertNull(dut.getApis());
    }

    @Test
    public void testGetApis()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        root.setAttribute(Algorithm.ALGORITHM_API_ATTRIBUTE_NAME, "someApi");
        root.setAttribute(Algorithm.ALGORITHM_NAME_ATTRIBUTE_NAME, "someAlgorithm");
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, null, null);
        assertEquals("someApi", dut.getApis());
    }

    @Test
    public void testGetAlgorithmElementNull()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertNull(dut.getAlgorithmElement("bla"));
    }

    @Test
    public void testGetAlgorithmElementNoChild()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        assertNull(dut.getAlgorithmElement("bla"));
    }

    @Test
    public void testGetAlgorithmElementNotThatChild()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        Element child = new Element("foo");
        root.addContent(child);
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        assertNull(dut.getAlgorithmElement("bla"));
    }

    @Test
    public void testGetAlgorithmElementOneChild()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        Element child = new Element("foo");
        root.addContent(child);
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        assertSame(child, dut.getAlgorithmElement("foo"));
    }

    @Test
    public void testGetAlgorithmElementTwoChildren()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        Element child = new Element("foo");
        Element sibling = new Element("foo");
        root.addContent(child);
        root.addContent(sibling);
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        assertSame(child, dut.getAlgorithmElement("foo"));
    }

    @Test
    public void testGetAlgorithmElementsNull()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertNull(dut.getAlgorithmElements("bla"));
    }

    @Test
    public void testGetAlgorithmElementsNoChild()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        List<Element> res = dut.getAlgorithmElements("bla");
        assertNotNull(res);
        assertEquals(0, res.size());
    }

    @Test
    public void testGetAlgorithmElementsNotThatChild()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        Element child = new Element("foo");
        root.addContent(child);
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        List<Element> res = dut.getAlgorithmElements("bla");
        assertNotNull(res);
        assertEquals(0, res.size());
    }

    @Test
    public void testGetAlgorithmElementsOneChild()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        Element child = new Element("foo");
        root.addContent(child);
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        List<Element> res = dut.getAlgorithmElements("foo");
        assertEquals(1, res.size());
        assertSame(child, res.get(0));
    }

    @Test
    public void testGetAlgorithmElementsTwoChildren()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        Element child = new Element("foo");
        Element sibling = new Element("foo");
        root.addContent(child);
        root.addContent(sibling);
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        List<Element> res = dut.getAlgorithmElements("foo");
        assertEquals(2, res.size());
        assertSame(child, res.get(0));
        assertSame(sibling, res.get(1));
    }
    */
}
