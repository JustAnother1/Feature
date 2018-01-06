
package de.nomagic.puzzler.Generator;

import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGetter;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.FileGroup.C_File;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.FileGroup.TextFile;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.Api;
import de.nomagic.puzzler.solution.ConditionEvaluator;
import de.nomagic.puzzler.solution.ConfiguredAlgorithm;
import de.nomagic.puzzler.solution.Function;

public class C_CodeGenerator extends Generator
{
    public final static String REQUIRED_ROOT_API = "program_entry_point";
    public final static String IMPLEMENTATION_PLACEHOLDER_REGEX = "\\$\\$\\$";
    public final static String ALGORITHM_C_CODE_CHILD_NAME = "c_code";

    public final static String ALGORITHM_IF_CHILD_NAME = "if";
    public final static String ALGORITHM_FUNCTION_CHILD_NAME = "function";
    public final static String ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME = "name";
    public final static String ALGORITHM_ADDITIONAL_C_CODE_CHILD_NAME = "additional";
    public final static String ALGORITHM_ADDITIONAL_INCLUDE_CHILD_NAME = "include";
    public final static String ALGORITHM_ADDITIONAL_FUNCTION_CHILD_NAME = "function";
    public final static String ALGORITHM_ADDITIONAL_FILE_CHILD_NAME = "file";
    public final static String ALGORITHM_ADDITIONAL_FILE_NAME_ATTRIBUTE = "name";

    public final static String CFG_DOC_CODE_SRC = "document_code_source";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private ConditionEvaluator condiEval;
    private FileGroup codeGroup;
    private C_File sourceFile;

    // if this is true then all code sniplets will be wrapped into comment lines explaining where they came from.
    private boolean document_code_source = false;



    // TODO: right now all code is inlined! Add support for functions (called from different places and distribution of code into separate files.

    public C_CodeGenerator(Context ctx)
    {
        super(ctx);
        condiEval = new ConditionEvaluator(ctx);
    }

    @Override
    public void configure(Configuration cfg)
    {
        if(null == cfg)
        {
            return;
        }
        if("true".equals(cfg.getString(CFG_DOC_CODE_SRC)))
        {
            log.trace("Switching on documentation of code source");
            document_code_source = true;
        }
    }

    public FileGroup generateFor(ConfiguredAlgorithm logic)
    {
        codeGroup = new FileGroup();

        if(null == logic)
        {
            ctx.addError(this, "" + logic + " : Failed to build the algorithm tree !");
            return null;
        }

        if(false == logic.hasApi(REQUIRED_ROOT_API))
        {
            log.trace("root: {}", logic);
            ctx.addError(this, "" + logic + " : Root element of the solution is not an " + REQUIRED_ROOT_API + " !");
            return null;
        }

        log.trace("starting to generate the c implementation for {}", logic);
        // we will need at least one *.c file. So create that now.
        sourceFile = createFile("main.c");

        FileGetter fg = new FileGetter();
        Api api = Api.getFromFile(REQUIRED_ROOT_API, fg, ctx);

        C_File imp = getImplementationFor(api, logic);
        if(null == imp)
        {
            return null;
        }
        sourceFile.addContentsOf(imp);

        codeGroup.add(sourceFile);

        return codeGroup;
    }

    private C_File getImplementationFor(Api api, ConfiguredAlgorithm logic)
    {
        log.trace("getting implementation of the {} from {}", api, logic);
        C_File aFile = new C_File("noname.c");
        Function[] funcs = api.getRequiredFunctions();
        for(int i = 0; i < funcs.length; i++)
        {
            C_functionCall fc = new C_functionCall(funcs[i].getName());
            String implementation = getCImplementationOf(fc, logic);
            if(null == implementation)
            {
                return null;
            }
            funcs[i].setImplementation(implementation);
            if(true == document_code_source)
            {
                aFile.addFunction(funcs[i], logic);
            }
            else
            {
                aFile.addFunction(funcs[i]);
            }
        }
        return aFile;
    }

