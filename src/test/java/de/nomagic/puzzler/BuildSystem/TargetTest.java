package de.nomagic.puzzler.BuildSystem;

import static org.junit.Assert.*;

import org.jdom2.Element;
import org.junit.Test;

public class TargetTest
{

    @Test
    public void testGetSource()
    {
        Target dut = new Target("bla");
        assertEquals("bla", dut.getSource());
    }

    @Test
    public void testGetSourceXmlNull()
    {
        Element xml = null;
        Target dut = new Target(xml);
        assertEquals(null, dut.getSource());
    }

    @Test
    public void testGetSourceXmlInvalid()
    {
        Element xml = new Element("bla");
        Target dut = new Target(xml);
        assertEquals(null, dut.getSource());
    }

    @Test
    public void testGetOutput()
    {
        Target dut = new Target("bla");
        dut.setOutput("blubb");
        assertEquals("blubb", dut.getOutput());
    }

    @Test
    public void testGetAsMakeFileTarget()
    {
        Target dut = new Target("bla");
        dut.setOutput("blubb");
        dut.setRule("rule");
        assertEquals("blubb: bla\n\trule", dut.getAsMakeFileTarget());
    }

    @Test
    public void testGetAsMakeFileTargetMultipleLines()
    {
        Target dut = new Target("bla");
        dut.setOutput("blubb");
        dut.setRule("  rule1\n       rule2   \n          rule3\n          ");
        assertEquals("blubb: bla\n\trule1\n\trule2\n\trule3", dut.getAsMakeFileTarget());
    }

    @Test
    public void testPhonyYes()
    {
        Element xml = new Element("bla");
        xml.setAttribute("phony", "TRUE");
        Target dut = new Target(xml);
        assertTrue(dut.isPhony());
    }

    @Test
    public void testPhonyNo()
    {
        Element xml = new Element("bla");
        xml.setAttribute("phony", "False");
        Target dut = new Target(xml);
        assertFalse(dut.isPhony());
    }

    @Test
    public void testPhonySet()
    {
        Element xml = new Element("bla");
        Target dut = new Target(xml);
        dut.setPhony(false);
        assertFalse(dut.isPhony());
        dut.setPhony(true);
        assertTrue(dut.isPhony());
    }

    @Test
    public void testSetDefault()
    {
        Element xml = new Element("bla");
        Target dut = new Target(xml);
        assertFalse(dut.isDefault());
        dut.setDefault();
        assertTrue(dut.isDefault());
    }
}
