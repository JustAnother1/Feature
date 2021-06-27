
package de.nomagic.puzzler.solution;

import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGetterImpl;

public class Algorithm extends Base
{
    public static final String ALGORITHM_REFFERENCE_ATTRIBUTE_NAME = "algorithm";
    public static final String ALGORITHM_NAME_ATTRIBUTE_NAME = "name";
    public static final String ALGORITHM_API_ATTRIBUTE_NAME = "api";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private Element root = null;

    public Algorithm(Element root, Context ctx)
    {
        super(ctx);
        this.root = root;
    }

    @Override
    public String toString()
    {
        if(null == root)
        {
            return "ERROR: unconfigured Algorithm";
        }
        else
        {
            String apis = root.getAttributeValue(ALGORITHM_API_ATTRIBUTE_NAME);
            if(null != apis)
            {
                return "Algorithm " + root.getAttributeValue(ALGORITHM_NAME_ATTRIBUTE_NAME)
                    + " implementing " + apis;
            }
            else
            {
                return "Algorithm " + root.getAttributeValue(ALGORITHM_NAME_ATTRIBUTE_NAME);
            }
        }
    }



    public static Algorithm getFromFile(Element curElement, Context ctx)
    {
        if(null == curElement)
        {
            return null;
        }
        Attribute algoAttr = curElement.getAttribute(ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
        if(null == algoAttr)
        {
            return null;
        }
        return getFromFile(algoAttr.getValue(), ctx);
    }

    public static Algorithm getFromFile(String Name, Context ctx)
    {
        if(null == ctx)
        {
            return null;
        }
        Element root = ctx.getFileGetter().getFromFile(Name,
                "algorithm",
                FileGetterImpl.ALGORITHM_ROOT_ELEMENT_NAME);
        if(null == root)
        {
            return null;
        }
        Algorithm res = new Algorithm(root, ctx);

        // TODO check required configuration
        // TODO check and load the referenced API

        return res;
    }

    public String getApis()
    {
        if(null != root)
        {
            String apis = root.getAttributeValue(ALGORITHM_API_ATTRIBUTE_NAME);
            return apis;
        }
        else
        {
            log.trace("No API attribute.");
            return null;
        }
    }

    public boolean hasApi(String api)
    {
        if(null == api)
        {
            return false;
        }
        if(null != root)
        {
            String apis = root.getAttributeValue(ALGORITHM_API_ATTRIBUTE_NAME);
            if(null == apis)
            {
                return false;
            }
            String[] apiArr = apis.split(",");
            for(int i = 0; i < apiArr.length; i++)
            {
                apiArr[i] = apiArr[i].trim();
                if(apiArr[i].equals(api))
                {
                    return true;
                }
            }
            // check if one of the apis implement the searched API
            for(int i = 0; i < apiArr.length; i++)
            {
                Api curApi = Api.getFromFile(apiArr[i], ctx);
                if(null == curApi)
                {
                    log.error("Reference to the invalid API {} in {} !", apiArr[i], this.toString());
                }
                else
                {
                    if(true == curApi.alsoImplements(api))
                    {
                        return true;
                    }
                    // else continue search
                }
            }
            return false;
        }
        else
        {
            return false;
        }
    }

    public Element getChild(String theChildsName)
    {
        if(null != root)
        {
            return root.getChild(theChildsName);
        }
        else
        {
            return null;
        }
    }

    public List<Element> getChildren(String theChildrensNames)
    {
        if(null != root)
        {
            return root.getChildren(theChildrensNames);
        }
        else
        {
            return null;
        }
    }

}
