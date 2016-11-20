
package de.nomagic.puzzler.FileGroup;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import de.nomagic.puzzler.progress.ProgressReport;

public class FileGroup 
{
	private HashMap<String, TextFile> files = new HashMap<String,TextFile>();

	public FileGroup() 
	{
	}

	public void add(TextFile aFile) 
	{
		files.put(aFile.getFileName(), aFile);
	}

	public boolean saveToFolder(String folder, ProgressReport report) 
	{
		File f = new File(folder);
		if(false == f.exists())
		{
			if(false == f.mkdirs())
			{
				report.addError(this, "could not create the output folder (" + folder + ")");
				return false;
			}
			// else ok
		}
		// else ok
		Iterator<Entry<String, TextFile>> it = files.entrySet().iterator();
	    while (it.hasNext()) 
	    {
	        Entry<String, TextFile> pair = it.next();
	        if(false == pair.getValue().saveToFolder(folder, report))
	        {
	        	return false;
	        }
	        // else ok
	    }
		return true;
	}

}
