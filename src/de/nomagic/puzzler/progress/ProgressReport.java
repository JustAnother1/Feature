
package de.nomagic.puzzler.progress;

/** Reports the progress to System.out and System.err
 * 
 *
 */
public class ProgressReport 
{

	public ProgressReport() 
	{
		// nothing to do here
	}

	public void close() 
	{
		// nothing to do here
	}

	public void setSucessful() 
	{
		// nothing to do here (No news are good news!)
	}

	public void addError(Object ref, String msg) 
	{
		if(ref instanceof String)
		{
			String name = (String)ref;
			System.err.println("ERROR(" + name + ") : " + msg);
		}
		else
		{
			System.err.println("ERROR(" + ref.getClass().getName() + ") : " + msg);
		}
	}

}
