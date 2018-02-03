
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

import de.nomagic.puzzler.configuration.Configuration;

public final class FileGetter
{
    public final static String ALGORITHM_ROOT_ELEMENT_NAME = "algorithm";
    public final static String API_ROOT_ELEMENT_NAME = "api";

    private static final Logger LOG = LoggerFactory.getLogger("FileGetter");

    private FileGetter()
    {
    }

    public static Document getXmlFile(String path, String name, Context ctx)
    {
        return tryToGetXmlFile(path, name, true, ctx);
    }

    public static Document getXmlFile(String[] paths, String name, Context ctx)
    {
        return tryToGetXmlFile(paths, name, true, ctx);
    }

    public static Document tryToGetXmlFile(String path,
                                           String name,
                                           boolean failureIsError,
                                           Context ctx)
    {
        if(null == name)
        {
            return null;
        }
        String xmlSource;
        if(null == path)
        {
            xmlSource = name;
        }
        else if(1 > path.length())
        {
            xmlSource = name;
        }
        else
        {
            if(false == path.endsWith(File.separator))
            {
                xmlSource = path + File.separator + name;
            }
            else
            {
                xmlSource = path + name;
            }
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
                LOG.trace("path = " + path);
                LOG.trace("name = " + name);
                ctx.addError("FileGetter", "File not found: " + xmlSource);
            }
            jdomDocument = null;
        }
        catch(JDOMException e)
        {
            if(true == failureIsError)
            {
                ctx.addError("FileGetter", "JDOM Exception");
            }
            LOG.trace(Tool.fromExceptionToString(e));
            jdomDocument = null;
        }
        catch (IOException e)
        {
            if(true == failureIsError)
            {
                ctx.addError("FileGetter", "IOException for file " + xmlSource);
                ctx.addError("FileGetter", e.getMessage());
            }
            LOG.trace(Tool.fromExceptionToString(e));
            jdomDocument = null;
        }

        return jdomDocument;
    }

    public static Document tryToGetXmlFile(String[] paths,
                                      String name,
                                      boolean failureIsError,
                                      Context ctx)
    {
        if(null == paths)
        {
            if(true == failureIsError)
            {
                if(null != ctx)
                {
                    ctx.addError("FileGetter", "no paths supplied");
                }
                else
                {
                    System.out.println("ERROR: FileGetter : no paths supplied");
                }
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
                Document res = getXmlFile(paths[i], name, ctx);
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

    public static Element getFromFile(String[] paths, String fileName, Context ctx)
    {
        if(null == paths)
        {
            LOG.trace("no paths supplied");
            return null;
        }
        Element res = null;
        for (int i = 0; i < paths.length; i++)
        {
            res = getFromFile(paths[i], fileName, ctx);
            if(null != res)
            {
                return res;
            }
        }
        return res;
    }

    public static Element getFromFile(String path, String fileName, Context ctx)
    {
        Document algo = FileGetter.tryToGetXmlFile(path,
                fileName,
                false,
                ctx);
        if(null == algo)
        {
            LOG.trace("Could not load the Element from the file {}",
                    path + fileName);
            return null;
        }

        Element root = algo.getRootElement();
        if(null == root)
        {
            LOG.trace("No root tag in the File {} ", fileName);
            return null;
        }

        return root;
    }

    public static Element getApiElement(String apiName, Context ctx)
    {
        return getFromFile(apiName, "api", API_ROOT_ELEMENT_NAME, ctx);
    }

    public static Element getAlgorithmElement(String algorithmName, Context ctx)
    {
        if((null == algorithmName))
        {
            LOG.warn("AlgorithmName null !");
            return null;
        }
        return getFromFile(algorithmName,
                           "algorithm",
                           ALGORITHM_ROOT_ELEMENT_NAME,
                           ctx);
    }

    private static Element getFromFile(String Name,
            String type,
            String rootElementName,
            Context ctx)
    {
        if(   (null == Name)
           || (null == type)
           || (null == rootElementName)
           || (null == ctx) )
        {
            LOG.warn("Invalid parameters!");
            LOG.warn("Name: " + Name);
            LOG.warn("type: " + type);
            LOG.warn("root name: " + rootElementName);
            LOG.warn("context: " + ctx);
            return null;
        }

        // Get project specific
        String fileName = type + File.separator + Name + "." + type + ".xml";
        Element root = getFromFile(ctx.cfg().getStringsOf(Configuration.PROJECT_PATH_CFG), fileName, ctx);
        if(null == root)
        {
            // if that failed then the architecture specific
            String[] paths = ctx.cfg().getStringsOf(Configuration.ENVIRONMENT_PATH_CFG);
            String architecture = ctx.getEnvironment().getArchitectureName();
            for(int i = 0; i < paths.length; i++)
            {
                paths[i] = paths[i] + architecture + File.separator;
            }
            String familyName = ctx.getEnvironment().getFamilyName();
            if(0 < familyName.length())
            {
                // if a family is given then the family has additional folders that are preferred to the general directories.
                Vector<String> famPaths = new Vector<String>();

                for(int i = 0; i < paths.length; i++)
                {
                    famPaths.add(paths[i] + familyName + File.separator);
                }
                for(int i = 0; i < paths.length; i++)
                {
                    famPaths.add(paths[i]);
                }
                paths = (String[]) famPaths.toArray();
            }
            root = getFromFile(paths, fileName, ctx);
            if(null == root)
            {
                // if that also failed then the common one from the library
                root = getFromFile(ctx.cfg().getStringsOf(Configuration.LIB_PATH_CFG), fileName, ctx);
                if(null == root)
                {
                    ctx.addError("static:Algorithm", "Could not find the " + type + " " + Name);
                    return null;
                }
            }
        }

        if(false == rootElementName.equals(root.getName()))
        {
            LOG.trace("Invalid root tag({}) in the {}", root.getName(), Name);
            return null;
        }

        return root;
    }



}
