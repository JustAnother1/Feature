
package de.nomagic.puzzler.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGetter;
import de.nomagic.puzzler.Project;
import de.nomagic.puzzler.BuildSystem.BuildSystemAddApi;
import de.nomagic.puzzler.BuildSystem.Target;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.FileFactory;
import de.nomagic.puzzler.configuration.Configuration;

public class Environment extends Base
{
    public final static String EXTERNAL_REFFERENCE_ATTRIBUTE_NAME = "ref";
    public final static String ROOT_ELEMENT_NAME = "environment";
    public final static String PIN_MAPPING_ELEMENT_NAME = "pinMapping";
    public final static String CPU_ELEMENT_NAME = "cpu";
    public final static String ARCHITECTURE_ELEMENT_NAME = "architecture";
    public final static String ARCHITECTURE_TYPE_ATTRIBUTE_NAME = "name";
    public final static String ARCHITECTURE_FAMILY_ATTRIBUTE_NAME = "family";
    public final static String ARCHITECTURE_DEVICE_ATTRIBUTE_NAME = "device";
    public final static String BUILD_CFG_ROOT_ELEMENT_NAME = "build_cfg";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private Element EnvironmentRoot = null;
    private Document externalReferenceDocument = null;
    private Element XmlTreeRoot = null;
    private String ArchitectureName = "";
    private String FamilyName = "";
    private String DeviceName = "";

    public Environment(Context ctx)
    {
        super(ctx);
    }

    public String getArchitectureName()
    {
        return ArchitectureName;
    }

    public String getFamilyName()
    {
        return FamilyName;
    }

