package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.junit.Test;

public class SolutionImplTest
{

    @Test
    public void testGetRootElement()
    {
        SolutionImpl dut = new SolutionImpl(null);
        assertFalse(dut.getFromProject(null));
    }

    @Test
    public void testGetAlgorithm()
    {
        SolutionImpl dut = new SolutionImpl(null);
        assertNull(dut.getAlgorithm("Bob", null));
    }

    @Test
    public void testGetFromProjectNull()
    {
        SolutionImpl dut = new SolutionImpl(null);
        assertFalse(dut.getFromProject(null));
    }

    @Test
    public void testCheckAndTestAgainstEnvironmentNull()
    {
        SolutionImpl dut = new SolutionImpl(null);
        assertFalse(dut.checkAndTestAgainstEnvironment());
    }

    /*
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
        assertEquals("ConfiguredAlgorithm.getTreeFrom : No root element in the provided solution !\n", err);
        assertEquals(77, err.length());
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
        assertEquals("ConfiguredAlgorithm.getTreeFrom.1 : invalid root tag (bad) !\n", err);
        assertEquals(61, err.length());
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
        assertEquals("ConfiguredAlgorithm.getTreeFrom.2 : No algorithm elements in the provided solution !\n", err);
        assertEquals(85, err.length());
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
        assertEquals("ConfiguredAlgorithm.getTreeFrom.3 : Failed to get Algorithm for null !\n", err);
        assertEquals(71, err.length());
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
        assertEquals("ConfiguredAlgorithm.getTreeFrom.3 : Failed to get Algorithm for algo !\n", err);
        assertEquals(71, err.length());
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
    */    
    
}
