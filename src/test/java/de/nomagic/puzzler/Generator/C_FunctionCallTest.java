package de.nomagic.puzzler.Generator;

import static org.junit.Assert.*;

import org.junit.Test;

public class C_FunctionCallTest
{

    @Test
    public void testToString()
    {
        C_FunctionCall dut = new C_FunctionCall("bla:blubb(blabla)");
        assertEquals("bla:blubb(blabla)", dut.toString());
    }

    @Test
    public void testGetApiNull()
    {
        C_FunctionCall dut = new C_FunctionCall("bla");
        assertEquals(null, dut.getApi());
    }

    @Test
    public void testGetApi()
    {
        C_FunctionCall dut = new C_FunctionCall("bla:blubb(blabla)");
        assertEquals("bla", dut.getApi());
    }

    @Test
    public void testSetApi()
    {
        C_FunctionCall dut = new C_FunctionCall("bla:blubb(blabla)");
        dut.setApi("somethingElse");
        assertEquals("somethingElse", dut.getApi());
    }

    @Test
    public void testGetArgumentsNull()
    {
        C_FunctionCall dut = new C_FunctionCall("bla");
        assertEquals(null, dut.getApi());
    }

    @Test
    public void testGetArguments()
    {
        C_FunctionCall dut = new C_FunctionCall("bla(blubb)");
        assertEquals("blubb", dut.getArguments());
    }

    @Test
    public void testGetName()
    {
        C_FunctionCall dut = new C_FunctionCall("bla(blubb)");
        assertEquals("bla", dut.getName());
    }

    @Test
    public void testSetFunctionalArguments()
    {
        C_FunctionCall dut = new C_FunctionCall("bla:bla");
        assertEquals(null, dut.getArguments());
        dut.setFunctionArguments("aloha");
        assertEquals("aloha", dut.getArguments());
    }

}
