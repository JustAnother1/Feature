package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import java.util.List;

import org.jdom2.Element;
import org.junit.Test;

import de.nomagic.puzzler.ContextImpl;
import de.nomagic.puzzler.configuration.Configuration;

public class AlgorithmTest {

    @Test
    public void testAlgorithm()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Algorithm cut = new Algorithm(null, ctx);
        assertFalse(cut.hasApi("bla"));
    }

    @Test
    public void testToString_null()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Algorithm cut = new Algorithm(null, ctx);
        assertSame("ERROR: unconfigured Algorithm", cut.toString());
    }

    @Test
    public void testToString()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element root = new Element("testElement");
        root.setAttribute(Algorithm.ALGORITHM_NAME_ATTRIBUTE_NAME, "blablabla");
        Algorithm cut = new Algorithm(root, ctx);
        assertEquals("Algorithm blablabla", cut.toString());
    }

   @Test
    public void testToString_Api()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element root = new Element("testElement");
        root.setAttribute(Algorithm.ALGORITHM_NAME_ATTRIBUTE_NAME, "blablabla");
        root.setAttribute(Algorithm.ALGORITHM_API_ATTRIBUTE_NAME, "foo");
        Algorithm cut = new Algorithm(root, ctx);
        assertEquals("Algorithm blablabla implementing foo", cut.toString());
    }

    @Test
    public void testGetFromFile_null()
    {
        assertNull(Algorithm.getFromFile(null, null));
    }

    @Test
    public void testGetFromFile_badElement()
    {
        Element root = new Element("bad");
        assertNull(Algorithm.getFromFile(root, null));
    }

    @Test
    public void testGetFromFile_badElement_lib()
    {
        Element root = new Element("bad");
        assertNull(Algorithm.getFromFile(root, null));
    }

    @Test
    public void testGetFromFile_noLib()
    {
        Element root = new Element("good");
        root.setAttribute(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME, "bla");
        assertNull(Algorithm.getFromFile(root, null));
    }

    @Test
    public void testGetFromFile_noImplementation()
    {
        Element root = new Element("good");
        root.setAttribute(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME, "bla");
        Algorithm res = Algorithm.getFromFile(root, null);
        assertNotNull(res);
        assertSame("ERROR: unconfigured Algorithm", res.toString());
    }

    @Test
    public void testHasApi_null()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Algorithm cut = new Algorithm(null, ctx);
        assertFalse(cut.hasApi("blupp"));
    }

    @Test
    public void testHasApi_bad()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element ele = new Element("bad");
        Algorithm cut = new Algorithm(ele, ctx);
        assertFalse(cut.hasApi("blupp"));
    }

    @Test
    public void testHasApi_wrongApi()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element ele = new Element("good");
        ele.setAttribute(Algorithm.ALGORITHM_API_ATTRIBUTE_NAME, "bla");
        Algorithm cut = new Algorithm(ele, ctx);
        assertFalse(cut.hasApi("blupp"));
    }

    @Test
    public void testHasApi_hasApi()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element ele = new Element("good");
        ele.setAttribute(Algorithm.ALGORITHM_API_ATTRIBUTE_NAME, "blupp");
        Algorithm cut = new Algorithm(ele, ctx);
        assertTrue(cut.hasApi("blupp"));
    }

    @Test
    public void testGetChild_null()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Algorithm cut = new Algorithm(null, ctx);
        assertNull(cut.getChild("Alex"));
    }

    @Test
    public void testGetChild_noChild()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element ele = new Element("good");
        Algorithm cut = new Algorithm(ele, ctx);
        assertNull(cut.getChild("Alex"));
    }

    @Test
    public void testGetChild_hasChild()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element ele = new Element("good");
        Element child = new Element("Alex");
        ele.addContent(child);
        Algorithm cut = new Algorithm(ele, ctx);
        Element res = cut.getChild("Alex");
        assertNotNull(res);
        assertEquals(res, child);
    }

    @Test
    public void testGetChildren_null()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Algorithm cut = new Algorithm(null, ctx);
        assertNull(cut.getChildren("Alex"));
    }

    @Test
    public void testGetChildren_noChild()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element ele = new Element("good");
        Algorithm cut = new Algorithm(ele, ctx);
        List<Element> res = cut.getChildren("Alex");
        assertNotNull(res);
        assertEquals(0, res.size());
    }

    @Test
    public void testGetChildren_hasChild()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element ele = new Element("good");
        Element child = new Element("Alex");
        ele.addContent(child);
        Algorithm cut = new Algorithm(ele, ctx);
        List<Element> res = cut.getChildren("Alex");
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(res.get(0), child);
    }
    @Test
    public void testGetChildren_hasChildren()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element ele = new Element("good");
        Element child = new Element("Alex");
        Element child2 = new Element("Alex");
        ele.addContent(child);
        ele.addContent(child2);
        Algorithm cut = new Algorithm(ele, ctx);
        List<Element> res = cut.getChildren("Alex");
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals(res.get(0), child);
        assertEquals(res.get(1), child2);
    }
}
