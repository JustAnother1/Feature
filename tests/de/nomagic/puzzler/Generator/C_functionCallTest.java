package de.nomagic.puzzler.Generator;

import static org.junit.Assert.*;

import org.junit.Test;

public class C_functionCallTest
{

    @Test
    public void testToString()
    {
        C_functionCall dut = new C_functionCall("bla:blubb(blabla)");
        assertEquals("bla:blubb(blabla)", dut.toString());
    }

    @Test
    public void testGetApi_null()
    {
        C_functionCall dut = new C_functionCall("bla");
        assertEquals(null, dut.getApi());
    }

    @Test
    public void testGetApi()
    {
        C_functionCall dut = new C_functionCall("bla:blubb(blabla)");
        assertEquals("bla", dut.getApi());
    }

    @Test
    public void testGetArguments_null()
    {
        C_functionCall dut = new C_functionCall("bla");
        assertEquals(null, dut.getApi());
    }

    @Test
    public void testGetArguments()
    {
        C_functionCall dut = new C_functionCall("bla(blubb)");
        assertEquals("blubb", dut.getArguments());
    }

    @Test
    public void testGetName()
    {
        C_functionCall dut = new C_functionCall("bla(blubb)");
        assertEquals("bla", dut.getName());
    }

    @Test
    public void testSetFunctionalArguments()
    {
        C_functionCall dut = new C_functionCall("bla:bla");
        assertEquals(null, dut.getArguments());
        dut.setFunctionArguments("aloha");
        assertEquals("aloha", dut.getArguments());
    }

}
