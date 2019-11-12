
package de.nomagic.puzzler.Generator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.CFile;
import de.nomagic.puzzler.FileGroup.FileFactory;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.AlgorithmInstanceInterface;
import de.nomagic.puzzler.solution.Api;
import de.nomagic.puzzler.solution.ConditionEvaluator;
import de.nomagic.puzzler.solution.ConfiguredAlgorithm;
import de.nomagic.puzzler.solution.Function;

public class CCodeGenerator extends Generator
{
    public static final String REQUIRED_ROOT_API = "program_entry_point";
    public static final String IMPLEMENTATION_PLACEHOLDER_REGEX = "€";
    public static final String ALGORITHM_C_CODE_CHILD_NAME = "c_code";

    public static final String ALGORITHM_IF_CHILD_NAME = "if";
    public static final String ALGORITHM_FUNCTION_CHILD_NAME = "function";
    public static final String ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME = "name";
    public static final String ALGORITHM_ADDITIONAL_C_CODE_CHILD_NAME = "additional";
    public static final String ALGORITHM_ADDITIONAL_INCLUDE_CHILD_NAME = "include";
    public static final String ALGORITHM_ADDITIONAL_FUNCTION_CHILD_NAME = "function";
    public static final String ALGORITHM_INITIALISATION_FUNCTION_NAME = "initialize";
    public static final String ALGORITHM_ADDITIONAL_FILE_CHILD_NAME = "file";
    public static final String ALGORITHM_ADDITIONAL_VARIABLE_CHILD_NAME = "variable";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private ConditionEvaluator condiEval;
    private FileGroup codeGroup;
    private CFile sourceFile;
    private HashMap<String, CInitCodeBlock> initcode = new HashMap<String, CInitCodeBlock>();

    public CCodeGenerator(Context ctx)
    {
        super(ctx);
        condiEval = new ConditionEvaluator(ctx);
    }

    @Override
    public String getLanguageName()
    {
        return "C";
    }

    public FileGroup generateFor(AlgorithmInstanceInterface logic)
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

        Api api = Api.getFromFile(REQUIRED_ROOT_API, ctx);
        if(null == api)
        {
            ctx.addError(this, "" + logic + " : Failed to load the api " + REQUIRED_ROOT_API + " !");
            return null;
        }

        addImplementationForTo(api, logic, "main.c");
        if(false == ctx.wasSucessful())
        {
            return null;
        }
        addInitCode();

