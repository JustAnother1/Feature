
package de.nomagic.puzzler.FileGroup;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextFile extends AbstractFile
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private HashMap<String, Vector<String>> sectionData = new HashMap<String,Vector<String>>();
    private Vector<String> sections = new Vector<String>();
    private boolean addSeperation = false;

    public TextFile(String filename)
    {
        super(filename);
    }

    public void createSection(String newSection)
    {
        sections.add(newSection);
        sectionData.put(newSection, new Vector<String>());
    }

    public void createSections(String[] newSections)
    {
        for(int i = 0; i < newSections.length; i++)
        {
            createSection(newSections[i]);
        }
    }

    public void addLine(String sectionName, String line)
    {
        String[] arr = {line};
        addLines(sectionName, arr);
    }

    public void addLines(String sectionName, String[] lines)
    {
        Vector<String> curSection = sectionData.get(sectionName);
        if(null == curSection)
        {
            log.error("Tried to add lines to the invalid section (" + sectionName + ")!");
        }
        else
        {
            for(int i = 0; i < lines.length; i++)
            {
                curSection.add(lines[i]);
            }
        }
    }

    protected void writeToStream(OutputStream out) throws IOException
    {
        for(int sec = 0; sec < sections.size(); sec++)
        {
            String curSection = sections.get(sec);
            Vector<String> curData = sectionData.get(curSection);
            curData = prepareSectionData(curSection, curData);
            for(int i = 0; i < curData.size(); i++)
            {
                out.write((curData.get(i) + "\n").getBytes());
            }
            if((true == addSeperation) && (0 < curData.size()))
            {
                out.write("\n".getBytes());
            }
        }
    }

    protected Vector<String>  prepareSectionData(String sectionName, Vector<String> sectionData)
    {
        // Nothing to do here
        return sectionData;
    }

    public void separateSectionWithEmptyLine(boolean b)
    {
        addSeperation = b;
    }

}
