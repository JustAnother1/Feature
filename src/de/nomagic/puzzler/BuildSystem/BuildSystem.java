
package de.nomagic.puzzler.BuildSystem;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;

public abstract class BuildSystem extends Base implements BuildSystemApi
{
    public BuildSystem(Context ctx)
    {
        super(ctx);
    }
}
