package de.nomagic.puzzler.solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Content.CType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Generator.CCodeGenerator;
import de.nomagic.puzzler.Generator.CFunctionCall;
import de.nomagic.puzzler.Generator.Generator;
import de.nomagic.puzzler.configuration.Configuration;

public class ImplementationPuzzlerC extends Base
{
    public static final String ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME = "name";
    public static final String ALGORITHM_IF_CHILD_NAME = "if";
    public static final String ALGORITHM_FOR_CHILDS_CHILD_NAME = "forChilds";
    public static final String ALGORITHM_FOR_CHILDS_CHILD_API_ATTRIBUTE = "api";
    public static final String IMPLEMENTATION_PLACEHOLDER_REGEX = "€";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final AlgorithmInstanceInterface algo;

    // if this is true then all code snippet will be wrapped into comment lines
    // explaining where they came from.
    private boolean documentCodeSource = false;

    public ImplementationPuzzlerC(Context ctx, AlgorithmInstanceInterface algo)
    {
        super(ctx);
        this.algo = algo;
        if(null == ctx)
        {
            log.error("Context is missing!");
            return;
        }
        Configuration cfg = ctx.cfg();
        if(null == cfg)
        {
            log.error("Configuration is missing!");
            return;
        }
        if("true".equals(cfg.getString(Configuration.CFG_DOC_CODE_SRC)))
        {
            log.trace("Switching on documentation of source code");
            documentCodeSource = true;
        }
    }

    public String getImplementationOf(CFunctionCall functionToCall)
    {
        if(null == functionToCall)
        {
            ctx.addError(this, "" + algo + " : Function call to null function!");
            return null;
        }
        String searchedFunctionName = functionToCall.getName();
        if(null == searchedFunctionName)
        {
            ctx.addError(this, "" + algo + " : Function call to unknown function!");
            return null;
        }
        if(1 > searchedFunctionName.length())
        {
            ctx.addError(this, "" + algo + " : Function call to unnamed function!");
            return null;
        }

        String api = functionToCall.getApi();
        if(null != api)
        {
            if(false == algo.hasApi(api))
            {
                log.warn("{} : Function call to wrong API!(API: {})", this, api);
                log.warn("valid APIs : {}",  algo.getApis());
                log.warn("Function called: {}",  functionToCall.getName());
                return null;
            }
            else
            {
                // OK
            }
        }
        else
        {
            // api == null
            log.warn("{} : Function call to unknown API! called function was '{}'", algo, functionToCall.getName());
            return null;
        }

        if(false == ctx.wasSucessful())
        {
            return null;
        }

        functionToCall.setFunctionArguments(functionToCall.getArguments());

        Element functionElement = getFunctionElement(searchedFunctionName, functionToCall.getArguments());
        if(null == functionElement)
        {
            return null;
        }
        String implementation = getImplementationFromFunctionElement(functionElement, functionToCall.getArguments());

        if(null == implementation)
        {
            return null;
        }

        implementation = replacePlaceholders(implementation, functionToCall.getArguments(), algo, functionElement);
        if(false == ctx.wasSucessful())
        {
            return null;
        }
        else
        {
            if(true == documentCodeSource)
            {
                return "// from " + toString() + System.getProperty("line.separator")
                       + implementation   // Implementation always comes with a line end
                       + "// end of " + toString() + System.getProperty("line.separator");
            }
            else
            {
                return implementation;
            }
        }
    }

    private String beautifyImplementation(String impl)
    {
        impl = impl.trim();
        if(0 < impl.length())
        {
            if(false == impl.endsWith(System.getProperty("line.separator")))
            {
                impl = impl + System.getProperty("line.separator");
            }
            return impl;
        }
        else
        {
            return "";
        }
    }

