package de.nomagic.puzzler;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProjectImplTest
{

    @Test
    public void testGetEnvironmentElementNull()
    {
        Project dut = new ProjectImpl(null);
        assertNull(dut.getEnvironmentElement());
    }

    @Test
    public void testGetSolutionElementNull()
    {
        Project dut = new ProjectImpl(null);
        assertNull(dut.getSolutionElement());
    }

}
