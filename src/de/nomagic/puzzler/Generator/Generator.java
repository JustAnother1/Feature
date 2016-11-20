
package de.nomagic.puzzler.Generator;

import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.Solution;

public abstract class Generator 
{
	protected Configuration cfg;

	public Generator() 
	{
		// TODO Auto-generated constructor stub
	}
	
	public void setConfiguration(Configuration cfg) 
	{
		this.cfg = cfg;
	}
	
	public abstract FileGroup generateFor(Solution s);

}
