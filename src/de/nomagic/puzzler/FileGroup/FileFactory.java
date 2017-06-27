package de.nomagic.puzzler.FileGroup;

import org.jdom2.Element;

public class FileFactory
{
    public final static String UNSTRUCTURED_TEXT_FILE = "unstructured";

    public final static String FILE_NAME_TAG = "filename";
    public final static String FILE_CONTENT_TAG = "content";

    public final static String UNSTRUCTURED_TEXT_SECTION = "unstructured";

    public static AbstractFile getFileFromXml(Element xml)
    {
        if(null == xml)
        {
            return null;
        }

        String name = xml.getName();
        if(true == UNSTRUCTURED_TEXT_FILE.equals(name))
        {
            return constructUnstructuredTextFileFrom(xml);
        }

        return null;
    }

    private static AbstractFile constructUnstructuredTextFileFrom(Element xml)
    {
        TextFile res = new TextFile(xml.getChildText(FILE_NAME_TAG));
        res.createSection(UNSTRUCTURED_TEXT_SECTION);
        res.addLine(UNSTRUCTURED_TEXT_SECTION, xml.getChildText(FILE_CONTENT_TAG));
        return res;
    }

}
