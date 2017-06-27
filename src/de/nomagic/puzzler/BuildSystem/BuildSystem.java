
package de.nomagic.puzzler.BuildSystem;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.progress.ProgressReport;

public abstract class BuildSystem extends Base
{
    public BuildSystem(ProgressReport report)
    {
        super(report);
    }

    public abstract FileGroup createBuildFor(FileGroup files, Environment e);

}
