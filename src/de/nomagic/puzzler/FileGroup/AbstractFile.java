package de.nomagic.puzzler.FileGroup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.BuildSystem.BuildSystemAddApi;
import de.nomagic.puzzler.progress.ProgressReport;

public abstract class AbstractFile
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    protected String fileName;

    public AbstractFile(String filename)
    {
        this.fileName = filename;
    }

    public String getFileName()
    {
        return fileName;
    }

    public boolean saveToFolder(String folder, ProgressReport report)
    {
        FileOutputStream fout;
        try
        {
            fout = new FileOutputStream(folder + File.separator + fileName);
            writeToStream(fout);
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

    protected abstract void writeToStream(OutputStream out) throws IOException;

    public void addToBuild(BuildSystemAddApi BuildSystem)
    {
        // nothing to do.
        // Override if something needs to be done.
    }
}
