package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;
import org.junit.Test;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Project;

public class ConfiguredAlgorithmTest
{
    @Test
    public void testConfiguredAlgorithm()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
    }

    @Test
    public void testGetTreeFromNullNull()
    {
        assertNull(ConfiguredAlgorithm.getTreeFrom(null, null));
    }

    @Test
    public void testGetTreeFromNoSolution()
    {
        Context ctx = new ContextStub(null);
        assertNull(ConfiguredAlgorithm.getTreeFrom(ctx, null));
    }

    @Test
    public void testGetTreeFromNoRoot()
    {
        ContextStub ctx = new ContextStub(null);
        Solution s = new SolutionStub();
        ctx.addSolution(s);
        assertNull(ConfiguredAlgorithm.getTreeFrom(ctx, null));
        String err = ctx.getErrors();
        assertEquals("ConfiguredAlgorithm.getTreeFrom : No root element in the provided solution !", err);
        assertEquals(76, err.length());
    }

    @Test
    public void testGetTreeFromBadRoot()
    {
        ContextStub ctx = new ContextStub(null);
        SolutionStub s = new SolutionStub();
        ctx.addSolution(s);
        Element badRoot = new Element("bad");
        s.setRootElement(badRoot);
        assertNull(ConfiguredAlgorithm.getTreeFrom(ctx, null));
        String err = ctx.getErrors();
        assertEquals("ConfiguredAlgorithm.getTreeFrom.1 : invalid root tag (bad) !", err);
        assertEquals(60, err.length());
    }

    @Test
    public void testGetTreeFromNoChildren()
    {
        ContextStub ctx = new ContextStub(null);
        SolutionStub s = new SolutionStub();
        ctx.addSolution(s);
        Element badRoot = new Element(Project.SOLUTION_ELEMENT_NAME);
        s.setRootElement(badRoot);
        assertNull(ConfiguredAlgorithm.getTreeFrom(ctx, null));
        String err = ctx.getErrors();
        assertEquals("ConfiguredAlgorithm.getTreeFrom.2 : No algorithm elements in the provided solution !", err);
        assertEquals(84, err.length());
    }

    @Test
    public void testGetTreeFromBadChildren()
    {
        ContextStub ctx = new ContextStub(null);
        SolutionStub s = new SolutionStub();
        ctx.addSolution(s);
        Element badRoot = new Element(Project.SOLUTION_ELEMENT_NAME);
        Element badChild = new Element("bad");
        badRoot.addContent(badChild);
        s.setRootElement(badRoot);
        assertNull(ConfiguredAlgorithm.getTreeFrom(ctx, null));
        String err = ctx.getErrors();
        assertEquals("ConfiguredAlgorithm.getTreeFrom.3 : Failed to get Algorithm for null !", err);
        assertEquals(70, err.length());
    }

    @Test
    public void testGetTreeFromBadAlgo()
    {
        ContextStub ctx = new ContextStub(null);
        SolutionStub s = new SolutionStub();
        ctx.addSolution(s);
        Element root = new Element(Project.SOLUTION_ELEMENT_NAME);
        Element child = new Element("bad");
        child.setAttribute(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME, "algo");
        root.addContent(child);
        s.setRootElement(root);
        assertNull(ConfiguredAlgorithm.getTreeFrom(ctx, null));
        String err = ctx.getErrors();
        assertEquals("ConfiguredAlgorithm.getTreeFrom.3 : Failed to get Algorithm for algo !", err);
        assertEquals(70, err.length());
    }

    @Test
    public void testGetTreeFromAlgo()
    {
        ContextStub ctx = new ContextStub(null);
        SolutionStub s = new SolutionStub();
        ctx.addSolution(s);
        Element root = new Element(Project.SOLUTION_ELEMENT_NAME);
        Element child = new Element("bad");
        child.setAttribute(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME, "algo");
        root.addContent(child);
        s.setRootElement(root);
        Algorithm algo = new Algorithm(child, ctx);
        s.addAlgorithm("algo", algo);
        ConfiguredAlgorithm res = ConfiguredAlgorithm.getTreeFrom(ctx, null);
        assertNotNull(res);
        // String[] err = ctx.getErrors();
        // assertEquals(1, err.length);
        // assertEquals("ConfiguredAlgorithm.getTree : Failed to get Algorithm for null !", err[0]);
    }

    @Test
    public void testHasApiNull()
    {
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
        assertEquals("dut", dut.getName());
        assertFalse(dut.hasApi("bla"));
    }

    @Test
    public void testHasApiNoApi()
    {
        Context ctx = new ContextStub(null);
        Element root = new Element("testElement");
        Algorithm algo = new Algorithm(root, ctx);
        ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", algo, ctx, null);
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
}
