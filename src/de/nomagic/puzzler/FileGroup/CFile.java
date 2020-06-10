package de.nomagic.puzzler.FileGroup;

import java.util.List;

import de.nomagic.puzzler.BuildSystem.BuildSystemApi;
import de.nomagic.puzzler.BuildSystem.Target;
import de.nomagic.puzzler.solution.Function;

public class CFile extends TextFile
{
    public static final String C_FILE_FILE_COMMENT_SECTION_NAME              = "FileHeader";
    public static final String C_FILE_INCLUDE_SECTION_NAME                   = "include";
    public static final String C_FILE_TYPE_SECTION_NAME                      = "typedef";
    public static final String C_FILE_GLOBAL_VAR_SECTION_NAME                = "globalVar";
    public static final String C_FILE_LOCAL_VAR_SECTION_NAME                 = "staticVar";
    public static final String C_FILE_LOCAL_FUNCTION_DEFINITION_SECTION_NAME = "staticFunc";
    public static final String C_FILE_FUNCTIONS_SECTION_NAME                 = "publicFunctions";
    public static final String C_FILE_STATIC_FUNCTIONS_SECTION_NAME          = "privateFunctions";

    private ElementHandler includes = new IncludeHandler();
    private ElementHandler functions = new FunctionHandler();

    public CFile(String filename)
    {
        super(filename);
        createSections(new String[] {C_FILE_FILE_COMMENT_SECTION_NAME,
                                     C_FILE_INCLUDE_SECTION_NAME,
                                     C_FILE_TYPE_SECTION_NAME,
                                     C_FILE_GLOBAL_VAR_SECTION_NAME,
                                     C_FILE_LOCAL_VAR_SECTION_NAME,
                                     C_FILE_LOCAL_FUNCTION_DEFINITION_SECTION_NAME,
                                     C_FILE_FUNCTIONS_SECTION_NAME,
                                     C_FILE_STATIC_FUNCTIONS_SECTION_NAME});
        separateSectionWithEmptyLine(true);
    }
    @Override
    public void addContentsOf(CFile otherFile)
    {
        if(null == otherFile)
        {
            return;
        }
        includes.addAll(otherFile.includes);
        functions.addAll(otherFile.functions);
        super.addContentsOf(otherFile);
    }

    @Override
    public void addToBuild(BuildSystemApi buildSystem)
    {
        if(false == buildSystem.hasTargetFor("%.c"))
        {
            Target cTarget = new Target("%.c");
            cTarget.setOutput("%.o");
            cTarget.setRule(" $(CC) -c $(CFLAGS) $< -o $@");
            buildSystem.addRequiredVariable("CC");
            buildSystem.addRequiredVariable("CFLAGS");
            buildSystem.addTarget(cTarget);
        }
        buildSystem.extendListVariable("C_SRC", fileName);
        String objName = fileName.substring(0, fileName.length() - ".c".length()) + ".o";
        buildSystem.extendListVariable("OBJS", objName);
    }

    public void addLineWithComment(String sectionName, String line, String comment)
    {
        if(true == C_FILE_INCLUDE_SECTION_NAME.equals(sectionName))
        {
            CInclude inc = new CInclude(line, comment);
            includes.add(inc);
        }
        else
        {
            addLine(sectionName, line + " // " + comment);
        }
    }

    @Override
    public void addLine(String sectionName, String line)
    {
        if(true == C_FILE_INCLUDE_SECTION_NAME.equals(sectionName))
        {
            CInclude inc = new CInclude(line, null);
            includes.add(inc);
        }
        else
        {
            super.addLine(sectionName, line);
        }
    }

    @Override
    protected List<String> prepareSectionData(String sectionName, List<String> sectionData)
    {
        if(true == C_FILE_INCLUDE_SECTION_NAME.equals(sectionName))
        {
            return includes.getCode(0, getLineSperator());
        }
        else if(true == C_FILE_LOCAL_FUNCTION_DEFINITION_SECTION_NAME.equals(sectionName))
        {
            return functions.getCode(FunctionHandler.TYPE_DECLARATION, getLineSperator());
        }
        else if(true == C_FILE_FUNCTIONS_SECTION_NAME.equals(sectionName))
        {
            return functions.getCode(FunctionHandler.TYPE_IMPLEMENTATION, getLineSperator());
        }
        else
        {
            // Nothing to do here
            return sectionData;
        }

    }

    public void addFunction(Function func, String description)
    {
        func.addComment(description);
        functions.add(func);
    }

    public void addFunction(Function func)
    {
        functions.add(func);
    }

}
