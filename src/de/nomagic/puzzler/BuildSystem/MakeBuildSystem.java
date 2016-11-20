
package de.nomagic.puzzler.BuildSystem;

import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.progress.ProgressReport;

public class MakeBuildSystem extends BuildSystem 
{
	public MakeBuildSystem(ProgressReport report) 
	{
		super(report);
	}

	public boolean createBuildFor(FileGroup files) 
	{
		if(null == cfg)
		{
			report.addError(this, "No Configuration provided !");
			return false;
		}
		report.addError(this, "Not implemented !");
		return false;
	}

}