    public boolean getFromProject(Project pro)
    {
        if(null == pro)
        {
            ctx.addError(this, "No Project provided !");
            return false;
        }

        EnvironmentRoot = pro.getEnvironmentElement();
        if(null == EnvironmentRoot)
        {
            ctx.addError(this, "No Environment Tag in Project !");
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
                    ctx.addError(this, "Invalid external reference !");
                    return false;
                }
                // read external Reference
                externalReferenceDocument = FileGetter.getXmlFile(ctx.cfg().getString(Configuration.ROOT_PATH_CFG),
                                                                  externalReferenceFileName,
                                                                  ctx);
                if(null == externalReferenceDocument)
                {
                    ctx.addError(this, "Could not read referenced File " + externalReferenceFileName);
                    return false;
                }

                Element extRefEleemnt  = externalReferenceDocument.getRootElement();
                if(null == extRefEleemnt)
                {
                    ctx.addError(this, "Could not read Root Element from " + externalReferenceFileName);
                    return false;
                }

                if(false == ROOT_ELEMENT_NAME.equals(extRefEleemnt.getName()))
                {
                    ctx.addError(this, "Environment File " + externalReferenceFileName
                            + " has an invalid root tag (" + extRefEleemnt.getName() + ") !");
                    return false;
                }
                XmlTreeRoot = extRefEleemnt;
            }
            else
            {
                // no external Reference - all data in this node
                XmlTreeRoot = EnvironmentRoot;
            }
        }
        else
        {
            XmlTreeRoot = EnvironmentRoot;
        }

        return parseEnvironmentXmlTree();
    }

    private boolean parseEnvironmentXmlTree()
    {
        Element cpu = XmlTreeRoot.getChild(CPU_ELEMENT_NAME);
        if(null == cpu)
        {
            ctx.addError(this, "Environment did not specify the cpu used.");
            return false;
        }
        Element architecture = cpu.getChild(ARCHITECTURE_ELEMENT_NAME);
        if(null == architecture)
        {
            ctx.addError(this, "Environment did not specify the cpu architecture used.");
            return false;
        }
        ArchitectureName = architecture.getAttributeValue(ARCHITECTURE_TYPE_ATTRIBUTE_NAME);
        log.trace("Architecture : {}", ArchitectureName);
        FamilyName = architecture.getAttributeValue(ARCHITECTURE_FAMILY_ATTRIBUTE_NAME);
        if(null == FamilyName)
        {
            FamilyName = "";
        }
        log.trace("Family name : {}", FamilyName);
        DeviceName = architecture.getAttributeValue(ARCHITECTURE_DEVICE_ATTRIBUTE_NAME);
        log.trace("Device name : {}", DeviceName);
        if((null == ArchitectureName) || (null == DeviceName))
        {
            ctx.addError(this, "Environment did not specify the cpu Device/architecture name.");
            return false;
        }
        return true;
    }


    public boolean provides(String name)
    {
        // the environment provides pins,...
        if(null != XmlTreeRoot)
        {
            Element cpu = XmlTreeRoot.getChild(CPU_ELEMENT_NAME);
            if(null != cpu)
            {
                Element pinMap = XmlTreeRoot.getChild(PIN_MAPPING_ELEMENT_NAME);
                if(null != pinMap)
                {
                    Element pin = pinMap.getChild(name);
                    if(null != pin)
                    {
                        return true;
                    }
                }
            }
        }

        // maybe more ???

        // the searched thing is not in the environment,..
        ctx.addError(this, "The device " + name + " could not be found in the environment !");
        return false;
    }

    public Element getAlgorithmCfg(String algoName)
    {
        // the environment provides pins,...
        if(null != XmlTreeRoot)
        {
            Element cpu = XmlTreeRoot.getChild(CPU_ELEMENT_NAME);
            if(null != cpu)
            {
                Element pinMap = XmlTreeRoot.getChild(PIN_MAPPING_ELEMENT_NAME);
                if(null != pinMap)
                {
                    Element pin = pinMap.getChild(algoName);
                    return pin;
                }
            }
        }
        log.trace("No pin with the name {} !", algoName);

        // maybe more ???
        // TODO Auto-generated method stub
        return null;
    }


    private Element getConfigurationElementFrom(String Path, String FileName)
    {
        Document comCfgDoc = FileGetter.getXmlFile(Path, FileName, ctx);
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
            List<Element> TargetList = reqTargets.getChildren();
            if(null != TargetList)
            {
                Iterator<Element> it = TargetList.iterator();
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
            List<Element> FileList = reqFiles.getChildren();
            if(null != FileList)
            {
                Iterator<Element> it = FileList.iterator();
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

    public boolean configureBuild(BuildSystemAddApi BuildSystem, HashMap<String, String> requiredEnvironmentVariables)
    {
        // Find configuration file for architecture
        String commonCfgFolder;
        String envPath = ctx.cfg().getString(Configuration.ENVIRONMENT_PATH_CFG);
        if(1 > envPath.length())
        {
            ctx.addError(this, "Environment path not configured !");
            return false;
        }
        if(0 < FamilyName.length())
        {
            // family provided -> common configuration is in family folder
            commonCfgFolder = ctx.cfg().getString(Configuration.ENVIRONMENT_PATH_CFG) // Path is guaranteed to end with File.separator !
                    + ArchitectureName + File.separator
                    + FamilyName;
        }
        else
        {
            // no family provided -> common configuration is in architecture folder
            commonCfgFolder = ctx.cfg().getString(Configuration.ENVIRONMENT_PATH_CFG) // Path is guaranteed to end with File.separator !
                    + ArchitectureName;
        }

        Element commonElement = getConfigurationElementFrom(commonCfgFolder, "common_" + "cfg_build.xml");
        Element deviceElement = getConfigurationElementFrom(commonCfgFolder, DeviceName + "_cfg_build.xml");

        if((null == commonElement) && (null == deviceElement))
        {
            ctx.addError(this, "No Build system configuration file found!");
            return false;
        }

        if(null != commonElement)
        {
            addConfigurationFromTo(commonElement, BuildSystem, requiredEnvironmentVariables);
        }
        if(null != deviceElement)
        {
            addConfigurationFromTo(deviceElement, BuildSystem, requiredEnvironmentVariables);
        }
        return true;
    }

}
