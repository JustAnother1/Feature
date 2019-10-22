
package de.nomagic.puzzler.solution;

import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGetter;

public class Algorithm extends Base
{
    public static final String ALGORITHM_REFFERENCE_ATTRIBUTE_NAME = "algorithm";
    public static final String ALGORITHM_NAME_ATTRIBUTE_NAME = "name";
    public static final String ALGORITHM_API_ATTRIBUTE_NAME = "api";

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
        Element root = FileGetter.getAlgorithmElement(Name, ctx);
        if(null == root)
        {
            return null;
        }
        Algorithm res = new Algorithm(root, ctx);

        // TODO check required configuration
        // TODO check and load the referenced API

        return res;
    }

    public boolean hasApi(String api)
    {
        if(null == api)
        {
            return true;
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
                if(true == curApi.alsoImplements(api))
                {
                    return true;
                }
                // else continue search
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