    // this only gets the implementation parts that are active. (evaluates IF). It does not replace place holders!
    private String getImplementationFromFunctionElement(Element function, String FunctionArguments)
    {
        // some part of the Implementation might be conditional, So only select the valid parts
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
                    Element active = ConditionEvaluator.getBest(curE, algo, FunctionArguments, function);
                    if(null != active)
                    {
                        String impl = beautifyImplementation(active.getText());
                        log.trace("adding the conditioned parts to the implementation : {}", impl);
                        sb.append(impl);
                    }
                    else
                    {
                        log.trace("The condition {} is not true", curE.getAttribute(ConditionEvaluator.CONDITION_EVALUATOR_CONDITION_ATTRIBUTE_NAME));
                    }
                    // else this part is not used this time.
                }
                else  if(ALGORITHM_FOR_CHILDS_CHILD_NAME.equals(curE.getName()))
                {
                    String api = curE.getAttributeValue(ALGORITHM_FOR_CHILDS_CHILD_API_ATTRIBUTE);
                    if(null == api)
                    {
                        ctx.addError(this, "" + algo + " : for childs element with missing api attribute !)");
                        return null;
                    }
                    Api theApi = Api.getFromFile(api, ctx);
                    if(null == theApi)
                    {
                        ctx.addError(this, "" + algo + " : for childs element with invalid api attribute ! (" + api + "))");
                        return null;
                    }
                    String commonCode = curE.getText();
                    Iterator<String> it = algo.getAllChildren();
                    while(it.hasNext())
                    {
                        String childName = it.next();
                        AlgorithmInstanceInterface curChild = algo.getChild(childName);
                        if(true == curChild.hasApi(api))
                        {
                            String implementation = replacePlaceHolders(commonCode, curChild);
                            if(null == implementation)
                            {
                                String error = "Could not get an Implementation for '" + commonCode + "' from " + curChild;
                                log.error(error);
                                ctx.addError(this, error);
                                return null;
                            }
                            else
                            {
                                implementation = beautifyImplementation(implementation);
                                algo.addExtraAlgo(curChild);
                                sb.append(implementation);
                            }
                        }
                        // else don't care for that child
                    }
                }
                else
                {
                    String impl = beautifyImplementation(curE.getText());
                    sb.append(impl);
                    log.warn("Adding non conditional Element data to implementation ! text:  {} element: {}", impl, curE);
                }
            }
            else if(CType.Comment == curC.getCType())
            {
                // ignore comments
            }
            else
            {
                // Not an element, therefore can not have if conditions,
                // therefore we can just extract all the text.
                String impl = beautifyImplementation(curC.getValue());
                sb.append(impl);
            }
        }
        return sb.toString();
    }


    private Element getFunctionElement(String searchedFunctionName, String FunctionArguments)
    {
        Element cCode = algo.getAlgorithmElement(CCodeGenerator.ALGORITHM_C_CODE_CHILD_NAME);
        if(null == cCode)
        {
            ctx.addError(this,
                "Could not read implementation for " + searchedFunctionName +
                " from " + toString());
            return null;
        }

        List<Element> funcs = cCode.getChildren(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        log.trace("func.size() : {}", funcs.size());
        for(int i = 0; i < funcs.size(); i++)
        {
            // check all functions
            Element curElement = funcs.get(i);
            String curName = curElement.getAttributeValue(ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME);
            log.trace("func.name : {}", curName);
            if(true == searchedFunctionName.equals(curName))
            {
                // found the correct function
                return curElement;
            }
        }

        // search also additional Functions
        Element additional = cCode.getChild(Generator.ALGORITHM_ADDITIONAL_CHILD_NAME);
        if(null != additional)
        {
            List<Element> addlist = additional.getChildren();
            if(null != addlist)
            {
                for(int i = 0; i < addlist.size(); i++)
                {
                    Element curElement = addlist.get(i);
                    String type = curElement.getName();
                    if(true == Generator.ALGORITHM_FUNCTION_CHILD_NAME.equals(type))
                    {
                        String curName = curElement.getAttributeValue(ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME);
                        log.trace("func.name : {}", curName);
                        if(true == searchedFunctionName.equals(curName))
                        {
                            // found the correct function
                            return curElement;
                        }
                    }
                    // else -> ignore
                }
            }
        }

        // function not found
        ctx.addError(this, "Function call to missing function! (" + algo
                        + ", function name : " + searchedFunctionName + " )");
        return null;
    }

    public String replacePlaceHolders(String line, AlgorithmInstanceInterface child)
    {
        if(null == line)
        {
            return "";
        }
        if(true == line.contains(ImplementationPuzzlerC.IMPLEMENTATION_PLACEHOLDER_REGEX))
        {
            StringBuilder res = new StringBuilder();
            String[] parts = line.split(ImplementationPuzzlerC.IMPLEMENTATION_PLACEHOLDER_REGEX);
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
                            String help = fillInFunctionCall(functionName, child);
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
                        // might be something that puzzler itself can calculate from the Algorithm Instance.
                        String paramValue = algo.getBuildIn(parts[i]);
                        if(null != paramValue)
                        {
                            log.trace("Found {} as value for {} in build in configuration.", paramValue, parts[i]);
                        }

                        if(null == paramValue)
                        {
                            ctx.addError(this,
                                "Invalid parameter requested : " + parts[i] );
                            ctx.addError(this,"available parameters: " + algo.dumpParameter());
                            ctx.addError(this,"available properties: " + algo.dumpProperty());
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
        else
        {
            // nothing to replace
            return line;
        }
    }

    private String replacePlaceholders(String implementation,
            String FunctionParameters,
            AlgorithmInstanceInterface logic,
            Element functionElement)
    {
        int numEuros = 0;
        int numOpenBraces = 0;
        int numClosingBaces = 0;

        if(null == implementation)
        {
            ctx.addError(this,"Implementation is null ! ");
            return null;
        }
        if(null == FunctionParameters)
        {
            log.trace("Function Parameters are null, changing to empty String !");
            log.trace("Implementation is : {} !", implementation);
            FunctionParameters = "";
        }
        if(null == logic)
        {
            ctx.addError(this,"logic is null ! ");
            return null;
        }
        if(null == functionElement)
        {
            ctx.addError(this,"function element is null ! ");
            return null;
        }

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
            // nothing to replace in the implementation
            // -> another job well done !
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
            return handleBracesInImplementation(implementation, FunctionParameters, functionElement);
        }
        else
        {
            return replacePlaceholdersInPart(implementation, FunctionParameters, functionElement);
        }
    }


    private String handleBracesInImplementation(String implementation,
            String FunctionParameters,
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
                    part = replacePlaceholdersInPart(part, FunctionParameters, functionElement);
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
            part = replacePlaceholdersInPart(part, FunctionParameters, functionElement);
        }
        else
        {
            // nothing to replace
        }
        return part;
    }

    private String replacePlaceholdersInPart(String implementation,
            String FunctionParameters,
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
                        String help = fillInFunctionCall(functionName, null); //search for function in all children
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
                    // check function parameters
                    String paramValue = getFunctionParameterValue(parts[i],
                                                                  functionElement,
                                                                  FunctionParameters);
                    if(null == paramValue)
                    {
                        // Not a parameter passed in the function call,
                        // but a parameter in the algorithm configuration?
                        paramValue = algo.getParameter(parts[i]);
                        if(null != paramValue)
                        {
                            log.trace("Found {} as value for {} in algorithm configuration.", paramValue, parts[i]);
                        }
                    }

                    // check build in
                    if(null == paramValue)
                    {
                        // might be something that puzzler itself can calculate from the Algorithm Instance.
                        paramValue = algo.getBuildIn(parts[i]);
                        if(null != paramValue)
                        {
                            log.trace("Found {} as value for {} in build in configuration.", paramValue, parts[i]);
                        }
                    }

                    if(null == paramValue)
                    {
                        ctx.addError(this,
                            "Invalid parameter requested : " + parts[i] );
                        ctx.addError(this,"available parameters: " + algo.dumpParameter());
                        ctx.addError(this,"available properties: " + algo.dumpProperty());
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
        if(null == ParameterName)
        {
            log.error("Function parameter name is null !");
            return null;
        }
        if(null == functionElement)
        {
            log.error("Function element is null !");
            return null;
        }
        if(null == FunctionParameters)
        {
            log.error("Function parameters are null !");
            return null;
        }
        // Reference to Algorithm parameter
        // get which parameter this is (1st 2nd 3rd,..)
        int paramIndex = 0;
        do {
            Attribute attr = functionElement.getAttribute("param" + paramIndex + "_name");
            if(null == attr)
            {
                log.trace("Function parameter {} not found !(Algorithm configuration?)", ParameterName);
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
            ctx.addError(this, "Could not get the " + paramIndex
                + ". parameter to this function from the parameters given as "
                + FunctionParameters );
            return null;
        }
    }

    // if child is not null them call the function in that child only.
    private String fillInFunctionCall(String functionName, AlgorithmInstanceInterface child)
    {
        log.trace("filling in the code for the function call to {} in {}", functionName, algo);
        Iterator<String> it;
        if(null == child)
        {
            // we now need to make sure that that function exists an can be called.
            // we therefore need to extract the function out of the children of this algorithm
            it = algo.getAllChildren();
        }
        else
        {
            ArrayList<String> list = new ArrayList<String>();
            list.add(child.getName());
            it = list.iterator();
        }

        StringBuilder res = new StringBuilder();

        CFunctionCall fc = new CFunctionCall(functionName);
        String params = fc.getArguments();
        fc.setFunctionArguments(params);

        boolean found = false;
        while(it.hasNext())
        {
            String childName = it.next();
            AlgorithmInstanceInterface childAlgo = algo.getChild(childName);
            if(true == childAlgo.hasApi(fc.getApi()))
            {
                String impl = childAlgo.getImplementationOf(fc);
                if(null == impl)
                {
                    continue;
                }
                else
                {
                    found = true;
                }
                algo.addExtraAlgo(childAlgo);
                res.append(impl);
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
                ConfiguredAlgorithm libAlgo = ConfiguredAlgorithm.getTreeFromEnvironment(libAlgoName, ctx, algo);
                if(null == libAlgo)
                {
                    ctx.addError(this, "" + algo + " : The Environment does not provide the needed library (" + libAlgoName + ") !");
                    ctx.addError(this, "" + algo + " : We needed to call the function " + functionName + " !");
                    return null;
                }
                log.trace("adding the library algorithm {}", libAlgo.getName());
                algo.addExtraAlgo(libAlgo);
                CFunctionCall libfc = new CFunctionCall(functionName);
                String impl = libAlgo.getImplementationOf(libfc);
                if(null == impl)
                {
                    ctx.addError(this, "" + algo + " : Function call to missing (lib) function (" + functionName + ") !");
                    return null;
                }
                res.append(impl);
            }
            else
            {
                ctx.addError(this, "" + algo + " : Function call to missing function (" + functionName + ") !");
                return null;
            }
        }
        return res.toString();
    }

}
