
package de.nomagic.puzzler.FileGroup;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextFile extends AbstractFile
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private HashMap<String, List<String>> sectionData = new HashMap<String,List<String>>();
    protected List<String> sections = new LinkedList<String>();
    private boolean addSeperation = false;

    public TextFile(String filename)
    {
        super(filename);
    }

    public void createSection(String newSection)
    {
        sections.add(newSection);
        sectionData.put(newSection, new LinkedList<String>());
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
        List<String> curSection = sectionData.get(sectionName);
        if(null == curSection)
        {
            log.error("Tried to add lines to the invalid section ({})!", sectionName);
        }
        else
        {
            Collections.addAll(curSection, lines);
        }
    }

    public void writeToStream(OutputStream out) throws IOException
    {
        for(int sec = 0; sec < sections.size(); sec++)
        {
            String curSection = sections.get(sec);
            List<String> curData = sectionData.get(curSection);
            curData = prepareSectionData(curSection, curData);
            for(int i = 0; i < curData.size(); i++)
            {
                if(i == curData.size() -1)
                {
                    // last line
                    String line = curData.get(i);
                    String lines[] = line.split("\\r?\\n");
                    for(int j = 0; j < lines.length; j++)
                    {
                        if(j == lines.length -1)
                        {
                            if(0 < lines[j].length())
                            {
                                out.write((lines[j] + getLineSperator()).getBytes());
                            }
                            else
                            {
                                // skip empty line
                            }
                        }
                        else
                        {
                            out.write((lines[j] + getLineSperator()).getBytes());
                        }
                    }
                }
                else
                {
                    out.write((curData.get(i) + getLineSperator()).getBytes());
                }
            }
            if((true == addSeperation) && (false == curData.isEmpty()))
            {
                out.write(getLineSperator().getBytes());
            }
        }
    }

    protected List<String>  prepareSectionData(String sectionName, List<String> sectionData)
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
        List<String> sect = sectionData.get(sectionName);
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