    private String getCImplementationOf(C_functionCall functionToCall, ConfiguredAlgorithm logic)
    {
        log.trace("getting the c implemention of the function {} from {}",
                functionToCall, logic);

        if( false == logic.hasApi(functionToCall.getApi()))
        {
            log.warn("{} : Function call to wrong API!(API: {})", logic, functionToCall.getApi());
            return null;
        }

        functionToCall.setFunctionArguments(
                condiEval.evaluateConditionParenthesis(functionToCall.getArguments(), logic, null, null));

        String searchedFunctionName = functionToCall.getName();
        if(null == searchedFunctionName)
        {
            ctx.addError(this, "" + logic + " : Function call to unknown function!");
            return null;
        }
        if(1 > searchedFunctionName.length())
        {
            ctx.addError(this, "" + logic + " : Function call to unnamed function!");
            return null;
        }
        Element functionElement = getFunctionElement(searchedFunctionName, functionToCall.getArguments(), logic);
        if(null == functionElement)
        {
            return null;
        }
        String implementation = null;

        if(true == document_code_source)
        {
            implementation =  "// from " + logic + "\n" // TODO aFile.getLineSperator()
                   + getImplementationFromFunctionElment(functionElement, functionToCall.getArguments(), logic)
                   + "// end of " + logic;
        }
        else
        {
            implementation = getImplementationFromFunctionElment(functionElement, functionToCall.getArguments(), logic);
        }

        // log.trace("implementation = {}", implementation);
        if(null == implementation)
        {
            return null;
        }
        // add additional Stuff
        getAdditionalsFrom(logic);
        implementation = implementation.trim();
        implementation = replacePlaceholders(implementation, functionToCall.getArguments(), logic, functionElement);
        return implementation;
    }

    private String getImplementationFromFunctionElment(Element function, String FunctionArguments, ConfiguredAlgorithm logic)
    {
        // the algorithm might have the functions wrapped into if condition tags.
        List<Element> cond = function.getChildren(ALGORITHM_IF_CHILD_NAME);
        if((null == cond) || (true == cond.isEmpty()))
        {
            // No conditions -> only implementation
            return function.getText();
        }
        else
        {
            // multiple variants with conditions -> find the best one
            Element best = condiEval.getBest(cond, logic, FunctionArguments, function);
            if(null == best)
            {
                // function not found
                ctx.addError(this, "" + logic + " : no valid condition found!");
                ctx.addError(this, logic.toString());
                ctx.addError(this, logic.dumpParameter());
                ctx.addError(this, logic.dumpProperty());
                if(true == log.isTraceEnabled())
                {
                    XMLOutputter xmlOut = new XMLOutputter();
                    log.trace(xmlOut.outputString(function));
                }
                return null;
            }
            return best.getText();
        }
    }

    private Element getFunctionElement(String searchedFunctionName, String FunctionArguments, ConfiguredAlgorithm logic)
    {
        if(null == logic)
        {
            ctx.addError(this, "" + logic + " : Requesting function without providing an Algorithm!(function name: " + searchedFunctionName + ")");
            return null;
        }

        Element cCode = logic.getAlgorithmElement(ALGORITHM_C_CODE_CHILD_NAME);
        if(null == cCode)
        {
            ctx.addError(this,
                "Could not read implementation for " + searchedFunctionName +
                " from " + logic.toString());
            return null;
        }

        List<Element> funcs = cCode.getChildren(ALGORITHM_FUNCTION_CHILD_NAME);
        log.trace("func.size() : {}", funcs.size());
        for(int i = 0; i < funcs.size(); i++)
        {
            // check all functions
            Element curElement = funcs.get(i);
            String name = curElement.getAttributeValue(ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME);
            log.trace("func.name : {}", name);
            if(true == searchedFunctionName.equals(name))
            {
                // found the correct function
                return curElement;
            }
        }
        // function not found
        ctx.addError(this, "Function call to missing function! (" + logic
                        + ", function name : " + searchedFunctionName + " )");
        return null;
    }

