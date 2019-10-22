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
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.FileGroup.TextFile;
import de.nomagic.puzzler.configuration.Configuration;

public class MakeBuildSystem extends BuildSystem implements BuildSystemAddApi
{
    public static final String MAKEFILE_FILE_COMMENT_SECTION_NAME = "FileHeader";
    public static final String MAKEFILE_FILE_VARIABLES_SECTION_NAME = "Variables";
    public static final String MAKEFILE_FILE_TARGET_SECTION_NAME = "targets";
    
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    
    private HashMap<String, Target> targets = new HashMap<String, Target>();
    private HashMap<String, String> requiredEnvironmentVariables = new HashMap<String, String>();
    private HashMap<String, String> listVariables = new HashMap<String, String>();
    private FileGroup buildFiles = new FileGroup();
    private int numDefaultTargets = 0;
    private TextFile makeFile;


    public MakeBuildSystem(Context ctx)
    {
        super(ctx);
    }

    private void addGenericTargets()
    {
        // clean:
        Iterator<String> it = targets.keySet().iterator();
        StringBuilder sb = new StringBuilder();
        while(it.hasNext())
        {
            Target curentTarget = targets.get(it.next());
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
        it = targets.keySet().iterator();
        sb = new StringBuilder();
        sb.append("clean ");
        while(it.hasNext())
        {
            Target curentTarget = targets.get(it.next());
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
        if(false == ctx.getEnvironment().configureBuild(this, requiredEnvironmentVariables))
        {
            ctx.addError(this, "Could not get configuration from environment !");
            return null;
        }        
        files.addAll(buildFiles);
        
        //create the Makefile
        makeFile = new TextFile("Makefile");
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

        String projectName = ctx.cfg().getString(Configuration.PROJECT_FILE_CFG);
        if(null == projectName)
        {
            ctx.addError(this, "No project name provided !");
            return null;
        }
        if(1 > projectName.length())
        {
            ctx.addError(this, "Empty project name provided !");
            return null;
        }
        if(true == projectName.contains(File.separator))
        {
            // remove path
            projectName = projectName.substring(projectName.lastIndexOf(File.separator) + 1);
        }
        listVariables.put("project", projectName);

        Iterator<String> itVariables = listVariables.keySet().iterator();
        while(itVariables.hasNext())
        {
            String name = itVariables.next();
            makeFile.addLine(MAKEFILE_FILE_VARIABLES_SECTION_NAME,
                    name + " = " + listVariables.get(name));
        }

        if(0 == numDefaultTargets)
        {
            log.warn("No target has been defined as beeing default!");
        }
        else if(1 == numDefaultTargets)
        {
            log.trace("Defauklt target has been defined!");
        }
        else if(1 < numDefaultTargets)
        {
            log.warn("More than one default target!");
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

        addGenericTargets();

        files.add(makeFile);
        return files;
    }

    @Override
    public boolean hasTargetFor(String source)
    {
        return targets.containsKey(source);
    }

    public void addTarget(Target aTarget)
    {
        if(true == aTarget.isDefault())
        {
            listVariables.put(".DEFAULT_GOAL", aTarget.getOutput());
            numDefaultTargets++;
        }
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
