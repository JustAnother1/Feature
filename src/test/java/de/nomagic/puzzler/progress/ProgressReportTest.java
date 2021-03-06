package de.nomagic.puzzler.progress;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProgressReportTest
{

    @Test
    public void testClose()
    {
        ProgressReport dut = new ProgressReport();
        dut.close();
    }

    @Test
    public void testWasSucessful()
    {
        ProgressReport dut = new ProgressReport();
        assertTrue(dut.wasSucessful());
        dut.addError("me", "Urg!");
        assertFalse(dut.wasSucessful());
        dut.close();
    }

    @Test
    public void testAddErrorNull()
    {
        ProgressReport dut = new ProgressReport();
        dut.addError(null, null);
    }

    @Test
    public void testAddErrorNoSource()
    {
        ProgressReport dut = new ProgressReport();
        dut.addError(null, "Boom!");
    }

    @Test
    public void testAddErrorSource()
    {
        ProgressReport dut = new ProgressReport();
        dut.addError(dut, "Boom!");
    }

    @Test
    public void test_getAllReports_empty()
    {
        ProgressReport dut = new ProgressReport();
        String res = dut.getAllReports();
        assertEquals("", res);
    }

    @Test
    public void test_getAllReports()
    {
        ProgressReport dut = new ProgressReport();
        dut.addError("bomb", "Boom!");
        String res = dut.getAllReports();
        assertEquals("ERROR(bomb) : Boom!\n", res);
    }
}
