package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;
import org.junit.Test;

import de.nomagic.puzzler.ContextImpl;
import de.nomagic.puzzler.Project;
import de.nomagic.puzzler.configuration.Configuration;

public class ConfiguredAlgorithmTest
{
    @Test
    public void testConfiguredAlgorithm()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
    }

    @Test
    public void testGetTreeFrom_null_null()
    {
        assertNull(ConfiguredAlgorithm.getTreeFrom(null, null));
    }

    @Test
    public void testGetTreeFrom_noSolution()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        assertNull(ConfiguredAlgorithm.getTreeFrom(ctx, null));
    }

    @Test
    public void testGetTreeFrom_Solution()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Solution s = new Solution(ctx);
        ctx.addSolution(s);
        assertNull(ConfiguredAlgorithm.getTreeFrom(ctx, null));
    }

    @Test
    public void testGetTreeFrom_Project()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Project pro = new Project(ctx);
        Solution s = new Solution(ctx);
        assertTrue(s.getFromProject(pro));
        ctx.addSolution(s);
        assertNull(ConfiguredAlgorithm.getTreeFrom(ctx, null));
    }

    @Test
    public void testHasApi_null()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        assertFalse(dut.hasApi("bla"));
    }

    @Test
    public void testHasApi_noApi()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element root = new Element("testElement");
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        assertEquals("dut", dut.getName());
        assertFalse(dut.hasApi("bla"));
    }

    @Test
    public void testHasApi_wrongApi()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element root = new Element("testElement");
        root.setAttribute(Algorithm.ALGORITHM_API_ATTRIBUTE_NAME, "foo");
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        assertEquals("dut", dut.getName());
        assertFalse(dut.hasApi("bla"));
    }

    @Test
    public void testHasApi_Api()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
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
        assertEquals("ConfiguredAlgorithm dut(null)", dut.toString());
    }

    @Test
    public void testAddAlgorithm_null()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        dut.addAlgorithm(null);
    }

    @Test
    public void testAddAlgorithm_null2()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        dut.addAlgorithm(dut);
    }

    @Test
    public void testGetAlgorithm_null()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        assertNull(dut.getAlgorithm("bla"));
    }

    @Test
    public void testGetAlgorithm_wrongName()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        ConfiguredAlgorithm algo = new ConfiguredAlgorithm("algo", null, null, null);
        dut.addAlgorithm(algo);
        assertNull(dut.getAlgorithm("bla"));
    }

    @Test
    public void testGetAlgorithm_hasOne()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        ConfiguredAlgorithm algo = new ConfiguredAlgorithm("bla", null, null, null);
        dut.addAlgorithm(algo);
        assertSame(algo, dut.getAlgorithm("bla"));
    }

    @Test
    public void testGetAllAlgorithms_none()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        Iterator<String> it = dut.getAllAlgorithms();
        assertNotNull(it);
        assertFalse(it.hasNext());
    }

    @Test
    public void testGetAllAlgorithms_one()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        ConfiguredAlgorithm algo = new ConfiguredAlgorithm("algo", null, null, null);
        dut.addAlgorithm(algo);
        Iterator<String> it = dut.getAllAlgorithms();
        assertNotNull(it);
        assertTrue(it.hasNext());
        assertEquals("algo", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testGetAllAlgorithms_two()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        ConfiguredAlgorithm algo = new ConfiguredAlgorithm("algo", null, null, null);
        dut.addAlgorithm(algo);
        ConfiguredAlgorithm algo2 = new ConfiguredAlgorithm("algo2", null, null, null);
        dut.addAlgorithm(algo2);
        Iterator<String> it = dut.getAllAlgorithms();
        assertNotNull(it);
        assertTrue(it.hasNext());
        assertEquals("algo2", it.next());
        assertTrue(it.hasNext());
        assertEquals("algo", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testGetBuildIn_bla()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        assertNull(dut.getBuildIn("bla"));
    }

    @Test
    public void testGetBuildIn_numChilds()
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
        assertEquals("Parameter:\n", dut.dumpParameter());
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
        assertEquals("Properties:\n", dut.dumpProperty());
    }

    @Test
    public void testGetProperty()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        assertNull(dut.getProperty("bla"));
    }

    @Test
    public void testGetAlgorithmElement_null()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertNull(dut.getAlgorithmElement("bla"));
    }

    @Test
    public void testGetAlgorithmElement_noChild()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element root = new Element("testElement");
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        assertNull(dut.getAlgorithmElement("bla"));
    }

    @Test
    public void testGetAlgorithmElement_notThatChild()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element root = new Element("testElement");
        Element child = new Element("foo");
        root.addContent(child);
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        assertNull(dut.getAlgorithmElement("bla"));
    }

    @Test
    public void testGetAlgorithmElement_oneChild()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element root = new Element("testElement");
        Element child = new Element("foo");
        root.addContent(child);
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        assertSame(child, dut.getAlgorithmElement("foo"));
    }

    @Test
    public void testGetAlgorithmElement_twoChildren()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
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
    public void testGetAlgorithmElements_null()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertNull(dut.getAlgorithmElements("bla"));
    }

    @Test
    public void testGetAlgorithmElements_noChild()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element root = new Element("testElement");
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
        List<Element> res = dut.getAlgorithmElements("bla");
        assertNotNull(res);
        assertEquals(0, res.size());
    }

    @Test
    public void testGetAlgorithmElements_notThatChild()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
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
    public void testGetAlgorithmElements_oneChild()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
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
    public void testGetAlgorithmElements_twoChildren()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
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
}
