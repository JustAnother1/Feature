package de.nomagic.puzzler.Environment;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jdom2.Element;

import de.nomagic.puzzler.ContextImpl;
import de.nomagic.puzzler.configuration.Configuration;

public class EnvironmentTest
{
    Configuration cfg = new Configuration();
    ContextImpl ctx = new ContextImpl(cfg);
    Environment dut;
    
    @Before
    public void setUp()
    {
        dut = new Environment(ctx);
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testEnvironment()
    {
        assertNotNull(dut);
    }
    
    @Test
    public void testloadFromNull()
    {
        assertFalse(dut.loadFromElement(null));
    }
    
    @Test
    public void testloadFromEmptyElement()
    {
        Element e = new Element("env");
        assertFalse(dut.loadFromElement(e));
    }
}
