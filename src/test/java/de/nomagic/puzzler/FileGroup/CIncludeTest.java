package de.nomagic.puzzler.FileGroup;

import static org.junit.Assert.*;

import org.junit.Test;

public class CIncludeTest
{

    @Test
    public void testGetNameNull()
    {
        C_Include dut = new C_Include(null, null);
        assertNull(dut.getName());
    }

    @Test
    public void testGetName()
    {
        C_Include dut = new C_Include("bla.h", null);
        assertEquals("bla.h", dut.getName());
    }

    @Test
    public void testGetNameComment()
    {
        C_Include dut = new C_Include("bla.h", "no comment");
        assertEquals("bla.h", dut.getName());
    }

    @Test
    public void testGetCommentNull()
    {
        C_Include dut = new C_Include("bla.h", null);
        assertEquals(null, dut.getComment());
    }

    @Test
    public void testGetComment()
    {
        C_Include dut = new C_Include("bla.h", "no comment");
        assertEquals("no comment", dut.getComment());
    }

    @Test
    public void testAddComment()
    {
        C_Include dut = new C_Include("bla.h", null);
        assertEquals(null, dut.getComment());
        dut.addComment("no comment");
        assertEquals("no comment", dut.getComment());
    }

    @Test
    public void testAddCommentAdd()
    {
        C_Include dut = new C_Include("bla.h", "no");
        assertEquals("no", dut.getComment());
        dut.addComment("comment");
        assertEquals("no comment", dut.getComment());
    }

    @Test
    public void testgetCodeNoComment()
    {
        C_Include dut = new C_Include("bla.h", null);
        assertEquals("#include <bla.h>", dut.getCode(0, null));
    }

    @Test
    public void testgetCodeComment()
    {
        C_Include dut = new C_Include("bla.h", "bla bla bla");
        assertEquals("#include <bla.h> // bla bla bla", dut.getCode(0, null));
    }

}
