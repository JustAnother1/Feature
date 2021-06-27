package de.nomagic.puzzler;

import static org.junit.Assert.*;

import org.junit.Test;

import de.nomagic.puzzler.solution.SolutionStub;

public class ContextImplTest
{

    @Test
    public void testwasSuccessfull()
    {
        ContextImpl dut = new ContextImpl(null);
        assertNotNull(dut);
        assertTrue(dut.wasSucessful()); // no failure registered yet.
        assertEquals("Context has no Errors!", dut.getErrors());
    }

    @Test
    public void testCfg()
    {
        ContextImpl dut = new ContextImpl(null);
        assertNotNull(dut);
        assertNotNull(dut.cfg());
    }

    @Test
    public void testClose()
    {
        ContextImpl dut = new ContextImpl(null);
        assertNotNull(dut);
        dut.close();
    }

    @Test
    public void testSolution()
    {
        ContextImpl dut = new ContextImpl(null);
        assertNotNull(dut);
        assertNull(dut.getSolution());
        SolutionStub sol = new SolutionStub();
        dut.addSolution(sol);
        assertEquals(sol, dut.getSolution());
    }

    @Test
    public void testFileGetter()
    {
        ContextImpl dut = new ContextImpl(null);
        assertNotNull(dut);
        assertNull(dut.getFileGetter());
        FileGetterStub fg = new FileGetterStub();
        dut.addFileGetter(fg);
        assertEquals(fg, dut.getFileGetter());
    }

    @Test
    public void testGetElementFrom()
    {
        ContextImpl dut = new ContextImpl(null);
        assertNotNull(dut);
        assertNull(dut.getElementfrom(null, null));
        assertNull(dut.getElementfrom(null, ""));
        assertNull(dut.getElementfrom("", ""));
    }

    @Test
    public void testGetElementFromFile()
    {
        ContextImpl dut = new ContextImpl(null);
        assertNotNull(dut);
        assertNull(dut.getElementfrom(null, null, null));
        assertNull(dut.getElementfrom(null, null, ""));
        assertNull(dut.getElementfrom(null, "", ""));
        assertNull(dut.getElementfrom("", "", ""));
    }

}
