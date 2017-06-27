
package de.nomagic.puzzler.solution;

import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.FileGetter;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.C_File;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.progress.ProgressReport;

public class Algorithm extends Base
{
    public final static String ALGORITHM_REFFERENCE_ATTRIBUTE_NAME = "algorithm";
    public final static String ALGORITHM_ROOT_ELEMENT_NAME = "algorithm";
    public final static String ALGORITHM_NAME_ATTRIBUTE_NAME = "name";
    public final static String ALGORITHM_API_ATTRIBUTE_NAME = "api";
    public final static String ALGORITHM_REQUIREMENTS_CHILD_NAME = "required";
    public final static String ALGORITHM_C_CODE_CHILD_NAME = "c_code";
    public final static String ALGORITHM_IF_CHILD_NAME = "if";
    public final static String ALGORITHM_FUNCTION_CHILD_NAME = "function";
    public final static String ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME = "name";
    public final static String ALGORITHM_ADDITIONAL_C_CODE_CHILD_NAME = "additional";
    public final static String ALGORITHM_ADDITIONAL_INCLUDE_CHILD_NAME = "include";
    public final static String ALGORITHM_ADDITIONAL_FUNCTION_CHILD_NAME = "function";

    private final Logger log = LoggerFactory.getLogger("Algorithm");

    private Element root = null;

    public Algorithm(Element root, ProgressReport report)
    {
        super(report);
        this.root = root;
    }

    @Override
    public String toString()
    {
        return "Algorithm " + root.getAttributeValue(ALGORITHM_NAME_ATTRIBUTE_NAME) + ")";
    }

    public static Algorithm getFromFile(Element curElement,
                                        Environment e,
                                        Configuration cfg,
                                        ProgressReport report)
    {
        Attribute algoAttr = curElement.getAttribute(ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
        Element root = FileGetter.getFromFile(algoAttr.getValue(),
                                              "algorithm",
                                              ALGORITHM_ROOT_ELEMENT_NAME,
                                              e,
                                              cfg,
                                              report);
        Algorithm res = new Algorithm(root, report);
        res.setConfiguration(cfg);

        // TODO check required configuration
        // TOD check and load the referenced API

        return res;
    }

    public Element getRequirements()
    {
        return root.getChild(ALGORITHM_REQUIREMENTS_CHILD_NAME);
    }

    public boolean hasApi(String api)
    {
        String apis = root.getAttributeValue(ALGORITHM_API_ATTRIBUTE_NAME);
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

    public String getFunctionCcode(String functionName)
    {
        Element cCode = root.getChild(ALGORITHM_C_CODE_CHILD_NAME);
        if(null == cCode)
        {
            // the algorithm might have the functions wrapped into if condition tags.
            Element ifCond = root.getChild(ALGORITHM_IF_CHILD_NAME); // TODO check condition
            if(null == ifCond)
            {
                report.addError("Algorithm.getFunctionCcode",
                        "Function call to missing code! (" + this.toString()
                        + ", function name : " + functionName + " )");
                return null;
            }
            else
            {
                cCode = ifCond.getChild(ALGORITHM_C_CODE_CHILD_NAME);
                if(null == cCode)
                {
                    report.addError("Algorithm.getFunctionCcode",
                            "Function call to missing code in conditional ! (" + this.toString()
                            + " , function name : " + functionName
                            + " , condition: " + ifCond.toString() + " )");
                    return null;
                }
            }
        }
        List<Element> funcs = cCode.getChildren(ALGORITHM_FUNCTION_CHILD_NAME);
        String searchedFunctionName = null;
        boolean hasParameter;
        if(true == functionName.contains("("))
        {
            searchedFunctionName = functionName.substring(0, functionName.indexOf('('));
            hasParameter = true;
        }
        else
        {
            searchedFunctionName = functionName;
            hasParameter = false;
        }
        if(null == searchedFunctionName)
        {
            report.addError("Algorithm.getFunctionCcode",
                    "Function call to unknown function!");
            return null;
        }
        if(1 > searchedFunctionName.length())
        {
            report.addError("Algorithm.getFunctionCcode",
                    "Function call to unnamed function!");
            return null;
        }
        for(int i = 0; i < funcs.size(); i++)
        {
            Element curElement = funcs.get(i);
            String name = curElement.getAttributeValue(ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME);
            if(true == searchedFunctionName.equals(name))
            {
                if(false == hasParameter)
                {
                    return curElement.getText();
                }
                else
                {
                    Element cond = curElement.getChild("if"); // TODO evaluate condition
                    if(null == cond)
                    {
                        // TODO handle parameter in function
                        return curElement.getText();
                    }
                    else
                    {
                        return cond.getText();
                    }
                }
            }
        }
        // function not found
        report.addError("Algorithm.getFunctionCcode",
                "Function call to missing function! (" + this.toString()
                        + ", function name : " + functionName + " )");
        return null;
    }

    public void addAdditionalsTo(C_File codeFile)
    {
        Element cCode = root.getChild(ALGORITHM_C_CODE_CHILD_NAME);
        if(null == cCode)
        {
            return;
        }
        Element additional = cCode.getChild(ALGORITHM_ADDITIONAL_C_CODE_CHILD_NAME);
        if(null == additional)
        {
            return;
        }
        List<Element> addlist = additional.getChildren();
        if(null == addlist)
        {
            return;
        }
        for(int i = 0; i < addlist.size(); i++)
        {
            Element curElement = addlist.get(i);
            String type = curElement.getName();
            switch(type)
            {
            case ALGORITHM_ADDITIONAL_INCLUDE_CHILD_NAME:
                codeFile.addLine(C_File.C_FILE_INCLUDE_SECTION_NAME, curElement.getText());
                break;

            case ALGORITHM_ADDITIONAL_FUNCTION_CHILD_NAME:
                Function func = new Function(curElement);
                String declaration = func.getDeclaration();
                codeFile.addLine(C_File.C_FILE_LOCAL_FUNCTION_DEFINITION_SECTION_NAME,
                                 declaration + ";");
                codeFile.addLine(C_File.C_FILE_FUNCTIONS_SECTION_NAME,
                                 declaration + "\n" + curElement.getText());
                break;

            default: // ignore
                break;
            }
        }
    }

}
