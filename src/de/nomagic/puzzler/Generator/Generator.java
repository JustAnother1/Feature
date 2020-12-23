
package de.nomagic.puzzler.Generator;

import java.util.HashMap;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.AlgorithmInstanceInterface;
import de.nomagic.puzzler.solution.ConditionEvaluator;

public abstract class Generator extends Base
{
    public static final String ALGORITHM_IF_CHILD_NAME = "if";
    public static final String ALGORITHM_FOR_CHILDS_CHILD_NAME = "forChilds";
    public static final String ALGORITHM_FOR_CHILDS_CHILD_API_ATTRIBUTE = "api";
    public static final String ALGORITHM_FOR_CHILDS_CHILD_CALL_ATTRIBUTE = "call";
    public static final String ALGORITHM_FUNCTION_CHILD_NAME = "function";
    public static final String ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME = "name";
    public static final String ALGORITHM_ADDITIONAL_C_CODE_CHILD_NAME = "additional";
    public static final String ALGORITHM_ADDITIONAL_INCLUDE_CHILD_NAME = "include";
    public static final String ALGORITHM_ADDITIONAL_FILE_CHILD_NAME = "file";
    public static final String ALGORITHM_ADDITIONAL_VARIABLE_CHILD_NAME = "variable";

    public static final String IMPLEMENTATION_PLACEHOLDER_REGEX = "€";
    // configuration
    public static final String CFG_DOC_CODE_SRC = "document_code_source";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    // if this is true then all code snippet will be wrapped into comment lines
    // explaining where they came from.
    protected boolean documentCodeSource = false;
    // generated files are collected in this
    protected FileGroup codeGroup;
    protected ConditionEvaluator condiEval;
    protected HashMap<String, CInitCodeBlock> initcode = new HashMap<String, CInitCodeBlock>();

    public Generator(Context ctx)
    {
        super(ctx);
        condiEval = new ConditionEvaluator(ctx);
    }

    public void configure(Configuration cfg)
    {
        if(null == cfg)
        {
            return;
        }
        if("true".equals(cfg.getString(CFG_DOC_CODE_SRC)))
        {
            log.trace("Switching on documentation of source code");
            documentCodeSource = true;
        }
    }

    public abstract FileGroup generateFor(AlgorithmInstanceInterface logic);

    public abstract String getLanguageName();

    protected String addCommentsToImplementation(String Implementation, AlgorithmInstanceInterface logic)
    {
        return "// from " + logic + System.getProperty("line.separator")
        + Implementation + System.getProperty("line.separator")
        + "// end of " + logic + System.getProperty("line.separator");
    }

    protected abstract String fillInFunctionCall(String functionName, AlgorithmInstanceInterface logic);

    protected String replacePlaceholdersInPart(String implementation,
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
                        if(null != paramValue)
                        {
                            log.trace("Found {} as value for {} in algorithm configuration.", paramValue, parts[i]);
                        }
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

    protected String getFunctionParameterValue(String ParameterName,
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

    protected String replacePlaceholders(String implementation,
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
            log.warn("Function Parameters are null, changing to empty String !");
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

    protected String handleBracesInImplementation(String implementation,
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

}
