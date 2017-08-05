
package de.nomagic.puzzler.Generator;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGroup.FileGroup;

public abstract class Generator extends Base
{
    public Generator(Context ctx)
    {
        super(ctx);
    }

    public abstract FileGroup generateFor();

}
