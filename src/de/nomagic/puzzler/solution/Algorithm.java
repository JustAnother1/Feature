
package de.nomagic.puzzler.solution;

import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Library;

public class Algorithm extends Base
{
    public final static String ALGORITHM_REFFERENCE_ATTRIBUTE_NAME = "algorithm";
    public final static String ALGORITHM_NAME_ATTRIBUTE_NAME = "name";
    public final static String ALGORITHM_API_ATTRIBUTE_NAME = "api";

    private final Logger log = LoggerFactory.getLogger("Algorithm");

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
            return "Algorithm " + root.getAttributeValue(ALGORITHM_NAME_ATTRIBUTE_NAME)
                + " implementing " +  root.getAttributeValue(ALGORITHM_API_ATTRIBUTE_NAME);
        }
    }

    public static Algorithm getFromFile(Element curElement, Library lib, Context ctx)
    {
        if((null == curElement) || (null == lib))
        {
            return null;
        }
        Attribute algoAttr = curElement.getAttribute(ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
        if(null == algoAttr)
        {
            return null;
        }
        Element root = lib.getAlgorithmElement(algoAttr.getValue(), ctx);
        Algorithm res = new Algorithm(root, ctx);

        // TODO check required configuration
        // TODO check and load the referenced API

        return res;
    }

    public boolean hasApi(String api)
    {
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
            // TODO check if one of the apis implement the searched API
            log.warn("Recursive API search not implemented!");
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
