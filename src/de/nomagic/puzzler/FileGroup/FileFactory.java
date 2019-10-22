package de.nomagic.puzzler.FileGroup;

import org.jdom2.Element;

public final class FileFactory
{
    public final static String UNSTRUCTURED_TEXT_SECTION = "unstructured";
    public final static String ALGORITHM_ADDITIONAL_FILE_NAME_ATTRIBUTE = "name";

    private FileFactory()
    {
    }

    public static AbstractFile getFileFromXml(Element xml)
    {
        if(null == xml)
        {
            return null;
        }
        
        String name = xml.getAttributeValue(ALGORITHM_ADDITIONAL_FILE_NAME_ATTRIBUTE);
        TextFile addFile;
        
        if(true == name.endsWith(".c"))
        {
            // C-File            
            addFile = new CFile(name);
            
        }
        else if(true == name.endsWith(".v"))
        {
            // Verilog-File            
            addFile = new VerilogFile(name);
        }
        else
        {
            addFile = new TextFile(name);
        }
        
        addFile.createSection(UNSTRUCTURED_TEXT_SECTION);
        addFile.addLine(UNSTRUCTURED_TEXT_SECTION, xml.getText());
        return addFile;
    }

}
