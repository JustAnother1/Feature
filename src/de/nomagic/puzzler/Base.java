package de.nomagic.puzzler;

import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.progress.ProgressReport;

public abstract class Base 
{
	protected Configuration cfg;
	protected ProgressReport report;
	
	public Base(ProgressReport report) 
	{
		this.report = report;
	}

	public void setConfiguration(Configuration cfg) 
	{
		this.cfg = cfg;
	}

}
