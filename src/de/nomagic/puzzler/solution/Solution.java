
package de.nomagic.puzzler.solution;

import java.util.HashMap;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.FileGetter;
import de.nomagic.puzzler.Project;
import de.nomagic.puzzler.PuzzlerMain;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.progress.ProgressReport;

public class Solution extends Base
{
	public final static String EXTERNAL_REFFERENCE_ATTRIBUTE_NAME = "ref";
	public final static String EXTERNAL_REFFERENCE_ROOT_ELEMENT_NAME = "solution";
	public final static String SOLUTION_FILE_CFG = "solutionFile";
	
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private HashMap<String, Algorithm> referencedAlgorithms = new HashMap<String,Algorithm>();
	
	private Element SolutionRoot = null;
	private Document externalReferenceDocument = null;
	private Element extRefEleemnt = null;
	
	public Solution(ProgressReport report) 
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
		
		SolutionRoot = pro.getSolutionElement();
		if(null == SolutionRoot)
		{		
			report.addError(this, "No Solution Tag in Project !");
			return false;
		}
		
		if(true == SolutionRoot.hasAttributes())
		{
			Attribute attr = SolutionRoot.getAttribute(EXTERNAL_REFFERENCE_ATTRIBUTE_NAME);
			if(null != attr)
			{
				String externalReferenceFileName = attr.getValue();
				if(null == externalReferenceFileName)
				{
					report.addError(this, "Invalid external reference !");
					return false;
				}
				// read external Reference
				externalReferenceDocument = FileGetter.getXmlFile(cfg.getString(PuzzlerMain.ROOT_PATH_CFG), 
						                                          externalReferenceFileName, report);
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
					report.addError(this, "Solution File " + externalReferenceFileName 
							+ " has an invalid root tag (" + extRefEleemnt.getName() + ") !");
					return false;
				}
				cfg.setString(SOLUTION_FILE_CFG, externalReferenceFileName);
			}
			else
			{
				// no external Reference - all data in this node
				cfg.setString(SOLUTION_FILE_CFG, cfg.getString(Project.PROJECT_FILE_CFG));
			}
		}
		
		return true;
	}
	
	private boolean checkAndTestElement(Element curElement, Environment e)
	{
		if(true == curElement.hasAttributes())
		{
			Attribute algoAttr = curElement.getAttribute(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
			if(null == algoAttr)
			{
				// no reference to algorithm -> nothing to check
			}
			else
			{
				// load Algorithm
				Algorithm algo = new Algorithm(report);
				algo.setConfiguration(cfg);
				if(false == algo.getFromFile(curElement, e))
				{					
					return false;
				}
				else
				{
					referencedAlgorithms.put(algoAttr.getValue(), algo);
				}
			}
			
		}
		else
		{
			// this needs to be supplied by the environment
			if(false == e.provides(curElement.getName()))
			{
				report.addError(this, "The environment does not have the device " + curElement.getName());
				return false;
			}
			// else OK
		}
		List<Element> children = curElement.getChildren();
		if((null == children) || (true == children.isEmpty()))
		{
			// nothing to check
		}
		else
		{
			// check children
			for(int i = 0; i < children.size(); i++)
			{
				Element curChildElement = children.get(i);
				if(false == checkAndTestElement(curChildElement, e))
				{
					return false;
				}
			}
		}
		return true;
	}

	public boolean checkAndTestAgainst(Environment e) 
	{
		if(null == e)
		{
			report.addError(this, "No Environment provided !");
			return false;
		}
		if(null == SolutionRoot)
		{		
			report.addError(this, "No Solution Tag in Project !");
			return false;
		}
		
		List<Element> children = SolutionRoot.getChildren();
		if((null == children) || (true == children.isEmpty()))
		{
			log.trace("No child tags in Solution Element -> external reference");
			if(null == extRefEleemnt)
			{
				report.addError(this, "No external Reference in empty solution tag");
				return false;
			}
			children = extRefEleemnt.getChildren();
			if((null == children) || (true == children.isEmpty()))
			{
				report.addError(this, "empty solution tag in external refernece");
				return false;
			}
		}
		
		for(int i = 0; i < children.size(); i++)
		{
			Element curElement = children.get(i);
			if(false == checkAndTestElement(curElement, e))
			{
				return false;
			}
		}
		
		return true;
	}
}