    private String fillInFunctionCall(String functionName, ConfiguredAlgorithm logic)
    {
        // we now need to make sure that that function exists an can be called.
        // we therefore need to extract the function out of the children of this algorithm
        Iterator<String> it = logic.getAllChildren();
        StringBuffer res = new StringBuffer();
        boolean found = false;
        while(it.hasNext())
        {
            String ChildName = it.next();
            ConfiguredAlgorithm childAlgo = logic.getChild(ChildName);
            C_functionCall fc = new C_functionCall(functionName);
            String params = fc.getArguments();
            params = condiEval.evaluateConditionParenthesis(params, logic, null, null);
            fc.setFunctionArguments(params);
            String impl = getCImplementationOf(fc, childAlgo);
            if(null == impl)
            {
                continue;
            }
            else
            {
                found = true;
            }
            if(true == document_code_source)
            {
                res.append(
                        "// from " + logic + "\n" // TODO aFile.getLineSperator()
                        + impl + ";" + "\n" // TODO aFile.getLineSperator()
                        + "// end of " + logic +  "\n"); // TODO aFile.getLineSperator()
            }
            else
            {
                res.append(impl);
            }
        }
        if(false == found)
        {
            // The Implementation was not in one of the child elements !
            // -> it can only be in the required Library Algorithms
            if(true == functionName.contains(":"))
            {
                String libAlgoName = functionName.substring(0, functionName.indexOf(":"));
                ConfiguredAlgorithm libAlgo = ConfiguredAlgorithm.getTreeFromEnvironment(libAlgoName, ctx, logic);
                if(null == libAlgo)
                {
                    ctx.addError(this, "" + logic + " : The Environment does not provide the needed library (" + libAlgoName + ") !");
                    ctx.addError(this, "" + logic + " : We needed to call the function " + functionName + " !");
                    return null;
                }
                C_functionCall fc = new C_functionCall(functionName);
                String impl = getCImplementationOf(fc, libAlgo);
                if(null == impl)
                {
                    ctx.addError(this, "" + logic + " : Function call to missing (lib) function (" + functionName + ") !");
                    return null;
                }
                if(true == document_code_source)
                {
                    res.append(
                            "// from " + logic + "\n" // TODO aFile.getLineSperator()
                            + impl + ";" + "\n" // TODO aFile.getLineSperator()
                            + "// end of " + logic +  "\n"); // TODO aFile.getLineSperator()
                }
                else
                {
                    res.append(impl);
                }
            }
            else
            {
                ctx.addError(this, "" + logic + " : Function call to missing function (" + functionName + ") !");
                return null;
            }
        }
        return res.toString();
    }

    private String replacePlaceholders(String implementation,
            String FunctionParameters,
            ConfiguredAlgorithm logic,
            Element functionElement)
    {
        StringBuffer res = new StringBuffer();
        String[] parts = implementation.split(IMPLEMENTATION_PLACEHOLDER_REGEX);
        for(int i = 0; i < parts.length; i++)
        {
            if(0 == i%2)
            {
                res.append(parts[i]);
            }
            else
            {
                if(true == parts[i].endsWith(")"))
                {
                    if(false == parts[i].contains("("))
                    {
                        ctx.addError(this,
                            "Invalid Function Name (missing open brace?) " + parts[i] );
                        return null;
                    }
                    else
                    {
                        // we found a reference to a function name
                        String functionName = parts[i];
                        String help = fillInFunctionCall(functionName, logic);
                        if(null == help)
                        {
                            return null;
                        }
                        else
                        {
                            res.append(help);
                        }
                    }
                }
                else
                {
                    String paramValue = getFunctionParameterValue(parts[i],
                            functionElement,
                            FunctionParameters);
                    if(null == paramValue)
                    {
                        // Not a parameter passed in the function call,
                        // but a parameter in the algorithm configuration?
                        paramValue = logic.getParameter(parts[i]);
                    }

                    if(null == paramValue)
                    {
                        ctx.addError(this,
                            "Invalid parameter requested : " + parts[i] );
                        ctx.addError(this,"available parameters: " + logic.dumpParameter());
                        return null;
                    }
                    else
                    {
                        res.append(paramValue);
                    }
                }
            }
        }
        return res.toString();
    }

