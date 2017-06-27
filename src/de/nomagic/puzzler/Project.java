
package de.nomagic.puzzler;

import org.jdom2.Document;
import org.jdom2.Element;

import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.progress.ProgressReport;

public class Project extends Base
{
    public final static String PROJECT_ROOT_ELEMENT_NAME = "project";
    public final static String ENVIRONMENT_ELEMENT_NAME = "environment";
    public final static String SOLUTION_ELEMENT_NAME = "solution";

    private Document doc = null;
    private Element ProjectRoot = null;

    public Project(ProgressReport report)
    {
        super(report);
    }

    public boolean getFromFiles()
    {
        if(null == cfg)
        {
            report.addError(this, "No Configuration provided");
            return false;
        }

        String fileName = cfg.getString(Configuration.PROJECT_FILE_CFG);
        if(null == fileName)
        {
            report.addError(this, "No Project File provided");
            return false;
        }

        fileName = fileName + ".xml";

        doc = FileGetter.getXmlFile(cfg.getString(Configuration.PROJECT_PATH_CFG), fileName, report);
        if(null == doc)
        {
            report.addError(this, "Could not read Project File " + fileName);
            return false;
        }

        ProjectRoot  = doc.getRootElement();
        if(null == ProjectRoot)
        {
            report.addError(this, "Could not read Root Element from " + fileName);
            return false;
        }

        if(false == PROJECT_ROOT_ELEMENT_NAME.equals(ProjectRoot.getName()))
        {
            report.addError(this, "Project File " + fileName + " has an invalid root tag (" + ProjectRoot.getName() + ") !");
            return false;
        }

        return true;
    }

    public Element getEnvironmentElement()
    {
        if(null == ProjectRoot)
        {
            return null;
        }
        return ProjectRoot.getChild(ENVIRONMENT_ELEMENT_NAME);
    }

    public Element getSolutionElement()
    {
        if(null == ProjectRoot)
        {
            return null;
        }
        return ProjectRoot.getChild(SOLUTION_ELEMENT_NAME);
    }

}
