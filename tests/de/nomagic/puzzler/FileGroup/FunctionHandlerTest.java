package de.nomagic.puzzler.FileGroup;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

import de.nomagic.puzzler.solution.Function;

public class FunctionHandlerTest
{

    @Test
    public void testIsEmpty_empty()
    {
        FunctionHandler dut = new FunctionHandler();
        assertTrue(dut.isEmpty());
    }

    @Test
    public void testAdd()
    {
        FunctionHandler dut = new FunctionHandler();
        Function func = new Function(null);
        assertTrue(dut.isEmpty());
        dut.add(func);
        assertFalse(dut.isEmpty());
    }

    @Test
    public void testAddAll_empty()
    {
        FunctionHandler dut = new FunctionHandler();
        FunctionHandler dut2 = new FunctionHandler();
        assertTrue(dut.isEmpty());
        dut.addAll(dut2);
        assertTrue(dut.isEmpty());
    }

    @Test
    public void testAddAll()
    {
        FunctionHandler dut = new FunctionHandler();
        FunctionHandler dut2 = new FunctionHandler();
        Function func = new Function(null);
        assertTrue(dut.isEmpty());
        dut.add(func);
        assertFalse(dut.isEmpty());
        assertTrue(dut2.isEmpty());
        dut2.addAll(dut);
        assertFalse(dut2.isEmpty());
    }

    @Test
    public void testgetCode()
    {
        FunctionHandler dut = new FunctionHandler();
        assertEquals(new Vector<String>(), dut.getCode(0, null));
    }

    @Test
    public void testgetCode_inc()
    {
        FunctionHandler dut = new FunctionHandler();
        Function func = new Function(null);
        dut.add(func);
        Vector<String> res = new Vector<String>();
        res.add("#include <bla.h>");
        // TODO assertEquals(res, dut.getCode(0, null));
    }

    @Test
    public void testgetCode_unique()
    {
        FunctionHandler dut = new FunctionHandler();
        Function func = new Function(null);
        dut.add(func);
        dut.add(func);
        Vector<String> res = new Vector<String>();
        res.add("#include <bla.h>");
        // TODO assertEquals(res, dut.getCode(0, null));
    }

    @Test
    public void testgetCode_sorted()
    {
        FunctionHandler dut = new FunctionHandler();
        Function funca = new Function(null);
        Function funcb = new Function(null);
        dut.add(funcb);
        dut.add(funca);
        Vector<String> res = new Vector<String>();
        res.add("#include <bla.h>");
        res.add("#include <no.h>");
        // TODO assertEquals(res, dut.getCode(0, null));
    }


}
