
package de.nomagic.puzzler.FileGroup;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import de.nomagic.puzzler.progress.ProgressReport;

public class FileGroup
{
    private HashMap<String, AbstractFile> files = new HashMap<String,AbstractFile>();

    public FileGroup()
    {
    }

    public void add(AbstractFile aFile)
    {
        if(null != aFile)
        {
            files.put(aFile.getFileName(), aFile);
        }
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
        Iterator<Entry<String, AbstractFile>> it = files.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<String, AbstractFile> pair = it.next();
            if(false == pair.getValue().saveToFolder(folder, report))
            {
                return false;
            }
            // else ok
        }
        return true;
    }

    public Iterator<String> getFileIterator()
    {
        return files.keySet().iterator();
    }

    public AbstractFile getFileWithName(String fileName)
    {
        return files.get(fileName);
    }

    public void addAll(FileGroup otherGroup)
    {
        Iterator<String> it = otherGroup.getFileIterator();
        while(it.hasNext())
        {
            add(otherGroup.getFileWithName(it.next()));
        }
    }

}
