package de.nomagic.puzzler.FileGroup;

import de.nomagic.puzzler.BuildSystem.BuildSystemAddApi;
import de.nomagic.puzzler.BuildSystem.Target;

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
    public void addToBuild(BuildSystemAddApi BuildSystem)
    {
        if(false == BuildSystem.hasTargetFor("%.c"))
        {
            Target cTarget = new Target("%c");
            cTarget.setOutput("%o");
            cTarget.setRule(" $(CC) -c $(CFLAGS) $< -o $@");
            BuildSystem.addRequiredVariable("CC");
            BuildSystem.addRequiredVariable("CFLAGS");
            BuildSystem.addTarget(cTarget);
        }
        BuildSystem.extendListVariable("C_SRC", fileName);
        String objName = fileName.substring(0, fileName.length() - ".c".length()) + ".o";
        BuildSystem.extendListVariable("OBJS", objName);
    }

}
