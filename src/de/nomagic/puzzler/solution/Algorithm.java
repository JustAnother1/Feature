
package de.nomagic.puzzler.solution;

import java.io.File;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.FileGetter;
import de.nomagic.puzzler.PuzzlerMain;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.progress.ProgressReport;

public class Algorithm extends Base
{
	public final static String ALGORITHM_REFFERENCE_ATTRIBUTE_NAME = "algorithm";
	public final static String ALGORITHM_ROOT_ELEMENT_NAME = "algorithm";

	private Element root = null;
	
	public Algorithm(ProgressReport report) 
	{
		super(report);
	}

	public boolean getFromFile(Element curElement, Environment e) 
	{
		Attribute algoAttr = curElement.getAttribute(ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
		Document algo = FileGetter.getXmlFile(cfg.getString(PuzzlerMain.LIB_PATH_CFG), 
				"algorithm" + File.separator + algoAttr.getValue() + ".algorithm.xml", 
				report);
		if(null == algo)
		{
			report.addError(this, "Could not load the algorithm " + algoAttr.getValue() + " referenced in Element " + curElement.getName());
			return false;
		}
		
		root = algo.getRootElement();
		if(null == root)
		{
			report.addError(this, "No root tag in the algorithm " + algoAttr.getValue() + " referenced in Element " + curElement.getName());
			return false;
		}
		
		if(false == ALGORITHM_ROOT_ELEMENT_NAME.equals(root.getName()))
		{
			report.addError(this, "Invalid root tag(" + root.getName() + ") in the algorithm " 
		                    + algoAttr.getValue() + " referenced in Element " + curElement.getName());
			return false;
		}

		// TODO check required configuration
		// TOD check and load the referenced API
		
		return true;
	}

}
