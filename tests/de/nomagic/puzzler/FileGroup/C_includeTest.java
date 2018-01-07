package de.nomagic.puzzler.FileGroup;

import static org.junit.Assert.*;

import org.junit.Test;

public class C_includeTest
{

    @Test
    public void testGetName_null()
    {
        C_include dut = new C_include(null, null);
        assertNull(dut.getName());
    }

    @Test
    public void testGetName()
    {
        C_include dut = new C_include("bla.h", null);
        assertEquals("bla.h", dut.getName());
    }

    @Test
    public void testGetName_comment()
    {
        C_include dut = new C_include("bla.h", "no comment");
        assertEquals("bla.h", dut.getName());
    }

    @Test
    public void testGetComment_null()
    {
        C_include dut = new C_include("bla.h", null);
        assertEquals(null, dut.getComment());
    }

    @Test
    public void testGetComment()
    {
        C_include dut = new C_include("bla.h", "no comment");
        assertEquals("no comment", dut.getComment());
    }

    @Test
    public void testAddComment()
    {
        C_include dut = new C_include("bla.h", null);
        assertEquals(null, dut.getComment());
        dut.addComment("no comment");
        assertEquals("no comment", dut.getComment());
    }

    @Test
    public void testAddComment_add()
    {
        C_include dut = new C_include("bla.h", "no");
        assertEquals("no", dut.getComment());
        dut.addComment("comment");
        assertEquals("no comment", dut.getComment());
    }

    @Test
    public void testgetCode_noComment()
    {
        C_include dut = new C_include("bla.h", null);
        assertEquals("#include <bla.h>", dut.getCode(0, null));
    }

    @Test
    public void testgetCode_Comment()
    {
        C_include dut = new C_include("bla.h", "bla bla bla");
        assertEquals("#include <bla.h> // bla bla bla", dut.getCode(0, null));
    }

}
