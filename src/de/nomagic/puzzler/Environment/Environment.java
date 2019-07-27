/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>
 *
 */
package de.nomagic.puzzler.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGetter;
import de.nomagic.puzzler.BuildSystem.BuildSystemAddApi;
import de.nomagic.puzzler.BuildSystem.Target;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.FileFactory;
import de.nomagic.puzzler.configuration.Configuration;

public class Environment extends Base
{
    public static final String ROOT_ELEMENT_NAME = "environment";
    public static final String TOOL_ELEMENT_NAME = "tool";
    public static final String TOOL_NAME_ATTRIBUTE_NAME = "name";
    public static final String PIN_MAPPING_ELEMENT_NAME = "resources";
    public static final String LIBRARIES_ELEMENT_NAME = "lib";
    public static final String BUILD_CFG_ROOT_ELEMENT_NAME = "build_cfg";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    
    private Element xmlTreeRoot = null;
    private String platformName = "";
    private String[] platformParts = new String[0];
    
    public Environment(Context ctx)
    {
        super(ctx);
    }

    public boolean loadFromElement(Element environmentRoot)
    {    
        if(null == environmentRoot)
        {
            ctx.addError(this, "No Environment Tag in Project !");
            return false;
        }
        xmlTreeRoot = environmentRoot;
        return parseEnvironmentXmlTree();
    }

    private boolean parseEnvironmentXmlTree()
    {
        Element cpu = xmlTreeRoot.getChild(TOOL_ELEMENT_NAME);
        if(null == cpu)
        {
            ctx.addError(this, "Environment did not specify the platform / environment type / tool chain to use.");
            return false;
        }
        platformName = cpu.getAttributeValue(TOOL_NAME_ATTRIBUTE_NAME);
        log.trace("type : {}", platformName);
        if(null == platformName)
        {
            ctx.addError(this, "Environment did not specify the type.");
            platformName = "";
            platformParts = new String[0];
            return false;
        }
        platformParts = platformName.split("/");
        return true;
    }
    
    public String[] getPlatformParts()
    {
        return platformParts;
    }


    public boolean provides(String name)
    {
        // the environment provides resources,...
        if(null != xmlTreeRoot)
        {
            Element cpu = xmlTreeRoot.getChild(TOOL_ELEMENT_NAME);
            if(null != cpu)
            {
                Element resMap = xmlTreeRoot.getChild(PIN_MAPPING_ELEMENT_NAME);
                if(null != resMap)
                {
                    Element pin = resMap.getChild(name);
                    if(null != pin)
                    {
                        return true;
                    }
                }
            }
        }

        // maybe more ???

        // the searched thing is not in the environment,..
        ctx.addError(this, "The ressource " + name + " could not be found in the environment !");
        return false;
    }

    public Element getAlgorithmCfg(String algoName)
    {
        // the environment provides pins,...
        if(null != xmlTreeRoot)
        {
            Element resMap = xmlTreeRoot.getChild(PIN_MAPPING_ELEMENT_NAME);
            if(null != resMap)
            {
                Element res = resMap.getChild(algoName);
                if(null != res)
                {
                    return res;
                }
                // else continue search
            }
        }
        log.trace("No ressource with the name {} !", algoName);

        // ... and libraries,...
        if(null != xmlTreeRoot)
        {
            Element libraries = xmlTreeRoot.getChild(LIBRARIES_ELEMENT_NAME);
            if(null != libraries)
            {
                Element lib = libraries.getChild(algoName);
                if(null != lib)
                {
                    return lib;
                }
                // else continue search
            }
        }
        log.trace("No lib with the name {} !", algoName);

        // ...maybe more ???
        return null;
    }

    private Element getConfigurationElementFrom(String Path, String FileName)
    {
        Document comCfgDoc = FileGetter.tryToGetXmlFile(Path, FileName, false, ctx);
        if(null != comCfgDoc)
        {
            Element root = comCfgDoc.getRootElement();
            if(null == root)
            {
                ctx.addError(this, "No root tag "
                        + "in the configuration file " + Path + FileName);
                return null;
            }

            if(false == BUILD_CFG_ROOT_ELEMENT_NAME.equals(root.getName()))
            {
                ctx.addError(this, "Invalid root tag(" + root.getName() + ")  "
                        + "in the configuration file " + Path + FileName);
                return null;
            }

            return root;
        }
        else
        {
            // no configuration
            return null;
        }
    }

    private void addConfigurationFromTo(Element cfgElement, BuildSystemAddApi BuildSystem, HashMap<String, String> requiredEnvironmentVariables)
    {
        // extract all "needed" stuff
        Element required = cfgElement.getChild("required");
        Element reqVariables = required.getChild("variables");
        if(null != reqVariables)
        {
            List<Element> variList = reqVariables.getChildren();
            if(null != variList)
            {
                Iterator<Element> it = variList.iterator();
                while(it.hasNext())
                {
                    Element curVar = it.next();
                    BuildSystem.extendListVariable(curVar.getName(),curVar.getText());
                }
            }
            // else no required Variables
        }
        // else no required Variables

        // required Targets
        Element reqTargets = required.getChild("targets");
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
                    BuildSystem.addTarget(t);
                }
            }
            // else no required Targets
        }
        // else no required Targets

        // required Files
        Element reqFiles = required.getChild("files");
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
                    BuildSystem.addFile(aFile);
                }
            }
            // else no required Targets
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
                    BuildSystem.extendListVariable(variName, variValue);
                }
            }
        }
    }

    public boolean configureBuild(BuildSystemAddApi buildSystem, HashMap<String, String> requiredEnvironmentVariables)
    {
        // Find configuration file for architecture
        String commonCfgFolder;
        String envPath = ctx.cfg().getString(Configuration.ENVIRONMENT_PATH_CFG);
        if(1 > envPath.length())
        {
            ctx.addError(this, "Environment path not configured !");
            return false;
        }
        
        String deviceName = platformParts[platformParts.length -1]; // last element
        StringBuilder architectureName = new StringBuilder();
        
        for(int i = 0; i < platformParts.length -1; i++)
        {
            architectureName.append(platformParts[i]);
            architectureName.append(File.separator);
        }
        commonCfgFolder = ctx.cfg().getString(Configuration.ENVIRONMENT_PATH_CFG) // Path is guaranteed to end with File.separator !
                + architectureName.toString();

        // search in family folder
        Element commonElement = getConfigurationElementFrom(commonCfgFolder, "common_" + "cfg_build.xml");
        Element deviceElement = getConfigurationElementFrom(commonCfgFolder, deviceName + "_cfg_build.xml");

        // if not found then search in Architecture folder
        if(null == commonElement)
        {
            commonElement = getConfigurationElementFrom(ctx.cfg().getString(Configuration.ENVIRONMENT_PATH_CFG) + architectureName,
                    "common_" + "cfg_build.xml");
        }
        if(null == deviceElement)
        {
            deviceElement = getConfigurationElementFrom(ctx.cfg().getString(Configuration.ENVIRONMENT_PATH_CFG) + architectureName,
                    deviceName + "_cfg_build.xml");
        }

        if((null == commonElement) && (null == deviceElement))
        {
            ctx.addError(this, "No Build system configuration file found!");
            return false;
        }

        if(null != commonElement)
        {
            addConfigurationFromTo(commonElement, buildSystem, requiredEnvironmentVariables);
        }
        if(null != deviceElement)
        {
            addConfigurationFromTo(deviceElement, buildSystem, requiredEnvironmentVariables);
        }
        return true;
    }

}
