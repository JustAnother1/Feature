package de.nomagic.puzzler.Generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.CFile;
import de.nomagic.puzzler.FileGroup.FileFactory;
import de.nomagic.puzzler.FileGroup.SourceFile;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.Algo_c_code;
import de.nomagic.puzzler.solution.Algo_c_code_impl;
import de.nomagic.puzzler.solution.AlgorithmInstanceInterface;
import de.nomagic.puzzler.solution.Api;
import de.nomagic.puzzler.solution.Function;
import de.nomagic.puzzler.solution.Solution;

/** generate C source code.
 *
 * This call takes AlgorithmInstanceInterface and extracts the C source code as a set of files.
 *
 */
public class C_CodeGenerator extends Generator
{
    public static final String ALGORITHM_CODE_CHILD_NAME = "c_code";
    public static final String ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME = "name";
    public static final String ALGORITHM_IF_CHILD_NAME = "if";
    public static final String ALGORITHM_FOR_CHILDS_CHILD_NAME = "forChilds";
    public static final String ALGORITHM_FOR_CHILDS_CHILD_API_ATTRIBUTE = "api";
    public static final String IMPLEMENTATION_PLACEHOLDER_REGEX = "€";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    // collects all ConfiguredAlgorithm classes used. The collected classes will
    // be asked for additional elements and those will be added to the generated code.
    // includes at least the own instance.
    private HashSet<AlgorithmInstanceInterface> extraAlgoList;
    // if this is true then all code snippet will be wrapped into comment lines
    // explaining where they came from.
    private boolean documentCodeSource = false;
    private SourceFile sourceFile;


    public C_CodeGenerator(Context ctx)
    {
        super(ctx);
        if(null == ctx)
        {
            log.error("Received no Context!");
            return;
        }
        Configuration cfg = ctx.cfg();
        if(null == cfg)
        {
            log.error("Received no Configuration!");
            return;
        }
        if("true".equals(ctx.cfg().getString(Configuration.CFG_DOC_CODE_SRC)))
        {
            log.trace("Switching on documentation of source code");
            documentCodeSource = true;
        }
        ROOT_FILE_NAME = "main.c";
    }

    @Override
    public String getLanguageName()
    {
        return "C";
    }
    
    @Override
    public String toString()
    {
    	return "C_CodeGenerator";
    }

    protected SourceFile createFile(String fileName)
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

    /** collects the implementations for all functions contained in the API.
     * Also adds implementations of additional required functions and adds everything to the codeGroup.
     * @param api Functions that the source code will be generated for.
     * @param logic the Algorithm that is converted to source code.
     * @return true == OK, false = something went wrong, no code generated.
     */
    @Override
    protected boolean generateSourceCodeFor(Api api, AlgorithmInstanceInterface logic)
    {
        extraAlgoList = new HashSet<AlgorithmInstanceInterface>();
        extraAlgoList.add(logic);
        sourceFile = createFile(ROOT_FILE_NAME);
        // ... now we can add the code to sourceFile

        log.trace("getting implementation of the {} from {}", api, logic);
        Function[] funcs = api.getRequiredFunctions();
        if(0 == funcs.length)
        {
            String error = "Could not get required function for the API " + api;
            log.error(error);
            ctx.addError(this, error);
        	return false;
        }
        for(int i = 0; i < funcs.length; i++)
        {
            C_FunctionCall fc = new C_FunctionCall(funcs[i].getName());
            fc.setApi(api.toString());
            String implementation = getImplementationOf(fc, logic);
            if(null == implementation)
            {
                String error = "Could not get an Implementation for " + funcs[i].getName();
                log.error(error);
                ctx.addError(this, error);
                return false;
            }
            else
            {
                funcs[i].setImplementation(implementation);
                sourceFile.addFunction(funcs[i]);
            }
        }
        addAllAdditionals(logic);
        codeGroup.add(sourceFile);
        return true;
    }

