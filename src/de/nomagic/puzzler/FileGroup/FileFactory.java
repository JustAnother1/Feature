package de.nomagic.puzzler.FileGroup;

import org.jdom2.Element;

public class FileFactory
{
    public final static String UNSTRUCTURED_TEXT_SECTION = "unstructured";
    public final static String ALGORITHM_ADDITIONAL_FILE_NAME_ATTRIBUTE = "name";

    public static AbstractFile getFileFromXml(Element xml)
    {
        if(null == xml)
        {
            return null;
        }

        TextFile addFile = new TextFile(xml.getAttributeValue(ALGORITHM_ADDITIONAL_FILE_NAME_ATTRIBUTE));
        addFile.createSection(UNSTRUCTURED_TEXT_SECTION);
        addFile.addLine(UNSTRUCTURED_TEXT_SECTION, xml.getText());
        return addFile;
    }

}
