package de.nomagic.puzzler.FileGroup;

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

}
