
package de.nomagic.puzzler.BuildSystem;

import java.util.HashMap;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.FileGroup;

public abstract class BuildSystem extends Base implements BuildSystemApi
{
    protected FileGroup buildFiles = new FileGroup();
    protected HashMap<String, String> requiredEnvironmentVariables = new HashMap<String, String>();

    public BuildSystem(Context ctx)
    {
        super(ctx);
    }

    @Override
    public void addFile(AbstractFile newFile)
    {
        buildFiles.add(newFile);
    }
}
