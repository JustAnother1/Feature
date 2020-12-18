
package de.nomagic.puzzler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.configuration.Configuration;

public final class FileGetter
{
    public static final String ALGORITHM_ROOT_ELEMENT_NAME = "algorithm";
    public static final String API_ROOT_ELEMENT_NAME = "api";
    private static final String CLASS_NAME = "FileGetter";

    private static final Logger LOG = LoggerFactory.getLogger(CLASS_NAME);

    private FileGetter()
    {
    }

    public static Document getXmlFromStream(InputStream in, Context ctx)
    {
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document jdomDocument = null;
        try
        {
            jdomDocument = jdomBuilder.build(in);
        }
        catch(JDOMException e)
        {
            ctx.addError(CLASS_NAME, "JDOM Exception");
            LOG.trace(Tool.fromExceptionToString(e));
            jdomDocument = null;
        }
        catch (IOException e)
        {
            ctx.addError(CLASS_NAME, "IOException from stream");
            ctx.addError(CLASS_NAME, e.getMessage());
            LOG.trace(Tool.fromExceptionToString(e));
            jdomDocument = null;
        }

        return jdomDocument;
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
        // LOG.trace("trying to open {}.", xmlSource);
        try
        {
            jdomDocument = jdomBuilder.build(xmlSource);
        }
        catch(FileNotFoundException e)
        {
            if(true == failureIsError)
            {
                LOG.trace("path = {}", path);
                LOG.trace("name = {}", name);
                ctx.addError(CLASS_NAME, "File not found: " + xmlSource);
            }
            jdomDocument = null;
        }
        catch(JDOMException e)
        {
            if(true == failureIsError)
            {
                ctx.addError(CLASS_NAME, "JDOM Exception");
            }
            LOG.trace(Tool.fromExceptionToString(e));
            jdomDocument = null;
        }
        catch (IOException e)
        {
            if(true == failureIsError)
            {
                ctx.addError(CLASS_NAME, "IOException for file " + xmlSource);
                ctx.addError(CLASS_NAME, e.getMessage());
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
                    ctx.addError(CLASS_NAME, "no paths supplied");
                }
                else
                {
                    System.out.println("ERROR: " + CLASS_NAME + " : no paths supplied");
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
            LOG.trace("trying to open {}.", xmlSource);
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

    public static Element getFromFile(String[] paths, String[] subpaths, String fileName, Context ctx)
    {
        if((null == paths) ||(null == subpaths))
        {
            LOG.trace("no paths supplied");
            return null;
        }
        Element res = null;
        for (int i = 0; i < paths.length; i++) // check in all paths
        {
            for(int j = 0; j < subpaths.length; j++) // Check with all sub paths
            {
                StringBuilder sb = new StringBuilder();
                sb.append(paths[i]);
                for(int k = 0; k < (subpaths.length - j); k++) // start with the deepest path
                {
                    sb.append(subpaths[k]);
                    sb.append(File.separator);
                }
                res = getFromFile(sb.toString(), fileName, ctx);
                if(null != res)
                {
                    return res;
                }
            }
        }
        return res;
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
            // LOG.trace("Could not load the Element from the file {}",
            //         path + fileName);
            return null;
        }

        Element root = algo.getRootElement();
        if(null == root)
        {
            LOG.trace("No root tag in the File {} ", fileName);
            return null;
        }
        LOG.trace("Loaded the Element({}) from the file {}",
                root.getName(), path + fileName);
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
            LOG.warn("Name: {}", Name);
            LOG.warn("type: {}", type);
            LOG.warn("root name: {}", rootElementName);
            LOG.warn("context: {}", ctx);
            return null;
        }

        // Get project specific
        String fileName = type + File.separator + Name + "." + type + ".xml";
        Element root = getFromFile(ctx.cfg().getStringsOf(Configuration.PROJECT_PATH_CFG), fileName, ctx);
        if(null == root)
        {
            // if that failed then the architecture specific
            String[] paths = ctx.cfg().getStringsOf(Configuration.ENVIRONMENT_PATH_CFG);
            String[] subPaths = ctx.getEnvironment().getPlatformParts();

            root = getFromFile(paths, subPaths, fileName, ctx);

            if(null == root)
            {
                // if that also failed then the common one from the library
                root = getFromFile(ctx.cfg().getStringsOf(Configuration.LIB_PATH_CFG), fileName, ctx);
                if(null == root)
                {
                    ctx.addError(CLASS_NAME, "Could not find the " + type + " " + Name);
                    LOG.info("Searched for a file named  {}", fileName);
                    LOG.info("in the folders:");

                    String[] searchedPaths = ctx.cfg().getStringsOf(Configuration.PROJECT_PATH_CFG);
                    for(int i = 0; i < searchedPaths.length; i++)
                    {
                        LOG.info(searchedPaths[i]);
                    }
                    for(int i = 0; i < paths.length; i++)
                    {
                        LOG.info(paths[i]);
                    }
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
