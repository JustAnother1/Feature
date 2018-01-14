package de.nomagic.puzzler.FileGroup;

import java.util.Vector;

import de.nomagic.puzzler.BuildSystem.BuildSystemAddApi;
import de.nomagic.puzzler.BuildSystem.Target;
import de.nomagic.puzzler.solution.ConfiguredAlgorithm;
import de.nomagic.puzzler.solution.Function;

public class C_File extends TextFile
{
    public final static String C_FILE_FILE_COMMENT_SECTION_NAME              = "FileHeader";
    public final static String C_FILE_INCLUDE_SECTION_NAME                   = "include";
    public final static String C_FILE_TYPE_SECTION_NAME                      = "typedef";
    public final static String C_FILE_GLOBAL_VAR_SECTION_NAME                = "globalVar";
    public final static String C_FILE_LOCAL_VAR_SECTION_NAME                 = "staticVar";
    public final static String C_FILE_LOCAL_FUNCTION_DEFINITION_SECTION_NAME = "staticFunc";
    public final static String C_FILE_FUNCTIONS_SECTION_NAME                 = "publicFunctions";
    public final static String C_FILE_STATIC_FUNCTIONS_SECTION_NAME          = "privateFunctions";

    private ElementHandler includes = new IncludeHandler();
    private ElementHandler functions = new FunctionHandler();

    public C_File(String filename)
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
    public void addContentsOf(C_File otherFile)
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
    public void addToBuild(BuildSystemAddApi BuildSystem)
    {
        if(false == BuildSystem.hasTargetFor("%.c"))
        {
            Target cTarget = new Target("%.c");
            cTarget.setOutput("%.o");
            cTarget.setRule(" $(CC) -c $(CFLAGS) $< -o $@");
            BuildSystem.addRequiredVariable("CC");
            BuildSystem.addRequiredVariable("CFLAGS");
            BuildSystem.addTarget(cTarget);
        }
        BuildSystem.extendListVariable("C_SRC", fileName);
        String objName = fileName.substring(0, fileName.length() - ".c".length()) + ".o";
        BuildSystem.extendListVariable("OBJS", objName);
    }

    public void addLineWithComment(String sectionName, String line, String comment)
    {
        if(true == C_FILE_INCLUDE_SECTION_NAME.equals(sectionName))
        {
            C_include inc = new C_include(line, comment);
            includes.add(inc);
        }
        else
        {
            addLine(sectionName, line + " // " + comment);
        }
    }

    public void addLine(String sectionName, String line)
    {
        if(true == C_FILE_INCLUDE_SECTION_NAME.equals(sectionName))
        {
            C_include inc = new C_include(line, null);
            includes.add(inc);
        }
        else
        {
            super.addLine(sectionName, line);
        }
    }

    protected Vector<String> prepareSectionData(String sectionName, Vector<String> sectionData)
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

    public void addFunction(Function func, ConfiguredAlgorithm logic)
    {
        func.addComment(logic.toString());
        functions.add(func);
    }

    public void addFunction(Function func)
    {
        functions.add(func);
    }

}
