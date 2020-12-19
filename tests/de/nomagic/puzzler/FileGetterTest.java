package de.nomagic.puzzler;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileGetterTest {

    @Test
    public void testGetXmlFileNull()
    {
        FileGetter fg = new FileGetter(null);
        String noString = null;
        assertNull(fg.getXmlFile(noString, null));
    }

    @Test
    public void tesTtryToGetXmlFileNull()
    {
        FileGetter fg = new FileGetter(null);
        String noString = null;
        assertNull(fg.tryToGetXmlFile(noString, null, false));
    }

    @Test
    public void tesTtryToGetXmlFileNoPathButName()
    {
        FileGetter fg = new FileGetter(null);
        String noString = null;
        assertNull(fg.tryToGetXmlFile(noString, "", false));
    }

    @Test
    public void tesTtryToGetXmlFileEmptyPathBut()
    {
        FileGetter fg = new FileGetter(null);
        assertNull(fg.tryToGetXmlFile("", "", false));
    }

}
