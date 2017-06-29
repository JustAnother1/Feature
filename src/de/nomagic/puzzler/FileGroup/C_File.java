package de.nomagic.puzzler.FileGroup;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

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

    protected Vector<String> prepareSectionData(String sectionName, Vector<String> sectionData)
    {
        if(true == C_FILE_INCLUDE_SECTION_NAME.equals(sectionName))
        {
            if(sectionData.isEmpty())
            {
                return sectionData;
            }
            // remove duplicates
            Collections.sort(sectionData);
            Iterator<String> it = sectionData.iterator();
            String first = it.next(); // we just checked that it is not empty, so this should work.
            while(it.hasNext())
            {
                String next = it.next();
                if(first.equals(next))
                {
                    it.remove();
                }
                else
                {
                    first = next;
                }
            }
            Vector<String> res = new Vector<String>();
            // expand to valid include statement
            for(int i = 0; i < sectionData.size(); i++)
            {
                String line = sectionData.get(i);
                line = "#include <" + line + ">";
                res.add(line);
            }
            return res;
        }
        else
        {
            // Nothing to do here
            return sectionData;
        }
    }

}
