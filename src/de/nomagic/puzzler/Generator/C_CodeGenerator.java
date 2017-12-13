
package de.nomagic.puzzler.Generator;

import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGetter;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.FileGroup.C_File;
import de.nomagic.puzzler.FileGroup.FileGroup;
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

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private ConditionEvaluator condiEval;
    private FileGroup libFiles = new FileGroup();  // TODO this is to add files that the algorithm needs additionally to the code.

    // TODO. right now all code is inlined! Add support for functions (called from different places and distribution of code into separate files.

    public C_CodeGenerator(Context ctx)
    {
        super(ctx);
        condiEval = new ConditionEvaluator(ctx);
    }

    public FileGroup generateFor(ConfiguredAlgorithm logic)
    {
        FileGroup codeGroup = new FileGroup();

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
        C_File mainC = createFile("main.c");

        FileGetter fg = new FileGetter();
        Api api = Api.getFromFile(REQUIRED_ROOT_API, fg, ctx);

        C_File imp = getImplementationFor(api, logic);
        if(null == imp)
        {
            return null;
        }
        mainC.addContentsOf(imp);

        codeGroup.addAll(libFiles);
        codeGroup.add(mainC);

        return codeGroup;
    }

    private C_File getImplementationFor(Api api, ConfiguredAlgorithm logic)
    {
        log.trace("getting implementation of the {} from {}", api, logic);
        C_File aFile = new C_File("noname.c");
        Function[] funcs = api.getRequiredFunctions();
        for(int i = 0; i < funcs.length; i++)
        {
            String declaration = funcs[i].getDeclaration();
            declaration = declaration.trim();
            String implementation = getCImplementationOf(funcs[i].getName(), logic);
            if(null == implementation)
            {
                return null;
            }
            aFile.addLine(C_File.C_FILE_LOCAL_FUNCTION_DEFINITION_SECTION_NAME, declaration + ";");
            aFile.addLine(C_File.C_FILE_FUNCTIONS_SECTION_NAME, declaration + "\n" + implementation);
        }
        // add additional Stuff
        aFile.addContentsOf(getAdditionalsFrom(logic));
        return aFile;
    }

    private String getCImplementationOf(String functionName, ConfiguredAlgorithm logic)
    {
        log.trace("getting the c implemention of the function {} from {}", functionName, logic);
        String implementation = getFunctionCcode(functionName, logic);
        log.trace("implementation = {}", implementation);
        if(null == implementation)
        {
            return null;
        }
        implementation = implementation.trim();
        implementation = replacePlaceholders(implementation, logic);
        return implementation;
    }

    private String getFunctionCcode(String functionName, ConfiguredAlgorithm logic)
    {
        Element cCode = findImplementation(logic);
        List<Element> funcs = cCode.getChildren(ALGORITHM_FUNCTION_CHILD_NAME);
        String searchedFunctionName = null;
        if(true == functionName.contains("("))
        {
            searchedFunctionName = functionName.substring(0, functionName.indexOf('('));
        }
        else
        {
            searchedFunctionName = functionName;
        }
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
        log.trace("func.size() : {}", funcs.size());
        for(int i = 0; i < funcs.size(); i++)
        {
            Element curElement = funcs.get(i);
            String name = curElement.getAttributeValue(ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME);
            log.trace("func.name : {}", name);
            if(true == searchedFunctionName.equals(name))
            {
                 List<Element> cond = curElement.getChildren(ALGORITHM_IF_CHILD_NAME);
                if((null == cond) || (true == cond.isEmpty()))
                {
                    // TODO handle parameter in function
                    return curElement.getText();
                }
                else
                {
                    Element best = condiEval.getBest(cond, logic); // TODO
                    if(null == best)
                    {
                        // function not found
                        ctx.addError(this, "" + logic + " : no valid condition found!");
                        return null;
                    }
                    return best.getText();
                }
            }
        }
        // function not found
        ctx.addError(this, "Function call to missing function! (" + logic
                        + ", function name : " + searchedFunctionName + " )");
        return null;
    }

    private Element findImplementation(ConfiguredAlgorithm logic)
    {
        Element cCode = logic.getAlgorithmElement(ALGORITHM_C_CODE_CHILD_NAME);
        if(null == cCode)
        {
            // the algorithm might have the functions wrapped into if condition tags.
            List<Element> conditions = logic.getAlgorithmElements(ALGORITHM_IF_CHILD_NAME);
            if(null == conditions)
            {
                ctx.addError(this, "" + logic + " : No Implementation found!");
                return null;
            }
            else
            {
                Element best = condiEval.getBest(conditions, logic);  // TODO
                if(null == best)
                {
                    ctx.addError(this,
                            "" + logic + " : no valid condition!");
                    ctx.addError(this, logic.toString());
                    ctx.addError(this, logic.dumpParameter());
                    ctx.addError(this, logic.dumpProperty());
                    return null;
                }
                cCode = best.getChild(ALGORITHM_C_CODE_CHILD_NAME);
                if(null == cCode)
                {
                    ctx.addError(this,"" + logic + " : Valid condition(" + best.toString()
                                      + ") did not have an Implementation!");
                    return null;
                }
                return cCode;
            }
        }
        else
        {
            return cCode;
        }
    }

    private String replacePlaceholders(String implementation, ConfiguredAlgorithm logic)
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
                // we found a reference to a function name
                String functionName = parts[i];
                // we now need to make sure that that function exists an can be called.
                // we therefore need to extract the function out of the children of this algorithm
                Iterator<String> it = logic.getAllChildren();
                if(false == it.hasNext())
                {
                    // We need a child to call the function !
                    ctx.addError(this, "" + logic + " : Function call to missing child!");
                    return null;
                }
                while(it.hasNext())
                {
                    String ChildName = it.next();
                    ConfiguredAlgorithm childAlgo = logic.getChild(ChildName);
                    String impl = getCImplementationOf(functionName, childAlgo);
                    if(null == impl)
                    {
                        return null;
                    }
                    res.append(impl);
                }
            }
        }
        return res.toString();
    }

    private C_File getAdditionalsFrom(ConfiguredAlgorithm logic)
    {
        Element cCode = findImplementation(logic);
        Element additional = cCode.getChild(ALGORITHM_ADDITIONAL_C_CODE_CHILD_NAME);
        if(null == additional)
        {
            log.trace("no addionals for algorithm {}", logic);
            return null;
        }
        List<Element> addlist = additional.getChildren();
        if(null == addlist)
        {
            return null;
        }
        C_File codeFile = new C_File("noname.c");
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
        return codeFile;
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
