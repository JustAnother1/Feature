package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.junit.Test;

public class SolutionImplTest
{

    @Test
    public void testGetRootElement()
    {
        SolutionImpl dut = new SolutionImpl(null);
        assertNull(dut.getRootElement());
    }

    @Test
    public void testGetAlgorithm()
    {
        SolutionImpl dut = new SolutionImpl(null);
        assertNull(dut.getAlgorithm("Bob"));
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

}
