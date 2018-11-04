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
    }

    public void testCfg()
    {
        ContextImpl dut = new ContextImpl(null);
        assertNotNull(dut);
        assertNull(dut.cfg());
    }

    public void testClose()
    {
        ContextImpl dut = new ContextImpl(null);
        assertNotNull(dut);
        dut.close();
    }

    public void testSolution()
    {
        ContextImpl dut = new ContextImpl(null);
        assertNotNull(dut);
        assertNull(dut.getSolution());
        SolutionStub sol = new SolutionStub();
        dut.addSolution(sol);
        assertEquals(sol, dut.getSolution());
    }

}