    private boolean checkImplementationRequest(C_FunctionCall functionToCall, AlgorithmInstanceInterface algo)
    {
        if(null == algo)
        {
            ctx.addError(this, "Function call to null Algorithm !");
            return false;
        }
        if(null == functionToCall)
        {
            ctx.addError(this, "" + algo + " : Function call to null function!");
            return false;
        }
        String searchedFunctionName = functionToCall.getName();
        if(null == searchedFunctionName)
        {
            ctx.addError(this, "" + algo + " : Function call to unknown function!");
            return false;
        }
        if(1 > searchedFunctionName.length())
        {
            ctx.addError(this, "" + algo + " : Function call to unnamed function!");
            return false;
        }        

        String api = functionToCall.getApi();
        if(null != api)
        {
            if(false == algo.hasApi(api))
            {
                log.warn("{} : Function call to wrong API!(API: {})", this, api);
                log.warn("valid APIs : {}",  algo.getApis());
                log.warn("Function called: {}",  functionToCall.getName());
                return false;
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
            return false;
        }

        if(false == ctx.wasSucessful())
        {
            return false;
        }
        return true;
    }

    /** extract the implementation for the requested function from the provided algorithm.
     *
     * @param functionToCall The function that we want to get the implementation of.
     * @param algo The algorithm that defines the function.
     * @return Implementation of function or null if something went wrong.
     */
    private String getImplementationOf(C_FunctionCall functionToCall, AlgorithmInstanceInterface algo)
    {
        if(false == checkImplementationRequest(functionToCall, algo))
        {
            return null;
        }
        Algo_c_code cCode = algo.get_c_code();
        if(null == cCode)
        {
            log.error("The algorithm {} does not have C-code!", algo);
            return null;
        }
        String implementation = cCode.getFunctionImplementation(functionToCall);
        if(null == implementation)
        {
            return null;
        }
        if(false == ctx.wasSucessful())
        {
            return null;
        }

        implementation = replacePlaceholders(implementation,
                functionToCall,
                algo);
        if(null == implementation)
        {
            return null;
        }
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

    /** replaces non code elements with there actual values in this instance.
     *
     * @param implementation source code with non code elements mixed in.
     * @param FunctionParameters parameters of the function call in this implementation. Used to get correct values.
     * @param logic Algorithm that this implementation comes from. Used to get correct values.
     * @return pure C code implementation.
     */
    private String replacePlaceholders(String implementation,
            C_FunctionCall Function,
            AlgorithmInstanceInterface logic)
    {
        int numEuros = 0;
        int numOpenBraces = 0;
        int numClosingBaces = 0;

        if(null == implementation)
        {
            ctx.addError(this,"Implementation is null ! ");
            return null;
        }
        if(null == logic)
        {
            ctx.addError(this,"logic is null ! ");
            return null;
        }

        for(int i = 0; i < implementation.length(); i++)
        {
            switch(implementation.charAt(i))
            {
            case Algo_c_code_impl.IMPLEMENTATION_PLACEHOLDER_CHAR : numEuros++; break;
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
            ctx.addError(this,"Invalid Syntax: odd number of " + Algo_c_code_impl.IMPLEMENTATION_PLACEHOLDER_CHAR + " ! ");
            return null;
        }
        if(numOpenBraces != numClosingBaces)
        {
            ctx.addError(this,"Invalid Syntax: braces don't match"
                    + " (open: " + numOpenBraces + "; close: " +  numClosingBaces + ") !");
            return null;
        }

        if(0 == numOpenBraces)
        {
            return replacePlaceholdersInPart(implementation, Function, logic);
        }
        else
        {
            return handleBracesInImplementation(implementation, Function, logic);
        }
    }

    private String handleBracesInImplementation(String implementation,
            C_FunctionCall Function,
            AlgorithmInstanceInterface algo)
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
                    part = replacePlaceholdersInPart(part, Function, algo);
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
            part = replacePlaceholdersInPart(part, Function, algo);
        }
        else
        {
            // nothing to replace
        }
        return part;
    }

	private String replacePlaceholdersInPart(String implementation,
            C_FunctionCall Function,
            AlgorithmInstanceInterface algo)
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
                    String help = replaceFunctionCallWihtImplementation(parts[i], algo);
                    if(null == help)
                    {
                        return null;
                    }
                    else
                    {
                        res.append(help);
                    }
                }
                else
                {
                    // check function parameters
                    String vari = replaceVariable(parts[i], Function, algo);
                    if(null == vari)
                    {
                        ctx.addError(this,"request was: " + implementation);
                        return null;
                    }
                    else
                    {
                        res.append(vari);
                    }
                }
            }
        }
        return res.toString();
    }

    private String replaceVariable(String iml, C_FunctionCall Function, AlgorithmInstanceInterface algo)
    {
        Algo_c_code cCode = algo.get_c_code();
        if(null == cCode)
        {
            log.error("The algorithm{} does not have C-code!", algo);
            return null;
        }
        String paramValue = cCode.getFunctionParameterValue(iml, Function);
        if(null == paramValue)
        {
            // Not a parameter passed in the function call,
            // but a parameter in the algorithm configuration?
            paramValue = algo.getParameter(iml);
            if(null != paramValue)
            {
                log.trace("Found {} as value for {} in algorithm configuration.", paramValue, iml);
            }
        }

        // check build in
        if(null == paramValue)
        {
            // might be something that puzzler itself can calculate from the Algorithm Instance.
            paramValue = algo.getBuildIn(iml);
            if(null != paramValue)
            {
                log.trace("Found {} as value for {} in build in configuration.", paramValue, iml);
            }
        }

        if(null == paramValue)
        {
            ctx.addError(this,
            "Invalid parameter requested : " + iml );
            ctx.addError(this,"available parameters: " + algo.dumpParameter());
            ctx.addError(this,"available properties: " + algo.dumpProperty());
            return null;
        }
        else
        {
            return paramValue;
        }
    }

    private String replaceFunctionCallWihtImplementation(String functionName, AlgorithmInstanceInterface algo)
    {
        if(false == functionName.contains("("))
        {
            ctx.addError(this,
                "Invalid Function Name (missing open brace?) " + functionName );
            return null;
        }
        else
        {
            // we found a reference to a function name
            log.trace("filling in the code for the function call to {} in {}", functionName, algo);
            C_FunctionCall fc = new C_FunctionCall(functionName);
            String params = fc.getArguments();
            fc.setFunctionArguments(params);
            String apiStr = fc.getApi();
            if(null == apiStr)
            {
                // call to function in this algorithm or additional function of this algorithm
                String impl = getImplementationOf(fc, algo);
                if(null == impl)
                {
                    // not found
                    log.error("call to function {} that is not available in {} !", fc, algo);
                    return null;
                }
                else
                {
                    return impl;
                }
            }
            else
            {
                // call to child apiStr or library function
            	AlgorithmInstanceInterface[] matchingChilds = algo.getChildsWithAPI(apiStr);
            	if(null != matchingChilds)
            	{
	            	if(0 < matchingChilds.length)
	            	{
	                	// child
	            		if(1 < matchingChilds.length)
	            		{
	            			// Wait what? Which shall we call ? Something is wrong here!
	                        log.error("call to API {} that is available in more than one child of {} !", apiStr, algo);
	                        return null;
	            		}
	            		else
	            		{
	    	                String implementation = getImplementationOf(fc, matchingChilds[0]);
	    	                if(null == implementation)
	    	                {
	    	                    String error = "Could not get an Implementation for " + fc.toString() + " from " + matchingChilds[0];
	    	                    log.error(error);
	    	                    ctx.addError(this, error);
	    	                    return null;
	    	                }
	    	                else
	    	                {
	    	                	return implementation;
	    	                }
	            		}
	            	}
	            	// else Library
            	}
            	// else Library
            	Solution s = ctx.getSolution();
            	if(null == s)
            	{
                    log.error("No solution available in this context !");
                    return null;
            	}
            	AlgorithmInstanceInterface otherAlgo = s.getAlgorithm(apiStr);

                String implementation = getImplementationOf(fc, otherAlgo);
                if(null == implementation)
                {
                    String error = "Could not get an Implementation for " + fc.toString();
                    log.error(error);
                    ctx.addError(this, error);
                    return null;
                }
                else
                {
                	return implementation;
                }
            }
        }
    }

    private void addAllAdditionals(AlgorithmInstanceInterface algo)
    {
        log.trace("starting to add addionals:");
        Iterator<AlgorithmInstanceInterface> it = extraAlgoList.iterator();
        while(it.hasNext())
        {
            AlgorithmInstanceInterface logic = it.next();

            Element cCode = logic.getAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
            if(null == cCode)
            {
                ctx.addError(this,
                    "Could not read implementation from " + logic.toString());
            }
            else
            {
                Element additional = cCode.getChild(Algo_c_code_impl.ALGORITHM_ADDITIONAL_CHILD_NAME);
                if(null == additional)
                {
                	// it is OK to have no entries in the additional element
                    log.trace("no addionals for algorithm {}", logic);
                }
                else
                {
                    List<Element> addlist = additional.getChildren();
                    if(null == addlist)
                    {
                        log.trace("empty addionals tag for algorithm {}", logic);
                    }
                    else
                    {
                        // this Algorithm has something -> add it
                        addAllAdditionalsForAlgorithm(logic, addlist, algo);
                    }
                }
            }
        }
        log.trace("added all addionals.");
    }

    private void addAllAdditionalsForAlgorithm(AlgorithmInstanceInterface logic,
            List<Element> addlist,
            AlgorithmInstanceInterface algo)
    {
        log.trace("adding addionals for algorithm {}", logic);
        for(int i = 0; i < addlist.size(); i++)
        {
            Element curElement = addlist.get(i);
            String type = curElement.getName();

            switch(type)
            {
            case Generator.ALGORITHM_ADDITIONAL_INCLUDE_CHILD_NAME:
                String include = curElement.getText();
                log.trace("adding include {}", include);
                sourceFile.addLine(CFile.C_FILE_INCLUDE_SECTION_NAME, include);
                break;

            case Generator.ALGORITHM_FUNCTION_CHILD_NAME:
                Function func = new Function(curElement);
                log.trace("adding function {}", func.getName());
                C_FunctionCall fc = new C_FunctionCall(func.getName());
                fc.setApi(logic.getName());
                String implementation = getImplementationOf(fc, logic);
                if(null == implementation)
                {
                    String error = "Could not get an Implementation for " + func.getName();
                    log.error(error);
                    ctx.addError(this, error);
                    continue;
                }
                else
                {
                    func.setImplementation(implementation);
                }
                sourceFile.addFunction(func);
                break;

            case Generator.ALGORITHM_ADDITIONAL_FILE_CHILD_NAME:
                AbstractFile aFile = FileFactory.getFileFromXml(curElement);
                codeGroup.add(aFile);
                log.trace("adding file {}", aFile.getFileName());
                break;

            case Generator.ALGORITHM_ADDITIONAL_VARIABLE_CHILD_NAME:
                String line = curElement.getText();
                C_FunctionCall dummy = new C_FunctionCall("none()");
                line = replacePlaceholders(line, dummy, algo);
                sourceFile.addLine(CFile.C_FILE_GLOBAL_VAR_SECTION_NAME, line);
                log.trace("adding variable ({})", line);
                break;

            default: // ignore
                log.warn("invalid type '{}' for algorithm '{}' !", type, logic);
                break;
            }
        }
    }

}
