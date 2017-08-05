
package de.nomagic.puzzler.BuildSystem;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGroup.FileGroup;

public abstract class BuildSystem extends Base
{
    public BuildSystem(Context ctx)
    {
        super(ctx);
    }

    public abstract FileGroup createBuildFor(FileGroup files);

}
