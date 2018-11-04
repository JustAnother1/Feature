package de.nomagic.puzzler.FileGroup;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

public class CFileTest
{

    @Test
    public void testAddContentsOfNull()
    {
        CFile dut = new CFile(null);
        dut.addContentsOf(null);
    }

    @Test
    public void testAddContentsOf()
    {
        CFile emptyFile = new CFile("empty");
        CFile dut = new CFile("dut");
        dut.addContentsOf(emptyFile);
    }

    @Test
    public void testAddLineInvalidSection()
    {
        CFile dut = new CFile("dut");
        dut.addLine("bla", "some line");
        Vector<String> res = new Vector<String>();
        Vector<String> newRes = dut.prepareSectionData("bla", res);
        assertNotNull(newRes);
        assertEquals(0, newRes.size());
    }

    @Test
    public void tesprepareSectionData()
    {
        CFile dut = new CFile("dut");
        dut.addLine("FileHeader", "some line");
        Vector<String> res = new Vector<String>();
        Vector<String> newRes = dut.prepareSectionData("FileHeader", res);
        assertNotNull(newRes);
        assertEquals(0, newRes.size());
    }

    @Test
    public void tesaddLine()
    {
        CFile dut = new CFile("dut");
        dut.addLine("FileHeader", "some line");
        String[] newRes = dut.getSectionLines("FileHeader");
        assertNotNull(newRes);
        assertEquals(1, newRes.length);
        assertEquals("some line", newRes[0]);
    }

    @Test
    public void tesaddLineWithComment()
    {
        CFile dut = new CFile("dut");
        dut.addLineWithComment("FileHeader", "some line", "some comment");
        String[] newRes = dut.getSectionLines("FileHeader");
        assertNotNull(newRes);
        assertEquals(1, newRes.length);
        assertEquals("some line // some comment", newRes[0]);
    }


}
