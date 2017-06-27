
package de.nomagic.puzzler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.progress.ProgressReport;

public class FileGetter
{
    private static final Logger log = LoggerFactory.getLogger("FileGetter");

    public static Document getXmlFile(String path, String name, ProgressReport report)
    {
        return tryToGetXmlFile(path, name, true, report);
    }

    public static Document tryToGetXmlFile(String path, String name,  boolean failureIsError, ProgressReport report)
    {
        String xmlSource;
        if(null == path)
        {
            xmlSource = name;
        }
        else
        {
            xmlSource = path + File.separator + name;
        }
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document jdomDocument = null;
        try
        {
            jdomDocument = jdomBuilder.build(xmlSource);
        }
        catch(FileNotFoundException e)
        {
            if(true == failureIsError)
            {
                report.addError("FileGetter", "File not found: " + xmlSource);
            }
            jdomDocument = null;
        }
        catch(JDOMException e)
        {
            if(true == failureIsError)
            {
                report.addError("FileGetter", "JDOM Exception");
            }
            log.trace(Tool.fromExceptionToString(e));
        }
        catch (IOException e)
        {
            if(true == failureIsError)
            {
                report.addError("FileGetter", "IOException for file " + xmlSource);
                report.addError("FileGetter", e.getMessage());
            }
            log.trace(Tool.fromExceptionToString(e));
        }
        return jdomDocument;
    }

    public static Document getXmlFile(String[] paths, String name, ProgressReport report)
    {
        return tryToGetXmlFile(paths, name, true, report);
    }

    public static Document tryToGetXmlFile(String[] paths, String name, boolean failureIsError, ProgressReport report)
    {
        if(null == paths)
        {
            if(true == failureIsError)
            {
                report.addError("FileGetter", "no paths supplied");
            }
            return null;
        }
        for (int i = 0; i < paths.length; i++)
        {
            String xmlSource;
            if(null == paths[i])
            {
                xmlSource = name;
            }
            else
            {
                xmlSource = paths[i] + File.separator + name;
            }
            File f = new File(xmlSource);
            if(true == f.exists())
            {
                Document res = getXmlFile(paths[i], name, report);
                if(null != res)
                {
                    return res;
                }
                // else try next file
            }
            // else try next file
        }
        return null;
    }

    public static Element getFromFile(String[] paths, String fileName, ProgressReport report)
    {
        if(null == paths)
        {
            log.trace("no paths supplied");
            return null;
        }
        Element res = null;
        for (int i = 0; i < paths.length; i++)
        {
            res = getFromFile(paths[i], fileName, report);
            if(null != res)
            {
                return res;
            }
        }
        return res;
    }

    public static Element getFromFile(String path, String fileName, ProgressReport report)
    {
        Document algo = FileGetter.tryToGetXmlFile(path,
                fileName,
                false,
                report);
        if(null == algo)
        {
            log.trace("Could not load the Element from the file {}",
                    path + fileName);
            return null;
        }

        Element root = algo.getRootElement();
        if(null == root)
        {
            log.trace("No root tag in the File {} ", fileName);
            return null;
        }

        return root;
    }


    public static Element getFromFile(String Name,
                                        String type,
                                        String rootElementName,
                                        Environment e,
                                        Configuration cfg,
                                        ProgressReport report)
    {
        if((null == Name)|| (null == type) || (null == rootElementName))
        {
            log.warn("Invalid parameters!");
            log.warn("Name: " + Name);
            log.warn("type: " + type);
            log.warn("root name: " + rootElementName);
            return null;
        }

        // Get project specific
        String fileName = type + File.separator + Name + "." + type + ".xml";
        Element root = getFromFile(cfg.getStringsOf(Configuration.PROJECT_PATH_CFG), fileName, report);
        if(null == root)
        {
            // if that failed then the architecture specific
            String[] paths = cfg.getStringsOf(Configuration.ENVIRONMENT_PATH_CFG);
            String Architecture = e.getArchitectureName();
            for(int i = 0; i < paths.length; i++)
            {
                paths[i] = paths[i] + Architecture + File.separator;
            }
            String FamilyName = e.getFamilyName();
            if(0 < FamilyName.length())
            {
                // if a family is given then the family has additional folders that are preferred to the general directories.
                Vector<String> famPaths = new Vector<String>();

                for(int i = 0; i < paths.length; i++)
                {
                    famPaths.add(paths[i] + FamilyName + File.separator);
                }
                for(int i = 0; i < paths.length; i++)
                {
                    famPaths.add(paths[i]);
                }
                paths = (String[]) famPaths.toArray();
            }
            root = getFromFile(paths, fileName, report);
            if(null == root)
            {
                // if that also failed then the common one from the library
                root = getFromFile(cfg.getStringsOf(Configuration.LIB_PATH_CFG), fileName, report);
                if(null == root)
                {
                    report.addError("static:Algorithm", "Could not find the " + type + " " + Name);
                    return null;
                }
            }
        }

        if(false == rootElementName.equals(root.getName()))
        {
            log.trace("Invalid root tag({}) in the {}", root.getName(), Name);
            return null;
        }

        return root;
    }


}
