package de.nomagic.puzzler.solution;

import java.util.List;
import java.util.Vector;

import org.jdom2.Element;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.FileGetter;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.progress.ProgressReport;

public class Api extends Base
{
    public final static String API_ROOT_ELEMENT_NAME = "api";
    public final static String API_FUNCTION_ELEMENT_NAME = "function";

    private Element root = null;

    public Api(Element root, ProgressReport report)
    {
        super(report);
        this.root = root;
    }


    public static Api getFromFile(String name,
                                  Environment e,
                                  Configuration cfg,
                                  ProgressReport report)
    {
        Element root = FileGetter.getFromFile(name, "api", API_ROOT_ELEMENT_NAME, e, cfg, report);
        Api res = new Api(root, report);
        res.setConfiguration(cfg);
        return res;
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
