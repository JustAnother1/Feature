package de.nomagic.puzzler;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProjectTest
{

    @Test
    public void testGetEnvironmentElement()
    {
        Project dut = new Project(null);
        assertNull(dut.getEnvironmentElement());
    }

    @Test
    public void testGetSolutionElement()
    {
        Project dut = new Project(null);
        assertNull(dut.getSolutionElement());
    }

}
