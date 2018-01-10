package de.nomagic.puzzler.FileGroup;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileFactoryTest
{

    @Test
    public void testGetFileFromXml()
    {
        assertNull(FileFactory.getFileFromXml(null));
    }

}
