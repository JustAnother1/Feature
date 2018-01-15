
package de.nomagic.puzzler;

import org.jdom2.Document;
import org.jdom2.Element;

import de.nomagic.puzzler.configuration.Configuration;

public class Project extends Base
{
    public final static String PROJECT_ROOT_ELEMENT_NAME = "project";
    public final static String ENVIRONMENT_ELEMENT_NAME = "environment";
    public final static String SOLUTION_ELEMENT_NAME = "solution";

    private Document doc = null;
    private Element projectRoot = null;

    public Project(Context ctx)
    {
        super(ctx);
    }

    public boolean getFromFiles()
    {

        String fileName = ctx.cfg().getString(Configuration.PROJECT_FILE_CFG);
        if(null == fileName)
        {
            ctx.addError(this, "No Project File provided");
            return false;
        }

        fileName = fileName + ".xml";

        doc = FileGetter.getXmlFile(ctx.cfg().getString(Configuration.PROJECT_PATH_CFG), fileName, ctx);
        if(null == doc)
        {
            ctx.addError(this, "Could not read Project File " + fileName);
            return false;
        }

        projectRoot  = doc.getRootElement();
        if(null == projectRoot)
        {
            ctx.addError(this, "Could not read Root Element from " + fileName);
            return false;
        }

        if(false == PROJECT_ROOT_ELEMENT_NAME.equals(projectRoot.getName()))
        {
            ctx.addError(this, "Project File " + fileName + " has an invalid root tag (" + projectRoot.getName() + ") !");
            return false;
        }

        return true;
    }

    public Element getEnvironmentElement()
    {
        if(null == projectRoot)
        {
            return null;
        }
        return projectRoot.getChild(ENVIRONMENT_ELEMENT_NAME);
    }

    public Element getSolutionElement()
    {
        if(null == projectRoot)
        {
            return null;
        }
        return projectRoot.getChild(SOLUTION_ELEMENT_NAME);
    }

}
