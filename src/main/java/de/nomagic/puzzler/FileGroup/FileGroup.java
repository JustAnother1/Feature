
package de.nomagic.puzzler.FileGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.nomagic.puzzler.Context;

public class FileGroup
{
    private HashMap<String, AbstractFile> files = new HashMap<String,AbstractFile>();

    public FileGroup()
    {
    }

    public int numEntries()
    {
        return files.size();
    }

    public void add(AbstractFile aFile)
    {
        if(null != aFile)
        {
            files.put(aFile.getFileName(), aFile);
        }
    }

    public boolean saveToFolder(String folder, Context ctx)
    {
        File f = new File(folder);
        if(false == f.exists())
        {
            if(false == f.mkdirs())
            {
                ctx.addError(this, "could not create the output folder (" + folder + ")");
                return false;
            }
            // else ok
        }
        // else ok
        Iterator<Entry<String, AbstractFile>> it = files.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<String, AbstractFile> pair = it.next();
            if(false == pair.getValue().saveToFolder(folder, ctx))
            {
                return false;
            }
            // else ok
        }
        return true;
    }

    public boolean zipToStdout(String ProjectFolderName)
    {
        return zipToStream(System.out, ProjectFolderName);
    }

    private boolean zipToStream(OutputStream out, String ProjectFolderName)
    {
        boolean res = true;
        ZipOutputStream zipOut = null;
        try
        {
            zipOut = new ZipOutputStream(out);
            zipOut.setLevel(9);  // max compression
            Iterator<Entry<String, AbstractFile>> it = files.entrySet().iterator();
            while (it.hasNext())
            {
                Entry<String, AbstractFile> pair = it.next();

                ZipEntry zipEntry = new ZipEntry(ProjectFolderName  + File.separator + pair.getKey());
                zipOut.putNextEntry(zipEntry);

                AbstractFile af = pair.getValue();
                af.writeToStream(zipOut);
            }
        }
        catch (IOException e)
        {
            res = false;
            e.printStackTrace();
        }
        finally
        {
            try
            {
                zipOut.close();
            }
            catch (IOException e)
            {
                res = false;
                e.printStackTrace();
            }
        }
        return res;
    }

    public boolean saveToZip(String filename, String ProjectFolderName)
    {
        boolean res = true;
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(filename);
            zipToStream(fos, ProjectFolderName);
        }
        catch (IOException e)
        {
            res = false;
            e.printStackTrace();
        }
        finally
        {
            if(null != fos)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    res = false;
                    e.printStackTrace();
                }
            }
        }
        return res;
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
