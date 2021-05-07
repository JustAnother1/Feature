package de.nomagic.puzzler.FileGroup;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

public class IncludeHandlerTest
{

    @Test
    public void testIsEmptyEmpty()
    {
        IncludeHandler dut = new IncludeHandler();
        assertTrue(dut.isEmpty());
    }

    @Test
    public void testAdd()
    {
        IncludeHandler dut = new IncludeHandler();
        CInclude inc = new CInclude("bla.h", null);
        assertTrue(dut.isEmpty());
        dut.add(inc);
        assertFalse(dut.isEmpty());
    }

    @Test
    public void testAddAllEmpty()
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
        CInclude inc = new CInclude("bla.h", null);
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
    public void testgetCodeInc()
    {
        IncludeHandler dut = new IncludeHandler();
        CInclude inc = new CInclude("bla.h", null);
        dut.add(inc);
        Vector<String> res = new Vector<String>();
        res.add("#include <bla.h>");
        assertEquals(res, dut.getCode(0, null));
    }

    @Test
    public void testgetCodeUnique()
    {
        IncludeHandler dut = new IncludeHandler();
        CInclude inc = new CInclude("bla.h", null);
        dut.add(inc);
        dut.add(inc);
        Vector<String> res = new Vector<String>();
        res.add("#include <bla.h>");
        assertEquals(res, dut.getCode(0, null));
    }

    @Test
    public void testgetCodeSorted()
    {
        IncludeHandler dut = new IncludeHandler();
        CInclude incb = new CInclude("bla.h", null);
        CInclude incn = new CInclude("no.h", null);
        dut.add(incn);
        dut.add(incb);
        Vector<String> res = new Vector<String>();
        res.add("#include <bla.h>");
        res.add("#include <no.h>");
        assertEquals(res, dut.getCode(0, null));
    }

}
