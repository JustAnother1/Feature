package de.nomagic.puzzler;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileGetterTest {

    @Test
    public void testGetXmlFile_null()
    {
        String noString = null;
        assertNull(FileGetter.getXmlFile(noString, null, null));
    }

    @Test
    public void testGetXmlFile_array_null()
    {
        String[] noStrings = null;
        assertNull(FileGetter.getXmlFile(noStrings, null, null));
    }

    @Test
    public void tesTtryToGetXmlFile_null()
    {
        String noString = null;
        assertNull(FileGetter.tryToGetXmlFile(noString, null, false, null));
    }

    @Test
    public void tesTtryToGetXmlFile_array_null()
    {
        String[] noStrings = null;
        assertNull(FileGetter.tryToGetXmlFile(noStrings, null, false, null));
    }

}
