package de.nomagic.puzzler.Generator;

import static org.junit.Assert.*;

import org.junit.Test;

public class CFunctionCallTest
{

    @Test
    public void testToString()
    {
        CFunctionCall dut = new CFunctionCall("bla:blubb(blabla)");
        assertEquals("bla:blubb(blabla)", dut.toString());
    }

    @Test
    public void testGetApiNull()
    {
        CFunctionCall dut = new CFunctionCall("bla");
        assertEquals(null, dut.getApi());
    }

    @Test
    public void testGetApi()
    {
        CFunctionCall dut = new CFunctionCall("bla:blubb(blabla)");
        assertEquals("bla", dut.getApi());
    }

    @Test
    public void testGetArgumentsNull()
    {
        CFunctionCall dut = new CFunctionCall("bla");
        assertEquals(null, dut.getApi());
    }

    @Test
    public void testGetArguments()
    {
        CFunctionCall dut = new CFunctionCall("bla(blubb)");
        assertEquals("blubb", dut.getArguments());
    }

    @Test
    public void testGetName()
    {
        CFunctionCall dut = new CFunctionCall("bla(blubb)");
        assertEquals("bla", dut.getName());
    }

    @Test
    public void testSetFunctionalArguments()
    {
        CFunctionCall dut = new CFunctionCall("bla:bla");
        assertEquals(null, dut.getArguments());
        dut.setFunctionArguments("aloha");
        assertEquals("aloha", dut.getArguments());
    }

}
