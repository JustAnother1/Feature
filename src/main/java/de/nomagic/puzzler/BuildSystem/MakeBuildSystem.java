/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>
 *
 */
package de.nomagic.puzzler.BuildSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.CFile;
import de.nomagic.puzzler.FileGroup.CppFile;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.FileGroup.TextFile;
import de.nomagic.puzzler.configuration.Configuration;

public class MakeBuildSystem extends BuildSystem
{
    public static final String MAKEFILE_FILE_COMMENT_SECTION_NAME = "FileHeader";
    public static final String MAKEFILE_FILE_VARIABLES_SECTION_NAME = "Variables";
    public static final String MAKEFILE_FILE_TARGET_SECTION_NAME = "targets";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private ArrayList<Target> targets = new ArrayList<Target>();
    private HashMap<String, String> listVariables = new HashMap<String, String>();
    private int numDefaultTargets = 0;
    private TextFile makeFile;


    public MakeBuildSystem(Context ctx)
    {
        super(ctx);
        addVariable("SOURCE_DIR", "$(dir $(lastword $(MAKEFILE_LIST)))");
        addVariable("VPATH", "$(SOURCE_DIR)");
    }

    private void addGenericTargets()
    {
        // clean:
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < targets.size(); i++)
        {
            Target curentTarget = targets.get(i);
            if(false == curentTarget.isPhony())
            {
                String out =  curentTarget.getOutput();
                if(null != out)
                {
                    out = out.replace("%", "*");
                    sb.append(out + " ");
                }
            }
            // else phony targets do not create files
        }
        makeFile.addLines(MAKEFILE_FILE_TARGET_SECTION_NAME,
                new String[] {"clean:",
                              "\trm -rf " + sb.toString().trim(),
                              "" });

