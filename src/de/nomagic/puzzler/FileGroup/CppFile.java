package de.nomagic.puzzler.FileGroup;

import de.nomagic.puzzler.BuildSystem.BuildSystemApi;
import de.nomagic.puzzler.BuildSystem.Target;

public class CppFile extends CFile
{

    public CppFile(String filename)
    {
        super(filename);
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

}
