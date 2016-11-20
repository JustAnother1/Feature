
package de.nomagic.puzzler.Environment;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.FileGetter;
import de.nomagic.puzzler.Project;
import de.nomagic.puzzler.PuzzlerMain;
import de.nomagic.puzzler.progress.ProgressReport;

public class Environment extends Base 
{
	public final static String EXTERNAL_REFFERENCE_ATTRIBUTE_NAME = "ref";
	public final static String EXTERNAL_REFFERENCE_ROOT_ELEMENT_NAME = "environment";
	
	private Element EnvironmentRoot = null;
	private Document externalReferenceDocument = null;
	private Element extRefEleemnt = null;
	
	public Environment(ProgressReport report)
	{
		super(report);
	}

	public boolean getFromProject(Project pro) 
	{
		if(null == pro)
		{		
			report.addError(this, "No Project provided !");
			return false;
		}
		
		EnvironmentRoot = pro.getEnvironmentElement();
		if(null == EnvironmentRoot)
		{		
			report.addError(this, "No Environment Tag in Project !");
			return false;
		}
		
		if(true == EnvironmentRoot.hasAttributes())
		{
			Attribute attr = EnvironmentRoot.getAttribute(EXTERNAL_REFFERENCE_ATTRIBUTE_NAME);
			if(null != attr)
			{
				String externalReferenceFileName = attr.getValue();
				if(null == externalReferenceFileName)
				{
					report.addError(this, "Invalid external reference !");
					return false;
				}
				// read external Reference
				externalReferenceDocument = FileGetter.getXmlFile(cfg.getString(PuzzlerMain.ROOT_PATH_CFG), externalReferenceFileName, report);
				if(null == externalReferenceDocument)
				{
					report.addError(this, "Could not read referenced File " + externalReferenceFileName);
					return false;
				}
				
				extRefEleemnt  = externalReferenceDocument.getRootElement();
				if(null == extRefEleemnt)
				{
					report.addError(this, "Could not read Root Element from " + externalReferenceFileName);
					return false;
				}
				
				if(false == EXTERNAL_REFFERENCE_ROOT_ELEMENT_NAME.equals(extRefEleemnt.getName()))
				{
					report.addError(this, "Environment File " + externalReferenceFileName 
							+ " has an invalid root tag (" + extRefEleemnt.getName() + ") !");
					return false;
				}
			}
			else
			{
				// no external Reference - all data in this node
			}
		}
		
		return true;
	}

	public boolean provides(String name) 
	{
		if(null != EnvironmentRoot)
		{
			Element child = EnvironmentRoot.getChild(name);
			if(null != child)
			{
				return true;
			}
		}
		if(null != extRefEleemnt)
		{
			Element child = extRefEleemnt.getChild(name);
			if(null != child)
			{
				return true;
			}
		}
		report.addError(this, "The device " + name + " could not be found in the environment !");
		return false;
	}


}
