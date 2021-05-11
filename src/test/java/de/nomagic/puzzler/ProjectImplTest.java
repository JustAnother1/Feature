package de.nomagic.puzzler;

import static org.junit.Assert.*;

import org.jdom2.Element;
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
    public void testGetEnvironmentElement()
    {
        Project dut = new ProjectImpl(null);
        Element project = new Element("project");
        Element environment = new Element("environment");
        project.addContent(environment);
        boolean res = dut.loadFromElement(project);
        assertTrue(res);
        Element env = dut.getEnvironmentElement();
        assertNotNull(env);
        assertEquals("environment", env.getName());
    }

    @Test
    public void test_loadFromElement_null()
    {
        Project dut = new ProjectImpl(null);
        boolean res = dut.loadFromElement(null);
        assertFalse(res);
    }

    @Test
    public void testGetSolutionElementNull()
    {
        Project dut = new ProjectImpl(null);
        assertNull(dut.getSolutionElement());
    }

    @Test
    public void testGetSolutionElement()
    {
        Project dut = new ProjectImpl(null);
        Element project = new Element("project");
        Element environment = new Element("environment");
        project.addContent(environment);
        Element solution = new Element("solution");
        project.addContent(solution);
        boolean res = dut.loadFromElement(project);
        assertTrue(res);
        Element sol = dut.getSolutionElement();
        assertNotNull(sol);
        assertEquals("solution", sol.getName());
    }

}
