package de.nomagic.puzzler.FileGroup;

import java.io.IOException;
import java.io.OutputStream;

public class EmptyFolder extends AbstractFile
{

    public EmptyFolder(String filename)
    {
        super(filename);
        if(false == this.fileName.endsWith("/"))
        {
            // make sure that the name ends with a slash to mark it as being a folder.
            this.fileName = this.fileName + "/";
        }
    }

    @Override
    public void writeToStream(OutputStream out) throws IOException
    {
        // nothing to do here
    }
}
