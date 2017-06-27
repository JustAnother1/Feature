
package de.nomagic.puzzler.BuildSystem;

import java.util.HashMap;
import java.util.Iterator;

import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.FileGroup.TextFile;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.progress.ProgressReport;

public class MakeBuildSystem extends BuildSystem implements BuildSystemAddApi
{
    public final static String MAKEFILE_FILE_COMMENT_SECTION_NAME        = "FileHeader";
    public final static String MAKEFILE_FILE_VARIABLES_SECTION_NAME        = "Variables";
    public final static String MAKEFILE_FILE_TARGET_SECTION_NAME        = "targets";
    private HashMap<String, Target> targets = new HashMap<String, Target>();
    private HashMap<String, String> requiredEnvironmentVariables = new HashMap<String, String>();
    private HashMap<String, String> listVariables = new HashMap<String, String>();
    private FileGroup buildFiles;


    public MakeBuildSystem(ProgressReport report)
    {
        super(report);
    }

    public FileGroup createBuildFor(FileGroup files, Environment e)
    {
        if(null == cfg)
        {
            report.addError(this, "No Configuration provided !");
            return null;
        }
        buildFiles = new FileGroup();

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
                                     "# created from " + cfg.getString(Configuration.SOLUTION_FILE_CFG) });

        Iterator<String> fileIt = files.getFileIterator();
        if(null == fileIt)
        {
            report.addError(this, "No source files provided !");
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
        listVariables.put("project", cfg.getString(Configuration.PROJECT_FILE_CFG));

        // get hardware configuration
        // add the stuff required by the hardware (targets, variables, files)
        if(false == e.configureBuild(this, requiredEnvironmentVariables))
        {
            report.addError(this, "Could not get configuration from environment !");
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
            String out =  curentTarget.getOutput();
            out = out.replaceAll("%", "*");
            sb.append(out + " ");
        }

        makeFile.addLines(MAKEFILE_FILE_TARGET_SECTION_NAME,
                new String[] {"clean:",
                              "\trm " + sb.toString() });

        buildFiles.add(makeFile);

        files.addAll(buildFiles);
        return files;
    }

    @Override
    public boolean hasTargetFor(String Source)
    {
        return targets.containsKey(Source);
    }

    public void addTarget(Target aTarget)
    {
        targets.put(aTarget.getSource(), aTarget);
    }

    public void extendListVariable(String list, String newElement)
    {
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

    public void addRequiredVariable(String Name)
    {
        requiredEnvironmentVariables.put(Name, "");
    }

    public void addFile(AbstractFile newFile)
    {
        buildFiles.add(newFile);
    }

}
