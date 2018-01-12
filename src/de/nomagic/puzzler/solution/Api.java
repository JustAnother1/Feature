package de.nomagic.puzzler.solution;

import java.util.List;
import java.util.Vector;

import org.jdom2.Element;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGetter;

public class Api extends Base
{
    public final static String API_NAME_ATTRIBUTE_NAME = "name";
    public final static String API_FUNCTION_ELEMENT_NAME = "function";

    private Element root = null;

    public Api(Element root, Context ctx)
    {
        super(ctx);
        this.root = root;
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
            Api res = new Api(root, ctx);
            return res;
        }
        else
        {
            return null;
        }
    }

    public Function[] getRequiredFunctions()
    {
        List<Element> funcList = root.getChildren(API_FUNCTION_ELEMENT_NAME);
        Vector<Function> resVec = new Vector<Function>();
        for(int i = 0; i < funcList.size(); i++)
        {
            Element curE = funcList.get(i);
            Function curFunc = new Function(curE);
            if(true == curFunc.isRequired())
            {
                resVec.addElement(curFunc);
            }
            // else ignore optional functions
        }
        return resVec.toArray(new Function[0]);
    }

}
