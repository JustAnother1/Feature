package de.nomagic.puzzler;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileGetterImplTest {

    @Test
    public void testGetXmlFileNull()
    {
        Context ctx = new ContextStub();
        FileGetter fg = new FileGetterImpl(ctx);
        String noString = null;
        assertNull(fg.getXmlFile(noString, null));
    }

    @Test
    public void tesTtryToGetXmlFileNull()
    {
        Context ctx = new ContextStub();
        FileGetter fg = new FileGetterImpl(ctx);
        String noString = null;
        assertNull(fg.tryToGetXmlFile(noString, null, false));
    }

    @Test
    public void tesTtryToGetXmlFileNoPathButName()
    {
        Context ctx = new ContextStub();
        FileGetter fg = new FileGetterImpl(ctx);
        String noString = null;
        assertNull(fg.tryToGetXmlFile(noString, "", false));
    }

    @Test
    public void tesTtryToGetXmlFileEmptyPathBut()
    {
        Context ctx = new ContextStub();
        FileGetter fg = new FileGetterImpl(ctx);
        assertNull(fg.tryToGetXmlFile("", "", false));
    }

    @Test
    public void testGetXmlFromString_null()
    {
        Context ctx = new ContextStub();
        FileGetter fg = new FileGetterImpl(ctx);
        assertNull(fg.getXmlFromString(null));
    }

    @Test
    public void testGetXmlFromString_empty()
    {
        Context ctx = new ContextStub();
        FileGetter fg = new FileGetterImpl(ctx);
        assertNull(fg.getXmlFromString(""));
    }

    @Test
    public void testGetXmlFromString_invalid()
    {
        Context ctx = new ContextStub();
        FileGetter fg = new FileGetterImpl(ctx);
        assertNull(fg.getXmlFromString("This is no XML!"));
    }

}
