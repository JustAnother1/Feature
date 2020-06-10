package de.nomagic.puzzler.BuildSystem;

import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.FileGroup;

public class NoBuildSystem implements BuildSystemApi
{

    public NoBuildSystem()
    {
        // nothing to do here
    }

    @Override
    public boolean hasTargetFor(String source)
    {
        return true;
    }

    @Override
    public void addTarget(Target aTarget)
    {
        // nothing to do here
    }

    @Override
    public void extendListVariable(String list, String newElement)
    {
        // nothing to do here
    }

    @Override
    public void addRequiredVariable(String string)
    {
        // nothing to do here
    }

    @Override
    public void addVariable(String variName, String variValue)
    {
        // nothing to do here
    }

    @Override
    public void addFile(AbstractFile newFile)
    {
        // nothing to do here
    }

    @Override
    public FileGroup createBuildFor(FileGroup files)
    {
        // we do not need to add anything
        return files;
    }

}
