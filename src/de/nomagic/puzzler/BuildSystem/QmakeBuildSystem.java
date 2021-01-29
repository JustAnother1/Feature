package de.nomagic.puzzler.BuildSystem;

import java.io.File;
import java.util.Iterator;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.FileGroup.TextFile;
import de.nomagic.puzzler.configuration.Configuration;

public class QmakeBuildSystem extends BuildSystem
{
    public static final String PROJECT_FILE_COMMENT_SECTION_NAME = "FileHeader";
    public static final String PROJECT_FILE_VARIABLES_SECTION_NAME = "Variables";
    public static final String PROJECT_FILE_FILE_SECTION_NAME = "files";

    public QmakeBuildSystem(Context ctx)
    {
        super(ctx);
    }

    @Override
    public boolean hasTargetFor(String source)
    {
        return false;
    }

    @Override
    public void addTarget(Target aTarget)
    {
    }

    @Override
    public void extendListVariable(String list, String newElement)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void addRequiredVariable(String name)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void addVariable(String variName, String variValue)
    {
        // TODO Auto-generated method stub
    }

    @Override
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
        /*
        if(false == configureBuild(e, requiredEnvironmentVariables))
        {
            ctx.addError(this, "Could not get configuration from environment !");
            return null;
        }
        */
        files.addAll(buildFiles);

        // create the projectFile ( Projectname.pro )

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

        TextFile projectFile;
        projectFile = new TextFile(projectName + ".pro");
        projectFile.separateSectionWithEmptyLine(true);
        projectFile.createSections(new String[]
                { PROJECT_FILE_COMMENT_SECTION_NAME,
                  PROJECT_FILE_VARIABLES_SECTION_NAME,
                  PROJECT_FILE_FILE_SECTION_NAME     });

        // there should be a file comment explaining what this is
        projectFile.addLines(PROJECT_FILE_COMMENT_SECTION_NAME,
                       new String[] {"# automatically created project file",
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
                String fileName = curFile.getFileName();
                if(true == fileName.endsWith(".cpp"))
                {
                    projectFile.addLine(PROJECT_FILE_FILE_SECTION_NAME, "SOURCES += " + fileName);
                }
            }
        }

        // add generic stuff:
        projectFile.addLine(PROJECT_FILE_VARIABLES_SECTION_NAME, "TEMPLATE = app");
        projectFile.addLine(PROJECT_FILE_VARIABLES_SECTION_NAME, "TARGET = " + projectName);
        projectFile.addLine(PROJECT_FILE_VARIABLES_SECTION_NAME, "INCLUDEPATH += .");
        projectFile.addLine(PROJECT_FILE_VARIABLES_SECTION_NAME, "DEFINES += QT_DEPRECATED_WARNINGS");

        files.add(projectFile);
        return files;
    }

}
