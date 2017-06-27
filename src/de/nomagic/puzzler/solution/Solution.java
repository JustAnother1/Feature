
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
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.progress.ProgressReport;

public class Solution extends Base
{
    public final static String EXTERNAL_REFFERENCE_ATTRIBUTE_NAME = "ref";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private HashMap<String, Algorithm> referencedAlgorithms = new HashMap<String,Algorithm>();

    private Element SolutionRoot = null;

    public Solution(ProgressReport report)
    {
        super(report);
    }

    public Element getRootElement()
    {
        return SolutionRoot;
    }

    public Algorithm getAlgorithm(String Name)
    {
        return referencedAlgorithms.get(Name);
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
                Document externalReferenceDocument = FileGetter.getXmlFile(cfg.getString(Configuration.ROOT_PATH_CFG),
                                                                  externalReferenceFileName, report);
                if(null == externalReferenceDocument)
                {
                    report.addError(this, "Could not read referenced File " + externalReferenceFileName);
                    return false;
                }

                Element extRefEleemnt  = externalReferenceDocument.getRootElement();
                if(null == extRefEleemnt)
                {
                    report.addError(this, "Could not read Root Element from " + externalReferenceFileName);
                    return false;
                }

                if(false == Project.SOLUTION_ELEMENT_NAME.equals(extRefEleemnt.getName()))
                {
                    report.addError(this, "Solution File " + externalReferenceFileName
                            + " has an invalid root tag (" + extRefEleemnt.getName() + ") !");
                    return false;
                }
                SolutionRoot = extRefEleemnt;
                cfg.setString(Configuration.SOLUTION_FILE_CFG, externalReferenceFileName);
            }
            else
            {
                // no external Reference - all data in this node
                cfg.setString(Configuration.SOLUTION_FILE_CFG, cfg.getString(Configuration.PROJECT_FILE_CFG));
            }
        }
        return true;
    }

    private boolean getAlgorithmForElement(Element cfgElement, Environment e)
    {
        Attribute algoAttr = cfgElement.getAttribute(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
        if(null == algoAttr)
        {
            // no reference to algorithm -> Failure !
            log.trace("Element does not have an algorithm attribute!");
            return false;
        }
        else
        {
            // load Algorithm
            Algorithm algo = Algorithm.getFromFile(cfgElement, e, cfg, report);

            if(null == algo)
            {
                return false;
            }
            else
            {
                referencedAlgorithms.put(algoAttr.getValue(), algo);
            }
        }
        return true;
    }

    private boolean checkAndTestElement(Element curElement, Environment e)
    {
        if(true == curElement.hasAttributes())
        {
            if(false == getAlgorithmForElement(curElement, e))
            {
                return false;
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
            else
            {
                Element envCfg = e.getAlgorithmCfg(curElement.getName());
                if(false == getAlgorithmForElement(envCfg, e))
                {
                    return false;
                }
            }
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
            report.addError(this, "No external Reference in empty solution tag");
            return false;
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
