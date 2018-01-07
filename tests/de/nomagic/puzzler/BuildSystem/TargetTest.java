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
    public void testGetSource_xml_null()
    {
        Element xml = null;
        Target dut = new Target(xml);
        assertEquals(null, dut.getSource());
    }

    @Test
    public void testGetSource_xml_invalid()
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

}
