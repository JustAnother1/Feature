package de.nomagic.puzzler;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileGetterTest {

    @Test
    public void testGetXmlFileNull()
    {
        String noString = null;
        assertNull(FileGetter.getXmlFile(noString, null, null));
    }

    @Test
    public void testGetXmlFileArrayNull()
    {
        String[] noStrings = null;
        assertNull(FileGetter.getXmlFile(noStrings, null, null));
    }

    @Test
    public void tesTtryToGetXmlFileNull()
    {
        String noString = null;
        assertNull(FileGetter.tryToGetXmlFile(noString, null, false, null));
    }

    @Test
    public void tesTtryToGetXmlFileArrayNull()
    {
        String[] noStrings = null;
        assertNull(FileGetter.tryToGetXmlFile(noStrings, null, false, null));
    }

    @Test
    public void tesTtryToGetXmlFileNoPathButName()
    {
        String noString = null;
        assertNull(FileGetter.tryToGetXmlFile(noString, "", false, null));
    }

    @Test
    public void tesTtryToGetXmlFileEmptyPathBut()
    {
        assertNull(FileGetter.tryToGetXmlFile("", "", false, null));
    }

}
