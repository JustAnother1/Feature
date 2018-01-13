package de.nomagic.puzzler;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class ToolTest {

    @Test
    public void testCurentDateTime()
    {
        String date = Tool.curentDateTime();
        assertNotNull(date);
        // "yyyy-MM-dd HH:mm:ss"
        assertTrue(date.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    public void testFromExceptionToString_null()
    {
        assertEquals("Exception [null]", Tool.fromExceptionToString(null));
    }

    @Test
    public void testFromExceptionToString()
    {
        Throwable e = new Throwable("TestException");
        StackTraceElement[] stackTrace = new StackTraceElement[2];
        stackTrace[0] = new StackTraceElement("class2", "method2", "file2", 23);
        stackTrace[1] = new StackTraceElement("class1", "method1", "file1", 42);
        e.setStackTrace(stackTrace);
        assertEquals("Exception [TestException\n"
                + "java.lang.Throwable: TestException\n"
                + "\tat class2.method2(file2:23)\n"
                + "\tat class1.method1(file1:42)\n"
                + "]", Tool.fromExceptionToString(e));
    }

    @Test
    public void testFromByteBufferToHexString_int_null()
    {
        int[] buf = null;
        assertEquals("[]", Tool.fromByteBufferToHexString(buf));
    }

    @Test
    public void testFromByteBufferToHexString_int()
    {
        int[] buf = new int[0];
        assertEquals("[]", Tool.fromByteBufferToHexString(buf));
    }

    @Test
    public void testFromByteBufferToHexString_byte_null()
    {
        byte[] buf = null;
        assertEquals("[]", Tool.fromByteBufferToHexString(buf));
    }

    @Test
    public void testFromByteBufferToHexString_byte()
    {
        byte[] buf = new byte[0];
        assertEquals("[]", Tool.fromByteBufferToHexString(buf));
    }

    @Test
    public void testFromByteBufferToHexString_byte_length()
    {
        byte[] buf = new byte[2];
        buf[0] = (byte)0xaf;
        buf[1] = (byte)0xfe;
        assertEquals("[AF]", Tool.fromByteBufferToHexString(buf, 1));
    }

    @Test
    public void testFromByteBufferToHexString_byte_len_off_null()
    {
        byte[] buf = null;
        assertEquals("[]", Tool.fromByteBufferToHexString(buf, 10, 5));
    }

    @Test
    public void testFromByteBufferToHexString_byte_len_off()
    {
        byte[] buf = new byte[2];
        buf[0] = (byte)0xaf;
        buf[1] = (byte)0xfe;
        assertEquals("[FE]", Tool.fromByteBufferToHexString(buf, 1, 1));
    }

    @Test
    public void testFromByteBufferToHexString_byte_len_off_long()
    {
        byte[] buf = new byte[6];
        buf[0] = (byte)0xaf;
        buf[1] = (byte)0xfe;
        buf[2] = (byte)0xaf;
        buf[3] = (byte)0xfe;
        buf[4] = (byte)0xaf;
        buf[5] = (byte)0xfe;
        assertEquals("[FE AF FE]", Tool.fromByteBufferToHexString(buf, 3, 1));
    }

    @Test
    public void testFromByteBufferToUtf8String_null()
    {
        assertEquals("[]", Tool.fromByteBufferToUtf8String(null));
    }

    @Test
    public void testFromByteBufferToUtf8String()
    {
        byte[] string = new byte[12];
        string[ 0] = (byte)0x48; // H
        string[ 1] = (byte)0x65; // e
        string[ 2] = (byte)0x6c; // l
        string[ 3] = (byte)0x6c; // l
        string[ 4] = (byte)0x6f; // o
        string[ 5] = (byte)0x20; //
        string[ 6] = (byte)0x57; // W
        string[ 7] = (byte)0x6f; // o
        string[ 8] = (byte)0x72; // r
        string[ 9] = (byte)0x6c; // l
        string[10] = (byte)0x64; // d
        string[11] = (byte)0x21; // !
        assertEquals("[Hello World!]", Tool.fromByteBufferToUtf8String(string));
    }

    @Test
    public void testFromByteBufferToUtf8String_len_off()
    {
        byte[] string = new byte[12];
        string[ 0] = (byte)0x48; // H
        string[ 1] = (byte)0x65; // e
        string[ 2] = (byte)0x6c; // l
        string[ 3] = (byte)0x6c; // l
        string[ 4] = (byte)0x6f; // o
        string[ 5] = (byte)0x20; //
        string[ 6] = (byte)0x57; // W
        string[ 7] = (byte)0x6f; // o
        string[ 8] = (byte)0x72; // r
        string[ 9] = (byte)0x6c; // l
        string[10] = (byte)0x64; // d
        string[11] = (byte)0x21; // !
        assertEquals("[lo Wo]", Tool.fromByteBufferToUtf8String(string, 5, 3));
    }

    @Test
    public void testIsValidChar()
    {
        assertTrue(Tool.isValidChar('a'));
        assertTrue(Tool.isValidChar('b'));
        assertTrue(Tool.isValidChar('c'));
        assertTrue(Tool.isValidChar('d'));
        assertTrue(Tool.isValidChar('e'));
        assertTrue(Tool.isValidChar('f'));
        assertTrue(Tool.isValidChar('g'));
        assertTrue(Tool.isValidChar('h'));
        assertTrue(Tool.isValidChar('i'));
        assertTrue(Tool.isValidChar('j'));
        assertTrue(Tool.isValidChar('k'));
        assertTrue(Tool.isValidChar('l'));
        assertTrue(Tool.isValidChar('m'));
        assertTrue(Tool.isValidChar('n'));
        assertTrue(Tool.isValidChar('o'));
        assertTrue(Tool.isValidChar('p'));
        assertTrue(Tool.isValidChar('q'));
        assertTrue(Tool.isValidChar('r'));
        assertTrue(Tool.isValidChar('s'));
        assertTrue(Tool.isValidChar('t'));
        assertTrue(Tool.isValidChar('u'));
        assertTrue(Tool.isValidChar('v'));
        assertTrue(Tool.isValidChar('w'));
        assertTrue(Tool.isValidChar('x'));
        assertTrue(Tool.isValidChar('y'));
        assertTrue(Tool.isValidChar('z'));
        assertTrue(Tool.isValidChar('A'));
        assertTrue(Tool.isValidChar('B'));
        assertTrue(Tool.isValidChar('C'));
        assertTrue(Tool.isValidChar('D'));
        assertTrue(Tool.isValidChar('E'));
        assertTrue(Tool.isValidChar('F'));
        assertTrue(Tool.isValidChar('G'));
        assertTrue(Tool.isValidChar('H'));
        assertTrue(Tool.isValidChar('I'));
        assertTrue(Tool.isValidChar('J'));
        assertTrue(Tool.isValidChar('K'));
        assertTrue(Tool.isValidChar('L'));
        assertTrue(Tool.isValidChar('M'));
        assertTrue(Tool.isValidChar('N'));
        assertTrue(Tool.isValidChar('O'));
        assertTrue(Tool.isValidChar('P'));
        assertTrue(Tool.isValidChar('Q'));
        assertTrue(Tool.isValidChar('R'));
        assertTrue(Tool.isValidChar('S'));
        assertTrue(Tool.isValidChar('T'));
        assertTrue(Tool.isValidChar('U'));
        assertTrue(Tool.isValidChar('V'));
        assertTrue(Tool.isValidChar('W'));
        assertTrue(Tool.isValidChar('X'));
        assertTrue(Tool.isValidChar('Y'));
        assertTrue(Tool.isValidChar('Z'));
        assertTrue(Tool.isValidChar('0'));
        assertTrue(Tool.isValidChar('1'));
        assertTrue(Tool.isValidChar('2'));
        assertTrue(Tool.isValidChar('3'));
        assertTrue(Tool.isValidChar('4'));
        assertTrue(Tool.isValidChar('5'));
        assertTrue(Tool.isValidChar('6'));
        assertTrue(Tool.isValidChar('7'));
        assertTrue(Tool.isValidChar('8'));
        assertTrue(Tool.isValidChar('9'));
        assertTrue(Tool.isValidChar(' '));
        assertFalse(Tool.isValidChar('!'));
        assertFalse(Tool.isValidChar('@'));
        assertFalse(Tool.isValidChar('*'));
    }


    @Test
    public void testOnlyAllowedChars()
    {
        assertEquals("bla bla", Tool.onlyAllowedChars("!!!bla bla!!!"));
    }

    @Test
    public void testGetStacTrace()
    {
        assertNotNull(Tool.getStacTrace());
    }

    @Test
    public void testValidatePath_null()
    {
        assertEquals("", Tool.validatePath(null));
    }

    @Test
    public void testValidatePath_empty()
    {
        assertEquals("", Tool.validatePath(""));
    }

    @Test
    public void testValidatePath_wrongEnd()
    {
        assertEquals("bla" + File.separator, Tool.validatePath("bla"));
    }

    @Test
    public void testValidatePath_ok()
    {
        assertEquals("bla" + File.separator, Tool.validatePath("bla" + File.separator));
    }
}
