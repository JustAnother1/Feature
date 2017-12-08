
package de.nomagic.puzzler.Generator;

import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGetter;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.FileGroup.AbstractFile;
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

    private FileGroup libFiles = null;
    private Element cCode = null;
    private ConditionEvaluator condiEval;

    public C_CodeGenerator(Context ctx)
    {
        super(ctx);
        condiEval = new ConditionEvaluator(ctx);
    }

    public FileGroup generateFor()
    {
        FileGroup codeGroup = new FileGroup();

        // create configured Algorithm Tree
        ConfiguredAlgorithm logic = ConfiguredAlgorithm.getTreeFrom(ctx, null);

        if(null == logic)
        {
            ctx.addError(this, "Failed to build the algorithm tree !");
            return null;
        }

        if(false == logic.hasApi(REQUIRED_ROOT_API))
        {
            log.trace("root: {}", logic);
            ctx.addError(this, "Root element of the solution is not an program entry point !");
            return null;
        }

        codeGroup = getCImplementationFor(logic, codeGroup);

        return codeGroup;
    }

    private FileGroup getCImplementationFor(ConfiguredAlgorithm logic, FileGroup codeGroup)
    {
        // we will need at least one *.c file. So create that now.
        C_File mainC = new C_File("main.c");

        // there should be a file comment explaining what this is
        mainC.addLines(C_File.C_FILE_FILE_COMMENT_SECTION_NAME,
                       new String[] {"/*",
                                     "  automatically created main.c",
                                     "  created at: " + Tool.curentDateTime(),
                                     "  created from " + ctx.cfg().getString(Configuration.SOLUTION_FILE_CFG),
                                     "*/"});

        FileGetter fg = new FileGetter();
        Api api = Api.getFromFile(REQUIRED_ROOT_API, fg, ctx);

        if(false == addCodeToFile(mainC, api, logic))
        {
            return null;
        }

        FileGroup newCodeFiles = getAdditionalFiles();
        if(null != newCodeFiles)
        {
            codeGroup.addAll(newCodeFiles);
        }
        codeGroup.add(mainC);
        return codeGroup;
    }

    private void addCodeFile(AbstractFile additionalFile)  // TODO remove?
    {
        if(null == libFiles)
        {
            libFiles = new FileGroup();
        }
        libFiles.add(additionalFile);
    }

    private FileGroup getAdditionalFiles()
    {
        return libFiles;
    }

    private boolean addCodeToFile(C_File codeFile, Api api, ConfiguredAlgorithm logic)
    {
        // TODO
        // TODO parse provides
        // TODO get Implementation from XML
        Function[] funcs = api.getRequiredFunctions();
        for(int i = 0; i < funcs.length; i++)
        {
            String declaration = funcs[i].getDeclaration();
            declaration = declaration.trim();
            String implementation = getCImplementationOf(funcs[i].getName(), codeFile, logic);
            if(null == implementation)
            {
                return false;
            }
            codeFile.addLine(C_File.C_FILE_LOCAL_FUNCTION_DEFINITION_SECTION_NAME, declaration + ";");
            codeFile.addLine(C_File.C_FILE_FUNCTIONS_SECTION_NAME, declaration + "\n" + implementation);
        }
        return true;
    }

    private String getCImplementationOf(String functionName, C_File codeFile, ConfiguredAlgorithm logic)
    {
        String implementation = getFunctionCcode(functionName, logic);
        if(null == implementation)
        {
            return null;
        }
        implementation = implementation.trim();
        // add additional Stuff
        addAdditionalsTo(codeFile, logic);
        // TODO replace all place holders with configuration values
        // TODO add code from configuredAlgorithms into place holders.
        implementation = replacePlaceholders(implementation, codeFile, logic);
        return implementation;
    }

    private String getFunctionCcode(String functionName, ConfiguredAlgorithm logic)
    {
        if(null == cCode)
        {
            findImplementation(logic);
            if(null == cCode)
            {
                ctx.addError(this, "No implementation available !");
                return null;
            }
        }
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
            ctx.addError(this, "Function call to unknown function!");
            return null;
        }
        if(1 > searchedFunctionName.length())
        {
            ctx.addError(this, "Function call to unnamed function!");
            return null;
        }
        for(int i = 0; i < funcs.size(); i++)
        {
            Element curElement = funcs.get(i);
            String name = curElement.getAttributeValue(ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME);
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
                        ctx.addError(this, "no valid condition found!");
                        return null;
                    }
                    return best.getText();
                }
            }
        }
        // function not found
        ctx.addError(this,
                "Function call to missing function! (" + logic
                        + ", function name : " + functionName + " )");
        return null;
    }

    private void findImplementation(ConfiguredAlgorithm logic)
    {
        cCode = logic.getAlgorithmElement(ALGORITHM_C_CODE_CHILD_NAME);
        if(null == cCode)
        {
            // the algorithm might have the functions wrapped into if condition tags.
            List<Element> conditions = logic.getAlgorithmElements(ALGORITHM_IF_CHILD_NAME);
            if(null == conditions)
            {
                ctx.addError(this, "No Implementation found!");
                return;
            }
            else
            {
                Element best = condiEval.getBest(conditions, logic);  // TODO
                if(null == best)
                {
                    ctx.addError(this,
                            "no valid condition!");
                    ctx.addError(this, logic.toString());
                    ctx.addError(this, logic.dumpParameter());
                    ctx.addError(this, logic.dumpProperty());
                    return;
                }
                cCode = best.getChild(ALGORITHM_C_CODE_CHILD_NAME);
                if(null == cCode)
                {
                    ctx.addError(this,
                            "Valid condition(" + best.toString() + ") did not have an Implementation!");
                    return;
                }
            }
        }
    }

    private String replacePlaceholders(String implementation, C_File codeFile, ConfiguredAlgorithm logic)
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
                // parts[i] is function name of child to call
                Iterator<String> it = logic.getAllAlgorithms();
                if(false == it.hasNext())
                {
                    // We need a child to call the function !
                    ctx.addError(this, "Function call to missing child!");
                    return null;
                }
                while(it.hasNext())
                {
                    String ChildName = it.next();
                    ConfiguredAlgorithm childAlgo = logic.getAlgorithm(ChildName);
                    String impl = getCImplementationOf(parts[i], codeFile, childAlgo);
                    res.append(impl);
                }
            }
        }
        return res.toString();
    }

    private void addAdditionalsTo(C_File codeFile, ConfiguredAlgorithm logic)
    {
        if(null == cCode)
        {
            findImplementation(logic);
            if(null == cCode)
            {
                ctx.addError(this, "No implementation available !");
                return;
            }
        }
        Element additional = cCode.getChild(ALGORITHM_ADDITIONAL_C_CODE_CHILD_NAME);
        if(null == additional)
        {
            log.trace("no addionals for algorithm" + this.toString());
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
;
