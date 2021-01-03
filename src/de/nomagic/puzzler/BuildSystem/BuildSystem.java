
package de.nomagic.puzzler.BuildSystem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.FileFactory;
import de.nomagic.puzzler.FileGroup.FileGroup;

public abstract class BuildSystem extends Base implements BuildSystemApi
{
    public static final String BUILD_CFG_ROOT_ELEMENT_NAME = "build_cfg";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    protected FileGroup buildFiles = new FileGroup();
    protected HashMap<String, String> requiredEnvironmentVariables = new HashMap<String, String>();

    public BuildSystem(Context ctx)
    {
        super(ctx);
    }

    @Override
    public void addFile(AbstractFile newFile)
    {
        log.trace("adding the file {}", newFile.getFileName());
        buildFiles.add(newFile);
    }

    private void addVariables(Element reqVariables)
    {
        if(null != reqVariables)
        {
            List<Element> variList = reqVariables.getChildren();
            if(null != variList)
            {
                Iterator<Element> it = variList.iterator();
                while(it.hasNext())
                {
                    Element curVar = it.next();
                    extendListVariable(curVar.getName(),curVar.getText());
                }
            }
            // else no required Variables
        }
        // else no required Variables
    }

    private void addTargets(Element reqTargets)
    {
        if(null != reqTargets)
        {
            List<Element> targetList = reqTargets.getChildren();
            if(null != targetList)
            {
                Iterator<Element> it = targetList.iterator();
                while(it.hasNext())
                {
                    Element curTarget = it.next();
                    Target t = new Target(curTarget);
                    addTarget(t);
                }
            }
            // else no required Targets
        }
        // else no required Targets
    }

    private void addFiles(Element reqFiles)
    {
        if(null != reqFiles)
        {
            List<Element> fileList = reqFiles.getChildren();
            if(null != fileList)
            {
                Iterator<Element> it = fileList.iterator();
                while(it.hasNext())
                {
                    Element curFile = it.next();
                    AbstractFile aFile = FileFactory.getFileFromXml(curFile);
                    addFile(aFile);
                }
            }
            // else no required Targets
        }
        // else no required Targets
    }


    private boolean addConfigurationFromTo(
            Element cfgElement,
            Map<String, String> requiredEnvironmentVariables)
    {
        if(null == cfgElement)
        {
            return false;
        }
        // extract all "needed" stuff
        Element required = cfgElement.getChild("required");
        if(null != required)
        {
            addVariables(required.getChild("variables"));
            addTargets(required.getChild("targets"));
            addFiles(required.getChild("files"));
        }

        // then extract everything mentioned in the hashmap.
        Element variables = cfgElement.getChild("variables");
        if(null != variables)
        {
            Iterator<String> it = requiredEnvironmentVariables.keySet().iterator();
            while(it.hasNext())
            {
                String variName = it.next();
                String variValue =  variables.getChildText(variName);
                if(null != variValue)
                {
                    extendListVariable(variName, variValue);
                }
            }
        }
        return true;
    }

    public boolean configureBuild(Environment e,
            Map<String, String> requiredEnvironmentVariables)
    {
        Element[] cfgFiles = e.getConfigFile("_cfg_build.xml", BUILD_CFG_ROOT_ELEMENT_NAME);

        if(null == cfgFiles)
        {
            ctx.addError(this, "No Build system configuration file found!");
            return false;
        }

        for(int i = 0; i < cfgFiles.length; i++)
        {
            if( false == addConfigurationFromTo(cfgFiles[i], requiredEnvironmentVariables))
            {
                return false;
            }
        }
        return true;
    }

}
