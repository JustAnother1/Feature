
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

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final Context ctx;
    private XmlRpcGetter xrg = null;

    public FileGetter(Context ctx)
    {
        this.ctx = ctx;
    }

    public Document getXmlFromStream(InputStream in)
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
            log.trace(Tool.fromExceptionToString(e));
            jdomDocument = null;
        }
        catch (IOException e)
        {
            ctx.addError(CLASS_NAME, "IOException from stream");
            ctx.addError(CLASS_NAME, e.getMessage());
            log.trace(Tool.fromExceptionToString(e));
            jdomDocument = null;
        }

        return jdomDocument;
    }

    public Document getXmlFile(String path, String name)
    {
        return tryToGetXmlFile(path, name, true);
    }

    public Document tryToGetXmlFile(String path,
                                    String name,
                                    boolean failureIsError)
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
        Document jdomDocument = null;
        if(null != xrg)
        {
            jdomDocument = xrg.getAsDocument(xmlSource);
            if(null != jdomDocument)
            {
                return jdomDocument;
            }
            // else not available this way so try the normal way
        }
        File xf = new File(xmlSource);
        if(false == xf.exists())
        {
            return null;
        }
        SAXBuilder jdomBuilder = new SAXBuilder();
        log.trace("trying to open {}.", xmlSource);
        try
        {
            jdomDocument = jdomBuilder.build(xmlSource);
        }
        catch(FileNotFoundException e)
        {
            if(true == failureIsError)
            {
                log.trace("path = {}", path);
                log.trace("name = {}", name);
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
            log.trace(Tool.fromExceptionToString(e));
            jdomDocument = null;
        }
        catch (IOException e)
        {
            if(true == failureIsError)
            {
                ctx.addError(CLASS_NAME, "IOException for file " + xmlSource);
                ctx.addError(CLASS_NAME, e.getMessage());
            }
            log.trace(Tool.fromExceptionToString(e));
            jdomDocument = null;
        }

        return jdomDocument;
    }

    private Element getFromFile(String[] paths, String[] subpaths, String fileName)
    {
        if((null == paths) ||(null == subpaths))
        {
            log.trace("no paths supplied");
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
                res = getFromFile(sb.toString(), fileName);
                if(null != res)
                {
                    return res;
                }
            }
        }
        return res;
    }

    private Element getFromFile(String[] paths, String fileName)
    {
        if(null == paths)
        {
            log.trace("no paths supplied");
            return null;
        }
        Element res = null;
        for (int i = 0; i < paths.length; i++)
        {
            res = getFromFile(paths[i], fileName);
            if(null != res)
            {
                return res;
            }
        }
        return res;
    }

    private Element getFromFile(String path, String fileName)
    {
        Document algo = tryToGetXmlFile(path,
                fileName,
                false);
        if(null == algo)
        {
            // LOG.trace("Could not load the Element from the file {}",
            //         path + fileName);
            return null;
        }

        Element root = algo.getRootElement();
        if(null == root)
        {
            log.trace("No root tag in the File {} ", fileName);
            return null;
        }
        log.trace("Loaded the Element({}) from the file {}",
                root.getName(), path + fileName);
        return root;
    }

    public Element getFromFile(String Name,
            String type,
            String rootElementName)
    {
        if(   (null == Name)
           || (null == type)
           || (null == rootElementName)
           || (null == ctx) )
        {
            log.warn("Invalid parameters!");
            log.warn("Name: {}", Name);
            log.warn("type: {}", type);
            log.warn("root name: {}", rootElementName);
            log.warn("context: {}", ctx);
            return null;
        }

        // Get project specific
        String fileName = type + File.separator + Name + "." + type + ".xml";
        Element root = getFromFile(ctx.cfg().getStringsOf(Configuration.PROJECT_PATH_CFG), fileName);
        if(null == root)
        {
            // if that failed then the architecture specific
            String[] paths = ctx.cfg().getStringsOf(Configuration.ENVIRONMENT_PATH_CFG);
            String[] subPaths = ctx.getEnvironment().getPlatformParts();

            root = getFromFile(paths, subPaths, fileName);

            if(null == root)
            {
                // if that also failed then the common one from the library
                root = getFromFile(ctx.cfg().getStringsOf(Configuration.LIB_PATH_CFG), fileName);
                if(null == root)
                {
                    ctx.addError(CLASS_NAME, "Could not find the " + type + " " + Name);
                    log.info("Searched for a file named  {}", fileName);
                    log.info("in the folders:");

                    String[] searchedPaths = ctx.cfg().getStringsOf(Configuration.PROJECT_PATH_CFG);
                    for(int i = 0; i < searchedPaths.length; i++)
                    {
                        log.info(searchedPaths[i]);
                    }
                    for(int i = 0; i < paths.length; i++)
                    {
                        log.info(paths[i]);
                    }
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

    public void addGetter(XmlRpcGetter xrg)
    {
        this.xrg = xrg;
    }

}
