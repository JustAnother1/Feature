package de.nomagic.puzzler.FileGroup;

import org.junit.Test;

public class C_FileTest
{

    @Test
    public void testAddContentsOf_null()
    {
        C_File dut = new C_File(null);
        dut.addContentsOf(null);
    }

    @Test
    public void testAddContentsOf()
    {
        C_File emptyFile = new C_File("empty");
        C_File dut = new C_File("dut");
        dut.addContentsOf(emptyFile);
    }

}
