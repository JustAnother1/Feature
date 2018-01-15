
package de.nomagic.puzzler.BuildSystem;

import java.util.HashMap;
import java.util.Iterator;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.FileGroup.TextFile;
import de.nomagic.puzzler.configuration.Configuration;

public class MakeBuildSystem extends BuildSystem implements BuildSystemAddApi
{
    public final static String MAKEFILE_FILE_COMMENT_SECTION_NAME        = "FileHeader";
    public final static String MAKEFILE_FILE_VARIABLES_SECTION_NAME        = "Variables";
    public final static String MAKEFILE_FILE_TARGET_SECTION_NAME        = "targets";
    private HashMap<String, Target> targets = new HashMap<String, Target>();
    private HashMap<String, String> requiredEnvironmentVariables = new HashMap<String, String>();
    private HashMap<String, String> listVariables = new HashMap<String, String>();
    private FileGroup buildFiles = new FileGroup();


    public MakeBuildSystem(Context ctx)
    {
        super(ctx);
    }

    public FileGroup createBuildFor(FileGroup files)
    {
        if(null == files)
        {
            return null;
        }

        //create the Makefile
        TextFile makeFile = new TextFile("Makefile");
        makeFile.separateSectionWithEmptyLine(true);
        makeFile.createSections(new String[]
                { MAKEFILE_FILE_COMMENT_SECTION_NAME,
                  MAKEFILE_FILE_VARIABLES_SECTION_NAME,
                  MAKEFILE_FILE_TARGET_SECTION_NAME     });

        // there should be a file comment explaining what this is
        makeFile.addLines(MAKEFILE_FILE_COMMENT_SECTION_NAME,
                       new String[] {"# automatically created makefile",
                                     "# created at: " + Tool.curentDateTime(),
                                     "# created from " + ctx.cfg().getString(Configuration.SOLUTION_FILE_CFG) });

        Iterator<String> fileIt = files.getFileIterator();
        if(null == fileIt)
        {
            ctx.addError(this, "No source files provided !");
            return null;
        }
        if(false == fileIt.hasNext())
        {
            ctx.addError(this, "No source files provided !");
            return null;
        }
        while(fileIt.hasNext())
        {
            AbstractFile curFile = files.getFileWithName(fileIt.next());
            if(null != curFile)
            {
                curFile.addToBuild(this);
            }
        }

        // add generic stuff:
        listVariables.put("project", ctx.cfg().getString(Configuration.PROJECT_FILE_CFG));

        // get hardware configuration
        // add the stuff required by the hardware (targets, variables, files)
        if(false == ctx.getEnvironment().configureBuild(this, requiredEnvironmentVariables))
        {
            ctx.addError(this, "Could not get configuration from environment !");
            return null;
        }

        Iterator<String> itVariables = listVariables.keySet().iterator();
        while(itVariables.hasNext())
        {
            String name = itVariables.next();
            makeFile.addLine(MAKEFILE_FILE_VARIABLES_SECTION_NAME,
                    name + " = " + listVariables.get(name));
        }

        Iterator<String> itTargets = targets.keySet().iterator();
        while(itTargets.hasNext())
        {
            String name = itTargets.next();
            Target t = targets.get(name);
            makeFile.addLine(MAKEFILE_FILE_TARGET_SECTION_NAME,
                    t.getAsMakeFileTarget());
            makeFile.addLine(MAKEFILE_FILE_TARGET_SECTION_NAME,
                    "");
        }
        // add generic stuff:

        // clean:
        Iterator<String> it = targets.keySet().iterator();
        StringBuffer sb = new StringBuffer();
        while(it.hasNext())
        {
            Target curentTarget = targets.get(it.next());
            if(false == curentTarget.isPhony())
            {
                String out =  curentTarget.getOutput();
                if(null != out)
                {
                    out = out.replaceAll("%", "*");
                    sb.append(out + " ");
                }
            }
            // else phony targets do not create files
        }
        makeFile.addLines(MAKEFILE_FILE_TARGET_SECTION_NAME,
                new String[] {"clean:",
                              "\trm " + sb.toString() });

        // PHONY
        it = targets.keySet().iterator();
        sb = new StringBuffer();
        while(it.hasNext())
        {
            Target curentTarget = targets.get(it.next());
            if(true == curentTarget.isPhony())
            {
                String out =  curentTarget.getOutput();
                if(null != out)
                {
                    out = out.replaceAll("%", "*");
                    sb.append(out + " ");
                }
            }
            // else phony targets do not create files
        }
        makeFile.addLines(MAKEFILE_FILE_TARGET_SECTION_NAME,
                new String[] {".PHONY: " + sb.toString() });

        files.add(makeFile);
        files.addAll(buildFiles);
        return files;
    }

    @Override
    public boolean hasTargetFor(String source)
    {
        return targets.containsKey(source);
    }

    public void addTarget(Target aTarget)
    {
        targets.put(aTarget.getSource(), aTarget);
    }

    public void extendListVariable(String list, String newElement)
    {
        newElement = newElement.trim();
        String oldList = listVariables.get(list);
        if(null == oldList)
        {
            // This list did not exist -> create the list
            listVariables.put(list, newElement);
        }
        else
        {
            String newList = oldList + " " + newElement;
            listVariables.put(list, newList);
        }
    }

    public void addVariable(String variName, String variValue)
    {
        listVariables.put(variName, variValue);
    }

    public void addRequiredVariable(String name)
    {
        requiredEnvironmentVariables.put(name, "");
    }

    public void addFile(AbstractFile newFile)
    {
        buildFiles.add(newFile);
    }

}
