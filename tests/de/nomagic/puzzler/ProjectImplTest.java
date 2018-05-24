package de.nomagic.puzzler;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProjectImplTest
{

    @Test
    public void testGetEnvironmentElement()
    {
        Project dut = new ProjectImpl(null);
        assertNull(dut.getEnvironmentElement());
    }

    @Test
    public void testGetSolutionElement()
    {
        Project dut = new ProjectImpl(null);
        assertNull(dut.getSolutionElement());
    }

}
