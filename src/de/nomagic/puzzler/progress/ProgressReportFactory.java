
package de.nomagic.puzzler.progress;

import de.nomagic.puzzler.configuration.Configuration;

public class ProgressReportFactory 
{

	public static ProgressReport getReportFor(Configuration cfg) 
	{
		return new ProgressReport();
	}


}
