
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
    protected Vector<String> sections = new Vector<String>();
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

    public void writeToStream(OutputStream out) throws IOException
    {
        for(int sec = 0; sec < sections.size(); sec++)
        {
            String curSection = sections.get(sec);
            Vector<String> curData = sectionData.get(curSection);
            curData = prepareSectionData(curSection, curData);
            for(int i = 0; i < curData.size(); i++)
            {
                out.write((curData.get(i) + getLineSperator()).getBytes());
            }
            if((true == addSeperation) && (0 < curData.size()))
            {
                out.write(getLineSperator().getBytes());
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

    public String[] getSectionLines(String sectionName)
    {
        Vector<String> sect = sectionData.get(sectionName);
        if(null == sect)
        {
            return new String[0];
        }
        else
        {
            return sect.toArray(new String[0]);
        }
    }

    public void addContentsOf(CFile otherFile)
    {
        if(null == otherFile)
        {
            return;
        }
        if(null == otherFile.sections)
        {
            return;
        }
        for(int i = 0; i < otherFile.sections.size(); i++)
        {
            String secName = otherFile.sections.get(i);
            String[] secLines = otherFile.getSectionLines(secName);
            this.addLines(secName, secLines);
        }
    }

    public String getLineSperator()
    {
        return System.getProperty("line.separator");
    }

}
