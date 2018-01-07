package de.nomagic.puzzler.FileGroup;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

public class IncludeHandlerTest
{

    @Test
    public void testIsEmpty_empty()
    {
        IncludeHandler dut = new IncludeHandler();
        assertTrue(dut.isEmpty());
    }

    @Test
    public void testAdd()
    {
        IncludeHandler dut = new IncludeHandler();
        C_include inc = new C_include("bla.h", null);
        assertTrue(dut.isEmpty());
        dut.add(inc);
        assertFalse(dut.isEmpty());
    }

    @Test
    public void testAddAll_empty()
    {
        IncludeHandler dut = new IncludeHandler();
        IncludeHandler dut2 = new IncludeHandler();
        assertTrue(dut.isEmpty());
        dut.addAll(dut2);
        assertTrue(dut.isEmpty());
    }

    @Test
    public void testAddAll()
    {
        IncludeHandler dut = new IncludeHandler();
        IncludeHandler dut2 = new IncludeHandler();
        C_include inc = new C_include("bla.h", null);
        assertTrue(dut.isEmpty());
        dut.add(inc);
        assertFalse(dut.isEmpty());
        assertTrue(dut2.isEmpty());
        dut2.addAll(dut);
        assertFalse(dut2.isEmpty());
    }

    @Test
    public void testgetCode()
    {
        IncludeHandler dut = new IncludeHandler();
        assertEquals(new Vector<String>(), dut.getCode(0, null));
    }

    @Test
    public void testgetCode_inc()
    {
        IncludeHandler dut = new IncludeHandler();
        C_include inc = new C_include("bla.h", null);
        dut.add(inc);
        Vector<String> res = new Vector<String>();
        res.add("#include <bla.h>");
        assertEquals(res, dut.getCode(0, null));
    }

    @Test
    public void testgetCode_unique()
    {
        IncludeHandler dut = new IncludeHandler();
        C_include inc = new C_include("bla.h", null);
        dut.add(inc);
        dut.add(inc);
        Vector<String> res = new Vector<String>();
        res.add("#include <bla.h>");
        assertEquals(res, dut.getCode(0, null));
    }

    @Test
    public void testgetCode_sorted()
    {
        IncludeHandler dut = new IncludeHandler();
        C_include incb = new C_include("bla.h", null);
        C_include incn = new C_include("no.h", null);
        dut.add(incn);
        dut.add(incb);
        Vector<String> res = new Vector<String>();
        res.add("#include <bla.h>");
        res.add("#include <no.h>");
        assertEquals(res, dut.getCode(0, null));
    }

}
