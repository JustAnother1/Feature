package de.nomagic.puzzler.Environment;

import static org.junit.Assert.*;

import org.junit.Test;

import de.nomagic.puzzler.ContextImpl;
import de.nomagic.puzzler.configuration.Configuration;

public class EnvironmentTest
{

    @Test
    public void testGetArchitectureNameNull()
    {
        Environment dut = new Environment(null);
        assertEquals("", dut.getArchitectureName());
    }

    @Test
    public void testGetFamilyNameNull()
    {
        Environment dut = new Environment(null);
        assertEquals("", dut.getFamilyName());
    }

    @Test(expected = NullPointerException.class)
    public void testGetFromProjectNullCtx()
    {
        Environment dut = new Environment(null);
        assertFalse(dut.getFromProject(null));
    }

    @Test
    public void testGetFromProjectNull()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        Environment dut = new Environment(ctx);
        assertFalse(dut.getFromProject(null));
        assertFalse(ctx.wasSucessful());
    }

}
