
package de.nomagic.puzzler.Generator;

import java.util.Iterator;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.Environment.Environment;
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
    public static final String ALGORITHM_C_CODE_CHILD_NAME = "c_code";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private CFile sourceFile;

    public CCodeGenerator(Context ctx)
    {
        super(ctx);
    }

    @Override
    public String getLanguageName()
    {
        return "C";
    }

    public FileGroup generateFor(AlgorithmInstanceInterface logic)
    {
        if(null == logic)
        {
            ctx.addError(this, "" + logic + " : Failed to build the algorithm tree !");
            return null;
        }

        codeGroup = new FileGroup();
        Environment e = ctx.getEnvironment();
        String rootApi = e.getRootApi();


        if(false == logic.hasApi(rootApi))
        {
            log.trace("root: {}", logic);
            ctx.addError(this, "" + logic + " : Root element of the solution is not an " + rootApi + " !");
            return null;
        }

        log.trace("starting to generate the C implementation for {}", logic);

        Api api = Api.getFromFile(rootApi, ctx);
        if(null == api)
        {
            ctx.addError(this, "" + logic + " : Failed to load the api " + rootApi + " !");
            return null;
        }

        addImplementationForTo(api, logic, "main.c");
        if(false == ctx.wasSucessful())
        {
            return null;
        }

        return codeGroup;
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
            fc.setApi(api.toString());
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
                getAdditionalsFrom(logic);
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

    private String getCImplementationOf(CFunctionCall functionToCall, AlgorithmInstanceInterface logic)
    {
        log.trace("getting the C implemention of the function {} from {}",
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

        String api = functionToCall.getApi();
        if(null != api)
        {
            if(false == logic.hasApi(api))
            {
                log.warn("{} : Function call to wrong API!(API: {})", logic, api);
                log.warn("valid APIs : {}",  logic.getApis());
                log.warn("Function called: {}",  functionToCall.getName());
                return null;
            }
        }
        // else API unknown -> can not check

        if(false == ctx.wasSucessful())
        {
            return null;
        }

        functionToCall.setFunctionArguments(functionToCall.getArguments());

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
                else  if(ALGORITHM_FOR_CHILDS_CHILD_NAME.equals(curE.getName()))
                {
                    String api = curE.getAttributeValue(ALGORITHM_FOR_CHILDS_CHILD_API_ATTRIBUTE);
                    if(null == api)
                    {
                        ctx.addError(this, "" + logic + " : for childs element with missing api attribute !)");
                        return null;
                    }
                    Api theApi = Api.getFromFile(api, ctx);
                    if(null == theApi)
                    {
                        ctx.addError(this, "" + logic + " : for childs element with invalid api attribute ! (" + api + "))");
                        return null;
                    }
                    String FuncToCall = curE.getAttributeValue(ALGORITHM_FOR_CHILDS_CHILD_CALL_ATTRIBUTE);
                    if(null == FuncToCall)
                    {
                        // if the API has only one function then we can call that
                        if(1 == theApi.getNumberOfFunctions())
                        {
                            Function first = theApi.getFunctionIndex(0);
                            FuncToCall = first.getName();
                            log.trace("{} : No function to call specified, but API({}) has only one function, so taking that.", logic, api);
                        }
                        else
                        {
                            ctx.addError(this, "" + logic + " : the API (" + api + ") has more than one Function. You must specify the function to call!)");
                            return null;
                        }
                    }
                    Iterator<String> it = logic.getAllChildren();
                    while(it.hasNext())
                    {
                        String childName = it.next();
                        ConfiguredAlgorithm curChild = logic.getChild(childName);
                        if(true == curChild.hasApi(api))
                        {
                            CFunctionCall fc = new CFunctionCall(FuncToCall);
                            String implementation = getCImplementationOf(fc, curChild);
                            if(null == implementation)
                            {
                                String error = "Could not get an Implementation for " + FuncToCall;
                                log.error(error);
                                ctx.addError(this, error);
                                return null;
                            }
                            else
                            {
                                getAdditionalsFrom(curChild);
                                sb.append(implementation);
                            }
                        }
                        // else don't care for that child
                    }
                }
                else
                {
                    String impl = curE.getText();
                    sb.append(impl);
                    log.warn("Adding non conditional Element data to implementation ! text:  {} element: {}", impl, curE);
                }
            }
            else
            {
                // Not an element, therefore can not have if conditions,
                // therefore we can just extract all the text.
                String impl = curC.getValue();
                impl = impl.trim();
                if(0 < impl.length())
                {
                    sb.append(impl);
                    log.trace("adding non element code to implmentation: {} from {}", impl, curC);
                }
                // else whitespace in between tags
            }
        }
        return (sb.toString()).trim();
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

        // search also additional Functions
        Element additional = cCode.getChild(ALGORITHM_ADDITIONAL_C_CODE_CHILD_NAME);
        if(null != additional)
        {
            List<Element> addlist = additional.getChildren();
            if(null != addlist)
            {
                for(int i = 0; i < addlist.size(); i++)
                {
                    Element curElement = addlist.get(i);
                    String type = curElement.getName();
                    switch(type)
                    {

                    case ALGORITHM_FUNCTION_CHILD_NAME:
                        String name = curElement.getAttributeValue(ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME);
                        log.trace("func.name : {}", name);
                        if(true == searchedFunctionName.equals(name))
                        {
                            // found the correct function
                            return curElement;
                        }
                        break;

                    default: // ignore
                        break;
                    }
                }
            }
        }

        // function not found
        ctx.addError(this, "Function call to missing function! (" + logic
                        + ", function name : " + searchedFunctionName + " )");
        return null;
    }

    protected String fillInFunctionCall(String functionName, AlgorithmInstanceInterface logic)
    {
        log.trace("filling in the code for the function call to {} in {}", functionName, logic);
        // we now need to make sure that that function exists an can be called.
        // we therefore need to extract the function out of the children of this algorithm
        Iterator<String> it = logic.getAllChildren();
        StringBuilder res = new StringBuilder();

        CFunctionCall fc = new CFunctionCall(functionName);
        String params = fc.getArguments();
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
                getAdditionalsFrom(childAlgo);
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
                getAdditionalsFrom(libAlgo);
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

            case ALGORITHM_FUNCTION_CHILD_NAME:
                Function func = new Function(curElement);

                CFunctionCall fc = new CFunctionCall(func.getName());
                String implementation = getCImplementationOf(fc, logic);
                if(null == implementation)
                {
                    String error = "Could not get an Implementation for " + func.getName();
                    log.error(error);
                    ctx.addError(this, error);
                    return;
                }
                else
                {
                    func.setImplementation(implementation);
                }

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