    private String getFunctionParameterValue(String ParameterName,
            Element functionElement,
            String FunctionParameters )
    {
        // Reference to Algorithm parameter
        // get which parameter this is (1st 2nd 3rd,..)
        int ParamIndex = 0;
        do {
            Attribute attr = functionElement.getAttribute("param" + ParamIndex + "_name");
            if(null == attr)
            {
                log.trace("Function parameter {} not found !", ParameterName);
                return null;
            }
            if(true == ParameterName.equals(attr.getValue()))
            {
                break;
            }
            else
            {
                ParamIndex++;
            }
        }while(true);

        // get value for that parameter from FunctionParameters
        String[] parameters = FunctionParameters.split(",");
        if(ParamIndex < parameters.length)
        {
            return parameters[ParamIndex];
        }
        else
        {
            ctx.addError(this,
                    "Could not get the " + ParamIndex
            + ". parameter to this function from the parameters given as " + FunctionParameters );
                return null;
        }
    }

    private void getAdditionalsFrom(ConfiguredAlgorithm logic)
    {
        Element cCode = logic.getAlgorithmElement(ALGORITHM_C_CODE_CHILD_NAME);
        if(null == cCode)
        {
            ctx.addError(this,
                "Could not read implementation from " + logic.toString());
            return;
        }
        Element additional = cCode.getChild(ALGORITHM_ADDITIONAL_C_CODE_CHILD_NAME);
        if(null == additional)
        {
            log.trace("no addionals for algorithm {}", logic);
            return;
        }
        List<Element> addlist = additional.getChildren();
        if(null == addlist)
        {
            log.trace("empty addionals tag for algorithm {}", logic);
            return;
        }
        for(int i = 0; i < addlist.size(); i++)
        {
            Element curElement = addlist.get(i);
            String type = curElement.getName();
            switch(type)
            {
            case ALGORITHM_ADDITIONAL_INCLUDE_CHILD_NAME:
                String include = curElement.getText();
                log.trace("adding include {}", include);
                if(true == document_code_source)
                {
                    sourceFile.addLineWithComment(C_File.C_FILE_INCLUDE_SECTION_NAME,
                            include, logic.toString());
                }
                else
                {
                    sourceFile.addLine(C_File.C_FILE_INCLUDE_SECTION_NAME, include);
                }
                break;

            case ALGORITHM_ADDITIONAL_FUNCTION_CHILD_NAME:
                Function func = new Function(curElement);

                if(true == document_code_source)
                {
                    sourceFile.addFunction(func, logic);
                }
                else
                {
                    sourceFile.addFunction(func);
                }
                break;

            case ALGORITHM_ADDITIONAL_FILE_CHILD_NAME:
                TextFile addFile = new TextFile(curElement.getAttributeValue(ALGORITHM_ADDITIONAL_FILE_NAME_ATTRIBUTE));
                addFile.createSection("content");
                addFile.addLine("content", curElement.getText());
                codeGroup.add(addFile);
                break;

            default: // ignore
                log.warn("invalid type {} for algorithm {}", type, logic);
                break;
            }
        }
        return;
    }


    private C_File createFile(String fileName)
    {
        C_File aFile = new C_File(fileName);

        // there should be a file comment explaining what this is
        aFile.addLines(C_File.C_FILE_FILE_COMMENT_SECTION_NAME,
                       new String[] {"/*",
                                     "  automatically created " + fileName,
                                     "  created at: " + Tool.curentDateTime(),
                                     "  created from " + ctx.cfg().getString(Configuration.SOLUTION_FILE_CFG),
                                     "*/"});
        return aFile;
    }

}
;
