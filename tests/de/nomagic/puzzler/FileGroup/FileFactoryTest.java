package de.nomagic.puzzler.FileGroup;

import static org.junit.Assert.*;

import org.jdom2.Element;
import org.junit.Test;

public class FileFactoryTest
{

    @Test
    public void testGetFileFromXmlNull()
    {
        assertNull(FileFactory.getFileFromXml(null));
    }

    @Test
    public void testGetFileFromXml()
    {
        Element e = new Element("bla");
        e.setAttribute(FileFactory.ALGORITHM_ADDITIONAL_FILE_NAME_ATTRIBUTE, "file42");
        AbstractFile res = FileFactory.getFileFromXml(e);
        assertNotNull(res);
        assertEquals("file42", res.getFileName());
    }

}
