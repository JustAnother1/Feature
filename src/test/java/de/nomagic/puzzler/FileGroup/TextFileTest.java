package de.nomagic.puzzler.FileGroup;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class TextFileTest
{

    @Test
    public void testgetLineSperator()
    {
        TextFile dut = new TextFile("main.c");
        assertEquals(System.getProperty("line.separator"), dut.getLineSperator());
    }

    @Test
    public void testWriteToStream_No_Data()
    {
        TextFile dut = new TextFile("main.c");
        dut.createSection("bla");
        dut.addLine("bla", null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            dut.writeToStream(out);
        }
        catch (IOException e)
        {
            fail("IOException");
        }
        byte[] res = out.toByteArray();
        String content = new String(res, StandardCharsets.UTF_8);
        assertEquals("", content);
    }

    @Test
    public void testWriteToStream_nul()
    {
        TextFile dut = new TextFile("main.c");
        dut.createSection("bla");
        dut.addLine("bla", "blubb");
        try
        {
            dut.writeToStream(null);
        }
        catch (IOException e)
        {
            fail("exception");
        }
        // OK
    }

}
