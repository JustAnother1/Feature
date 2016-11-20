
package de.nomagic.puzzler.BuildSystem;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.progress.ProgressReport;

public abstract class BuildSystem extends Base
{
	public BuildSystem(ProgressReport report) 
	{
		super(report);
	}
	
	public abstract boolean createBuildFor(FileGroup files);

}