        return codeGroup;
    }

    private void addInitCode()
    {
        Element funcElement = new Element(ALGORITHM_ADDITIONAL_FUNCTION_CHILD_NAME);
        funcElement.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, ALGORITHM_INITIALISATION_FUNCTION_NAME);
        funcElement.setAttribute(Function.FUNCTION_TYPE_ATTRIBUTE_NAME, Function.FUNCTION_REQUIRED_TYPE);
        Function initFunc = new Function(funcElement);
        StringBuilder sb = new StringBuilder();
        for (String curBlock : initcode.keySet())
        {
            log.trace("Found init code for {}", curBlock);
        }

        for (CInitCodeBlock curBlock : initcode.values())
        {
            sb.append(curBlock.getImplemenation());
            sb.append("\n");
        }
        initFunc.setImplementation(sb.toString());

        CFile main = (CFile)codeGroup.getFileWithName("main.c");
        main.addFunction(initFunc);
    }

    private void addImplementationForTo(Api api, AlgorithmInstanceInterface logic, String curFileName)
    {
        AbstractFile curAbsFile = codeGroup.getFileWithName(curFileName);
        if(null == curAbsFile)
        {
            sourceFile = createFile(curFileName);
        }
        else if(curAbsFile instanceof CFile)
        {
            sourceFile = (CFile)curAbsFile;
        }
        else
        {
            // we should add code to a file that is not a C File!
            log.error("Can not add C source code of {} to {} !", logic, curFileName);
            ctx.addError(this, "Can not add C source code of " + logic + " to " + curFileName + " !");
            return;
        }

        // ... now we can add the code to sourceFile

        log.trace("getting implementation of the {} from {}", api, logic);
        Function[] funcs = api.getRequiredFunctions();
        for(int i = 0; i < funcs.length; i++)
        {
            CFunctionCall fc = new CFunctionCall(funcs[i].getName());
            String implementation = getCImplementationOf(fc, logic);
            if(null == implementation)
            {
                String error = "Could not get an Implementation for " + funcs[i].getName();
                log.error(error);
                ctx.addError(this, error);
                return;
            }
            else
            {
                funcs[i].setImplementation(implementation);
            }

            if(true == documentCodeSource)
            {
                sourceFile.addFunction(funcs[i], logic.toString());
            }
            else
            {
                sourceFile.addFunction(funcs[i]);
            }
        }

        codeGroup.add(sourceFile);
    }

    private String addCommentsToImplementation(String Implementation, AlgorithmInstanceInterface logic)
    {
        return "// from " + logic + System.getProperty("line.separator")
        + Implementation + System.getProperty("line.separator")
        + "// end of " + logic;
    }

    private void handleInitFunctionOfAlgorithm(AlgorithmInstanceInterface logic)
    {
        // check if logic hat initialize function, if so then add that to the initcode
        Element cCode = logic.getAlgorithmElement(ALGORITHM_C_CODE_CHILD_NAME);
        if(null != cCode)
        {
            List<Element> funcs = cCode.getChildren(ALGORITHM_FUNCTION_CHILD_NAME);
            if(null != funcs)
            {
                for(int i = 0; i < funcs.size(); i++)
                {
                    // check all functions
                    Element curElement = funcs.get(i);
                    String name = curElement.getAttributeValue(ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME);
                    if(true == ALGORITHM_INITIALISATION_FUNCTION_NAME.equals(name))
                    {
                        // found another init function
                        log.trace("Found initFunctuntion in {}", logic);
                        String id = logic.toString();
                        String impl = getImplementationFromFunctionElement(curElement, "", logic);
                        if(true == documentCodeSource)
                        {
                            impl = addCommentsToImplementation(impl, logic);
                        }
                        impl = impl.trim();
                        impl = replacePlaceholders(impl, "", logic, curElement);
                        CInitCodeBlock initB = new CInitCodeBlock(id, impl);
                        initcode.put(id, initB);
                    }
                }
            }
        }
    }

    private String getCImplementationOf(CFunctionCall functionToCall, AlgorithmInstanceInterface logic)
    {
        log.trace("getting the c implemention of the function {} from {}",
                functionToCall, logic);

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

        if( false == logic.hasApi(functionToCall.getApi()))
        {
            log.warn("{} : Function call to wrong API!(API: {})", logic, functionToCall.getApi());
            log.warn("Function called: {}",  functionToCall.getName());
            return null;
        }

        handleInitFunctionOfAlgorithm(logic);
        if(false == ctx.wasSucessful())
        {
            return null;
        }

        functionToCall.setFunctionArguments(
                condiEval.evaluateConditionParenthesis(functionToCall.getArguments(), logic, null, null));

        Element functionElement = getFunctionElement(searchedFunctionName, functionToCall.getArguments(), logic);
        if(null == functionElement)
        {
            return null;
        }
        String implementation = getImplementationFromFunctionElement(functionElement, functionToCall.getArguments(), logic);

        if(null == implementation)
        {
            return null;
        }

        if(true == documentCodeSource)
        {
            implementation = addCommentsToImplementation(implementation, logic);
        }

        getAdditionalsFrom(logic);
        implementation = implementation.trim();
        implementation = replacePlaceholders(implementation, functionToCall.getArguments(), logic, functionElement);
        if(false == ctx.wasSucessful())
        {
            return null;
        }
        else
        {
            return implementation;
        }
    }

    private String getImplementationFromFunctionElement(Element function, String FunctionArguments, AlgorithmInstanceInterface logic)
    {
        // the algorithm might have the functions wrapped into if condition tags.
        List<Element> cond = function.getChildren(ALGORITHM_IF_CHILD_NAME);
        if((null == cond) || (true == cond.isEmpty()))
        {
            // No conditions -> only implementation
            String impl = function.getText();
            log.trace("found unconditional implementation : {}", impl);
            return impl;
        }
        else
        {
            // TODO: multiple variants with conditions -> find the best one
            // some part of the Implementation is conditional, So only select the valid parts
            StringBuilder sb = new StringBuilder();
            List<Content> parts = function.getContent();
            for (int i = 0; i < parts.size() ; i++)
            {
                Content curC = parts.get(i);
                if(CType.Element == curC.getCType())
                {
                    Element curE = (Element)curC;
                    if(ALGORITHM_IF_CHILD_NAME.equals(curE.getName()))
                    {
                        // this part is conditional -> check if we need it.
                        Element active = condiEval.getBest(curE, logic, FunctionArguments, function);
                        if(null != active)
                        {
                            String impl = active.getText();
                            log.trace("adding the conditioned parts to the implementation : {}", impl);
                            sb.append(impl);
                        }
                        else
                        {
                            log.trace("The condition {} is not true", curE.getAttribute(ConditionEvaluator.CONDITION_EVALUATOR_CONDITION_ATTRIBUTE_NAME));
                        }
                        // else this part is not used this time.
                    }
                    else
                    {
                        String impl = curE.getText();
                        sb.append(impl);
                        log.trace("Adding non conditional Element data to implementation : {}", impl);
                    }
                }
                else
                {
                    // Not an element, therefore can not have if conditions,
                    // therefore we can just extract all the text.
                    String impl = curC.getValue();
                    sb.append(impl);
                    log.trace("adding non element code to implmentation: {}", impl);
                }
            }
            return sb.toString();
        }
    }

    private Element getFunctionElement(String searchedFunctionName, String FunctionArguments, AlgorithmInstanceInterface logic)
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

    private String fillInFunctionCall(String functionName, AlgorithmInstanceInterface logic)
    {
        log.trace("filling in the code for the function call to {} in {}", functionName, logic);
        // we now need to make sure that that function exists an can be called.
        // we therefore need to extract the function out of the children of this algorithm
        Iterator<String> it = logic.getAllChildren();
        StringBuilder res = new StringBuilder();

        CFunctionCall fc = new CFunctionCall(functionName);
        String params = fc.getArguments();
        params = condiEval.evaluateConditionParenthesis(params, logic, null, null);
        fc.setFunctionArguments(params);

        boolean found = false;
        while(it.hasNext())
        {
            String childName = it.next();
            ConfiguredAlgorithm childAlgo = logic.getChild(childName);
            if(true == childAlgo.hasApi(fc.getApi()))
            {
                String impl = getCImplementationOf(fc, childAlgo);
                if(null == impl)
                {
                    continue;
                }
                else
                {
                    found = true;
                }
                if(true == documentCodeSource)
                {
                    res.append(addCommentsToImplementation(impl, logic));
                }
                else
                {
                    res.append(impl);
                }
            }
            // else this child is for something else
        }
        if(false == found)
        {
            // The Implementation was not in one of the child elements !
            // -> it can only be in the required Library Algorithms
            if(true == functionName.contains(":"))
            {
                String libAlgoName = functionName.substring(0, functionName.indexOf(':'));
                // include the library
                ConfiguredAlgorithm libAlgo = ConfiguredAlgorithm.getTreeFromEnvironment(libAlgoName, ctx, logic);
                if(null == libAlgo)
                {
                    ctx.addError(this, "" + logic + " : The Environment does not provide the needed library (" + libAlgoName + ") !");
                    ctx.addError(this, "" + logic + " : We needed to call the function " + functionName + " !");
                    return null;
                }
                CFunctionCall libfc = new CFunctionCall(functionName);
                String impl = getCImplementationOf(libfc, libAlgo);
                if(null == impl)
                {
                    ctx.addError(this, "" + logic + " : Function call to missing (lib) function (" + functionName + ") !");
                    return null;
                }
                if(true == documentCodeSource)
                {
                    res.append(addCommentsToImplementation(impl, logic));
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

    private String replacePlaceholdersInPart(String implementation,
            String FunctionParameters,
            AlgorithmInstanceInterface logic,
            Element functionElement)
    {
        StringBuilder res = new StringBuilder();
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
                        ctx.addError(this,"available properties: " + logic.dumpProperty());
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

    private String handleBracesInImplementation(String implementation,
            String FunctionParameters,
            AlgorithmInstanceInterface logic,
            Element functionElement)
    {
        HashMap<Integer,StringBuilder> partsMap = new HashMap<Integer,StringBuilder>();
        Integer level = 0;
        StringBuilder curPart =  new StringBuilder();
        partsMap.put(level, curPart);
        for(int i = 0; i < implementation.length(); i++)
        {
            char c = implementation.charAt(i);
            switch(c)
            {
            case'(':
                partsMap.put(level, curPart);
                level++;
                curPart =  new StringBuilder();
                break;

            case')':
                String part = curPart.toString();
                if(true == part.contains(IMPLEMENTATION_PLACEHOLDER_REGEX))
                {
                    part = replacePlaceholdersInPart(part, FunctionParameters, logic, functionElement);
                    if(null == part)
                    {
                        return null;
                    }
                }
                else
                {
                    // nothing to replace
                }
                level--;
                curPart = partsMap.get(level);
                curPart.append('(' + part + ')');
                break;

            default:
                curPart.append(c);
                break;
            }
        }
        String part = curPart.toString();
        if(true == part.contains(IMPLEMENTATION_PLACEHOLDER_REGEX))
        {
            part = replacePlaceholdersInPart(part, FunctionParameters, logic, functionElement);
        }
        else
        {
            // nothing to replace
        }
        return part;
    }

    private String replacePlaceholders(String implementation,
            String FunctionParameters,
            AlgorithmInstanceInterface logic,
            Element functionElement)
    {
        int numEuros = 0;
        int numOpenBraces = 0;
        int numClosingBaces = 0;
        for(int i = 0; i < implementation.length(); i++)
        {
            switch(implementation.charAt(i))
            {
            case '€': numEuros++; break;
            case '(': numOpenBraces++; break;
            case ')': numClosingBaces++; break;
            default: break;
            }
        }

        if(0 == numEuros)
        {
            return implementation;
        }

        if(0 != numEuros%2)
        {
            ctx.addError(this,"Invalid Syntax: odd number of € ! ");
            return null;
        }
        if(numOpenBraces != numClosingBaces)
        {
            ctx.addError(this,"Invalid Syntax: braces don't match"
                    + " (open: " + numOpenBraces + "; close: " +  numClosingBaces + ") !");
            return null;
        }

        if(0 != numOpenBraces)
        {
            return handleBracesInImplementation(implementation, FunctionParameters, logic, functionElement);
        }
        else
        {
            if(true == implementation.contains(IMPLEMENTATION_PLACEHOLDER_REGEX))
            {
                return replacePlaceholdersInPart(implementation, FunctionParameters, logic, functionElement);
            }
            else
            {
                // nothing to replace
                return implementation;
            }
        }
    }

    private String getFunctionParameterValue(String ParameterName,
            Element functionElement,
            String FunctionParameters )
    {
        // Reference to Algorithm parameter
        // get which parameter this is (1st 2nd 3rd,..)
        int paramIndex = 0;
        do {
            Attribute attr = functionElement.getAttribute("param" + paramIndex + "_name");
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
                paramIndex++;
            }
        }while(true);

        // get value for that parameter from FunctionParameters
        String[] parameters = FunctionParameters.split(",");
        if(paramIndex < parameters.length)
        {
            return parameters[paramIndex];
        }
        else
        {
            ctx.addError(this,
                    "Could not get the " + paramIndex
            + ". parameter to this function from the parameters given as " + FunctionParameters );
                return null;
        }
    }

    private void getAdditionalsFrom(AlgorithmInstanceInterface logic)
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
                if(true == documentCodeSource)
                {
                    sourceFile.addLineWithComment(CFile.C_FILE_INCLUDE_SECTION_NAME,
                            include, logic.toString());
                }
                else
                {
                    sourceFile.addLine(CFile.C_FILE_INCLUDE_SECTION_NAME, include);
                }
                break;

            case ALGORITHM_ADDITIONAL_FUNCTION_CHILD_NAME:
                Function func = new Function(curElement);

                if(true == documentCodeSource)
                {
                    sourceFile.addFunction(func, logic.toString());
                }
                else
                {
                    sourceFile.addFunction(func);
                }
                break;

            case ALGORITHM_ADDITIONAL_FILE_CHILD_NAME:
                AbstractFile aFile = FileFactory.getFileFromXml(curElement);
                codeGroup.add(aFile);
                break;

            case ALGORITHM_ADDITIONAL_VARIABLE_CHILD_NAME:
                sourceFile.addLine(CFile.C_FILE_GLOBAL_VAR_SECTION_NAME, curElement.getText());
                break;

            default: // ignore
                log.warn("invalid type '{}' for algorithm '{}' !", type, logic);
                break;
            }
        }
    }


    private CFile createFile(String fileName)
    {
        CFile aFile = new CFile(fileName);

        // there should be a file comment explaining what this is
        aFile.addLines(CFile.C_FILE_FILE_COMMENT_SECTION_NAME,
                       new String[] {"/*",
                                     "  automatically created " + fileName,
                                     "  created at: " + Tool.curentDateTime(),
                                     "  created from " + ctx.cfg().getString(Configuration.SOLUTION_FILE_CFG),
                                     "*/"});
        return aFile;
    }

}
;