        // PHONY
        sb = new StringBuilder();
        sb.append("clean ");
        for(int i = 0; i < targets.size(); i++)
        {
            Target curentTarget = targets.get(i);
            if(true == curentTarget.isPhony())
            {
                String out =  curentTarget.getOutput();
                if(null != out)
                {
                    out = out.replace("%", "*");
                    sb.append(out + " ");
                }
            }
            // else phony targets do not create files
        }
        makeFile.addLines(MAKEFILE_FILE_TARGET_SECTION_NAME,
                new String[] {".PHONY: " + sb.toString().trim() });

    }

    private void createMakeFile()
    {
        //create the Makefile
        makeFile = new TextFile("Makefile");
        makeFile.separateSectionWithEmptyLine(true);
        makeFile.createSections(new String[]
                { MAKEFILE_FILE_COMMENT_SECTION_NAME,
                  MAKEFILE_FILE_VARIABLES_SECTION_NAME,
                  MAKEFILE_FILE_TARGET_SECTION_NAME     });

        // there should be a file comment explaining what this is
        makeFile.addLines(MAKEFILE_FILE_COMMENT_SECTION_NAME,
                       new String[] {"# automatically created Makefile",
                                     "# created at: " + Tool.curentDateTime(),
                                     "# created from " + ctx.cfg().getString(Configuration.SOLUTION_FILE_CFG) });
    }

    private FileGroup updateFromProjectFiles(FileGroup files)
    {
        Iterator<String> fileIt = files.getFileIterator();
        if(false == fileIt.hasNext())
        {
            ctx.addError(this, "No source files provided !");
            return null;
        }
        while(fileIt.hasNext())
        {
            AbstractFile curFile = files.getFileWithName(fileIt.next());

            if(true == curFile instanceof CFile)
            {
                if(false == hasTargetFor("%.c"))
                {
                    Target cTarget = new Target("%.c");
                    cTarget.setOutput("%.o");
                    cTarget.setRule(" $(CC) -c $(CFLAGS) $< -o $@");
                    addRequiredVariable("CC");
                    addRequiredVariable("CFLAGS");
                    addTarget(cTarget);
                }
                String fileName = curFile.getFileName();
                extendListVariable("C_SRC", fileName);
                String objName = fileName.substring(0, fileName.length() - ".c".length()) + ".o";
                extendListVariable("OBJS", objName);
            }

            if(true == curFile instanceof CppFile)
            {
                String fileName = curFile.getFileName();

                String cppExtension = fileName.substring(fileName.lastIndexOf('.'));

                if(false == hasTargetFor("%" + cppExtension))
                {
                    Target cTarget = new Target("%" + cppExtension);
                    cTarget.setOutput("%.o");
                    cTarget.setRule(" $(CC) -c $(CPPFLAGS) $< -o $@");
                    addRequiredVariable("CC");
                    addRequiredVariable("CPPFLAGS");
                    addTarget(cTarget);
                }

                extendListVariable("CPP_SRC", fileName);
                String objName = fileName.substring(0, fileName.length() - cppExtension.length()) + ".o";
                extendListVariable("OBJS", objName);
            }
        }
        return files;
    }

    private boolean handleProjectName()
    {
        String projectName = ctx.cfg().getString(Configuration.PROJECT_FILE_CFG);
        if(null == projectName)
        {
            ctx.addError(this, "No project name provided !");
            return false;
        }
        if(1 > projectName.length())
        {
            ctx.addError(this, "Empty project name provided !");
            return false;
        }
        if(true == projectName.contains(File.separator))
        {
            // remove path
            projectName = projectName.substring(projectName.lastIndexOf(File.separator) + 1);
        }
        listVariables.put("project", projectName);
        return true;
    }

    private void handleVariables()
    {
        Iterator<String> itVariables = listVariables.keySet().iterator();
        while(itVariables.hasNext())
        {
            String name = itVariables.next();
            makeFile.addLine(MAKEFILE_FILE_VARIABLES_SECTION_NAME,
                    name + " = " + listVariables.get(name));
        }

    }

    private void handleTargets()
    {
        if(0 == numDefaultTargets)
        {
            log.warn("No target has been defined as beeing default!");
        }
        else if(1 == numDefaultTargets)
        {
            log.trace("Default target has been defined!");
        }
        else if(1 < numDefaultTargets)
        {
            log.warn("More than one default target!");
        }

        for(int i = 0; i < targets.size(); i++)
        {
            Target t = targets.get(i);
            makeFile.addLine(MAKEFILE_FILE_TARGET_SECTION_NAME,
                    t.getAsMakeFileTarget());
            makeFile.addLine(MAKEFILE_FILE_TARGET_SECTION_NAME,
                    "");
        }

        addGenericTargets();
    }

    public FileGroup createBuildFor(FileGroup files)
    {
        if(null == files)
        {
            return null;
        }
        // get hardware configuration
        // add the stuff required by the hardware (targets, variables, files)
        Environment e = ctx.getEnvironment();
        if(null == e)
        {
            ctx.addError(this, "No Environment available !");
            return null;
        }
        if(false == configureBuild(e, requiredEnvironmentVariables))
        {
            ctx.addError(this, "Could not get configuration from environment !");
            return null;
        }

        log.trace("adding {} files.", buildFiles.numEntries());
        files.addAll(buildFiles);

        createMakeFile();

        files = updateFromProjectFiles(files);
        if(null == files)
        {
            return null;
        }

        // add generic stuff:

        if(false == handleProjectName())
        {
            return null;
        }

        handleVariables();
        handleTargets();

        files.add(makeFile);
        return files;
    }

    @Override
    public boolean hasTargetFor(String source)
    {
        if(null == source)
        {
            return false;
        }
        for(int i = 0; i < targets.size(); i++)
        {
            Target t = targets.get(i);
            if(source.equals(t.getSource()))
            {
                return true;
            }
        }
        return false;
    }

    public void addTarget(Target aTarget)
    {
        if(true == aTarget.isDefault())
        {
            listVariables.put(".DEFAULT_GOAL", aTarget.getOutput());
            numDefaultTargets++;
        }
        targets.add(aTarget);
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

}
