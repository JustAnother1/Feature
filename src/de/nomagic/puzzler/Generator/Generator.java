
package de.nomagic.puzzler.Generator;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.ConfiguredAlgorithm;

public abstract class Generator extends Base
{
    public Generator(Context ctx)
    {
        super(ctx);
    }

    public abstract FileGroup generateFor(ConfiguredAlgorithm logic);

    public abstract void configure(Configuration cfg);

    public abstract String getLanguageName();

}
