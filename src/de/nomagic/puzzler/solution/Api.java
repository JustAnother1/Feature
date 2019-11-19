package de.nomagic.puzzler.solution;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGetter;

public class Api extends Base
{
    public static final String API_NAME_ATTRIBUTE_NAME = "name";
    public static final String API_ALSO_IMPLEMENT_ATTRIBUTE_NAME = "implements";
    public static final String API_FUNCTION_ELEMENT_NAME = "function";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private Element root = null;
    private List<Element> funcList = null;

    public Api(Element root, Context ctx)
    {
        super(ctx);
        this.root = root;
        if(null != root)
        {
            funcList = root.getChildren(API_FUNCTION_ELEMENT_NAME);
        }
    }

    @Override
    public String toString()
    {
        if(null != root)
        {
            return "Api " + root.getAttributeValue(API_NAME_ATTRIBUTE_NAME);
        }
        else
        {
            return "invalid Api";
        }
    }

    public static Api getFromFile(String name, Context ctx)
    {
        Element root = FileGetter.getApiElement(name, ctx);
        if(null != root)
        {
            return new Api(root, ctx);
        }
        else
        {
            return null;
        }
    }

    public Function[] getRequiredFunctions()
    {
        if(null == funcList)
        {
            return null;
        }
        else
        {
            ArrayList<Function> resVec = new ArrayList<Function>();
            for(int i = 0; i < funcList.size(); i++)
            {
                Element curE = funcList.get(i);
                Function curFunc = new Function(curE);
                if(true == curFunc.isRequired())
                {
                    resVec.add(curFunc);
                }
                // else ignore optional functions
            }
            return resVec.toArray(new Function[0]);
        }
    }

    public int getNumberOfFunctions()
    {
        if(null == funcList)
        {
            return 0;
        }
        else
        {
            return funcList.size();
        }
    }

    public Function getFunctionIndex(int idx)
    {
        if(null == funcList)
        {
            return null;
        }
        else
        {
            Element curE = funcList.get(idx);
            if(null == curE)
            {
                return null;
            }
            else
            {
                return new Function(curE);
            }
        }
    }

    public boolean containsFunction(String name)
    {
        if((null == funcList) || (null == name))
        {
            return false;
        }
        else
        {
            for(int i = 0; i < funcList.size(); i++)
            {
                Element curE = funcList.get(i);
                Function curFunc = new Function(curE);
                if(true == name.equals(curFunc.getName()))
                {
                    // found the function
                    return true;
                }
            }
            // searched everything -> nothing found
            return false;
        }
    }

    public boolean alsoImplements(String api)
    {
        String res = root.getAttributeValue(API_ALSO_IMPLEMENT_ATTRIBUTE_NAME);
        if(null == res)
        {
            return false;
        }
        if(1 > res.length())
        {
            return false;
        }
        // do we implement the API ?
        if(true == res.contains(api))
        {
            return true;
        }
        // check each implemented API if that API implements the searched for API.
        String[] apiArr = res.split(",");
        for(int i = 0; i < apiArr.length; i++)
        {
            apiArr[i] = apiArr[i].trim();
            Api curApi = Api.getFromFile(apiArr[i], ctx);
            if(null == curApi)
            {
                log.error("Reference to the invalid API {} in {} !", apiArr[i], this);
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

}
