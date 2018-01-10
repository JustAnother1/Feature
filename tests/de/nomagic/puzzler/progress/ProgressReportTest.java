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
    public void testAddError_null()
    {
        ProgressReport dut = new ProgressReport();
        dut.addError(null, null);
    }

    @Test
    public void testAddError_noSource()
    {
        ProgressReport dut = new ProgressReport();
        dut.addError(null, "Boom!");
    }

    @Test
    public void testAddError_Source()
    {
        ProgressReport dut = new ProgressReport();
        dut.addError(dut, "Boom!");
    }

}
