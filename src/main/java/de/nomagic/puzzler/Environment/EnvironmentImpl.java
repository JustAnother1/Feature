package de.nomagic.puzzler.Environment;

import java.io.File;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.configuration.Configuration;

public class EnvironmentImpl extends Base implements Environment
{

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private Element xmlTreeRoot = null;
    private String[] platformParts = new String[0];


    public EnvironmentImpl(Context ctx)
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
        String platformName = cpu.getAttributeValue(TOOL_NAME_ATTRIBUTE_NAME);
        log.trace("type : {}", platformName);
        if(null == platformName)
        {
            ctx.addError(this, "Environment did not specify the tool name.");
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

    public String getBuldSystemType()
    {
        if(null != xmlTreeRoot)
        {
            Element build = xmlTreeRoot.getChild(BUILD_SYSTEM_ELEMENT_NAME);
            if(null != build)
            {
                return build.getAttributeValue(BUILD_SYSTEM_TYPE_ATTRIBUTE_NAME);
            }
        }
        return null;
    }

    public String getRootApi()
    {
        if(null != xmlTreeRoot)
        {
            Element build = xmlTreeRoot.getChild(ROOT_API_ELEMENT_NAME);
            if(null != build)
            {
                return build.getAttributeValue(ROOT_API_NAME_ATTRIBUTE_NAME);
            }
        }
        return null;
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
        ctx.addError(this, "The ressource '" + name + "' could not be found in the environment !");
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
            log.trace("No ressource with the name '{}' !", algoName);
        }
        // ...maybe more ???
        log.trace("could find nothing with the name '{}' !", algoName);
        return null;
    }

    private Element getConfigurationElementFrom(String Path, String FileName, String RootElementName)
    {
        Document comCfgDoc = ctx.getFileGetter().tryToGetXmlFile(Path, FileName, false);
        if(null != comCfgDoc)
        {
            Element root = comCfgDoc.getRootElement();
            if(null == root)
            {
                ctx.addError(this, "No root tag "
                        + "in the configuration file " + Path + FileName);
                return null;
            }

            if(false == RootElementName.equals(root.getName()))
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

    public Element[] getConfigFile(String postfix, String RootElementName)
    {
        ArrayList<Element> res = new ArrayList<Element>();
        // Find configuration file for environment
        String commonCfgFolder;
        String envPath = ctx.cfg().getString(Configuration.ENVIRONMENT_PATH_CFG);
        if(1 > envPath.length())
        {
            ctx.addError(this, "Environment path not configured !");
            return null;
        }

        String deviceName = platformParts[platformParts.length -1]; // last element
        StringBuilder architectureName = new StringBuilder();

        for(int i = 0; i < platformParts.length -1; i++)
        {
            architectureName.append(platformParts[i]);
            architectureName.append(File.separator);
        }
        commonCfgFolder = ctx.cfg().getString(Configuration.PROJECT_PATH_CFG) // Path is guaranteed to end with File.separator !
                + architectureName.toString();

        // search in project folder
        Element commonElement = getConfigurationElementFrom(commonCfgFolder, "common" + postfix, RootElementName);
        Element deviceElement = getConfigurationElementFrom(commonCfgFolder, deviceName + postfix, RootElementName);

        // if not found then search in environment folder
        if(null == commonElement)
        {
            // search whole tree
            for(int i = 0; i < platformParts.length; i++)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(ctx.cfg().getString(Configuration.ENVIRONMENT_PATH_CFG));
                for(int k = 0; k < (platformParts.length - (i + 1)); k++) // start with the deepest path
                {
                    sb.append(platformParts[k]);
                    sb.append(File.separator);
                }
                commonElement = getConfigurationElementFrom(
                        sb.toString(),
                        "common" + postfix,
                        RootElementName);
                if(null != commonElement)
                {
                    break;
                }
            }

        }

        if(null == deviceElement)
        {
            // only in the device sub folder
            deviceElement = getConfigurationElementFrom(
                    ctx.cfg().getString(Configuration.ENVIRONMENT_PATH_CFG) + architectureName,
                    deviceName + postfix,
                    RootElementName);
        }
        // TODO ??? maybe have more than one common file?
        res.add(commonElement);
        res.add(deviceElement);
        return res.toArray(new Element[0]);
    }

}
