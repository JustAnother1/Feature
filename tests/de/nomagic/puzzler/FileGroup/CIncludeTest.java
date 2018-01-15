package de.nomagic.puzzler.FileGroup;

import static org.junit.Assert.*;

import org.junit.Test;

public class CIncludeTest
{

    @Test
    public void testGetNameNull()
    {
        CInclude dut = new CInclude(null, null);
        assertNull(dut.getName());
    }

    @Test
    public void testGetName()
    {
        CInclude dut = new CInclude("bla.h", null);
        assertEquals("bla.h", dut.getName());
    }

    @Test
    public void testGetNameComment()
    {
        CInclude dut = new CInclude("bla.h", "no comment");
        assertEquals("bla.h", dut.getName());
    }

    @Test
    public void testGetCommentNull()
    {
        CInclude dut = new CInclude("bla.h", null);
        assertEquals(null, dut.getComment());
    }

    @Test
    public void testGetComment()
    {
        CInclude dut = new CInclude("bla.h", "no comment");
        assertEquals("no comment", dut.getComment());
    }

    @Test
    public void testAddComment()
    {
        CInclude dut = new CInclude("bla.h", null);
        assertEquals(null, dut.getComment());
        dut.addComment("no comment");
        assertEquals("no comment", dut.getComment());
    }

    @Test
    public void testAddCommentAdd()
    {
        CInclude dut = new CInclude("bla.h", "no");
        assertEquals("no", dut.getComment());
        dut.addComment("comment");
        assertEquals("no comment", dut.getComment());
    }

    @Test
    public void testgetCodeNoComment()
    {
        CInclude dut = new CInclude("bla.h", null);
        assertEquals("#include <bla.h>", dut.getCode(0, null));
    }

    @Test
    public void testgetCodeComment()
    {
        CInclude dut = new CInclude("bla.h", "bla bla bla");
        assertEquals("#include <bla.h> // bla bla bla", dut.getCode(0, null));
    }

}
