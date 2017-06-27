
package de.nomagic.puzzler.Generator;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.progress.ProgressReport;
import de.nomagic.puzzler.solution.Solution;

public abstract class Generator extends Base
{
    public Generator(ProgressReport report)
    {
        super(report);
    }

    public abstract FileGroup generateFor(Solution s, Environment e);

}
