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
    public void testToStringNull()
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
    public void testToStringApi()
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
    public void testGetFromFileNull()
    {
        assertNull(Algorithm.getFromFile((Element)null, null));
        assertNull(Algorithm.getFromFile((String)null, null));
    }

    @Test
    public void testGetFromFileBadElement()
    {
        Element root = new Element("bad");
        assertNull(Algorithm.getFromFile(root, null));
    }

    @Test
    public void testGetFromFileBadElementLib()
    {
        Element root = new Element("bad");
        assertNull(Algorithm.getFromFile(root, null));
    }

    @Test
    public void testGetFromFileNoImplementation()
    {
        Element root = new Element("good");
        root.setAttribute(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME, "bla");
        Algorithm res = Algorithm.getFromFile(root, null);
        assertNull(res);
    }

    @Test
    public void testHasApiNull()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element ele = new Element("bad");
        Algorithm cut = new Algorithm(ele, ctx);
        assertFalse(cut.hasApi(null));
    }

    @Test
    public void testHasApiBad()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element ele = new Element("bad");
        Algorithm cut = new Algorithm(ele, ctx);
        assertFalse(cut.hasApi("blupp"));
    }

    @Test
    public void testHasApiWrongApi()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element ele = new Element("good");
        ele.setAttribute(Algorithm.ALGORITHM_API_ATTRIBUTE_NAME, "bla");
        Algorithm cut = new Algorithm(ele, ctx);
        assertFalse(cut.hasApi("blupp"));
    }

    @Test
    public void testHasApiHasApi()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element ele = new Element("good");
        ele.setAttribute(Algorithm.ALGORITHM_API_ATTRIBUTE_NAME, "blupp");
        Algorithm cut = new Algorithm(ele, ctx);
        assertTrue(cut.hasApi("blupp"));
    }

    @Test
    public void testGetChildNull()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Algorithm cut = new Algorithm(null, ctx);
        assertNull(cut.getChild("Alex"));
    }

    @Test
    public void testGetChildNoChild()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Element ele = new Element("good");
        Algorithm cut = new Algorithm(ele, ctx);
        assertNull(cut.getChild("Alex"));
    }

    @Test
    public void testGetChildHasChild()
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
    public void testGetChildrenNull()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Algorithm cut = new Algorithm(null, ctx);
        assertNull(cut.getChildren("Alex"));
    }

    @Test
    public void testGetChildrenNoChild()
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
    public void testGetChildrenHasChild()
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
    public void testGetChildrenHasChildren()
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
