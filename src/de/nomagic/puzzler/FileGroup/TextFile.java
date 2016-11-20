
package de.nomagic.puzzler.FileGroup;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.progress.ProgressReport;

public class TextFile 
{
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private HashMap<String, Vector<String>> sectionData = new HashMap<String,Vector<String>>();
	private Vector<String> sections = new Vector<String>();
	private String fileName;

	public TextFile(String filename) 
	{
		this.fileName = filename;
	}
	
	public String getFileName() 
	{
		return fileName;
	}

	public void createSections(String[] newSections) 
	{
		for(int i = 0; i < newSections.length; i++)
		{
			sections.add(newSections[i]);
			sectionData.put(newSections[i], new Vector<String>());
		}
	}

	public void addLines(String sectionName, String[] lines) 
	{
		Vector<String> curSection = sectionData.get(sectionName);
		for(int i = 0; i < lines.length; i++)
		{
			curSection.add(lines[i]);
		}
	}

	public boolean saveToFolder(String folder, ProgressReport report)
	{
		FileOutputStream fout;
		try 
		{
			fout = new FileOutputStream(folder + fileName);
			for(int sec = 0; sec < sections.size(); sec++)
			{
				String curSection = sections.get(sec);
				Vector<String> curData = sectionData.get(curSection);
				for(int i = 0; i < curData.size(); i++)
				{
					fout.write((curData.get(i) + "\n").getBytes());
				}
			}
			fout.close();
			return true;
		}
		catch (FileNotFoundException e) 
		{
			report.addError("TextFile(" + fileName + ")", e.getMessage());
			log.trace(Tool.fromExceptionToString(e));
		} 
		catch (IOException e) 
		{
			report.addError("TextFile(" + fileName + ")", e.getMessage());
			log.trace(Tool.fromExceptionToString(e));
		}
		return false;
	}
}
