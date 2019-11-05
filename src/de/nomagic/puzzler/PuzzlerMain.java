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
package de.nomagic.puzzler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import org.jdom2.Element;

import de.nomagic.puzzler.BuildSystem.BuildSystem;
import de.nomagic.puzzler.BuildSystem.MakeBuildSystem;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.Generator.CCodeGenerator;
import de.nomagic.puzzler.Generator.CodeGeneratorFactory;
import de.nomagic.puzzler.Generator.Generator;
import de.nomagic.puzzler.Generator.IDEProjectFileGenerator;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.ConfiguredAlgorithm;
import de.nomagic.puzzler.solution.Solution;
import de.nomagic.puzzler.solution.SolutionImpl;

/** Main function of puzzler.
 *
 * @author Lars P&ouml;tter
 * (<a href=mailto:Lars_Poetter@gmx.de>Lars_Poetter@gmx.de</a>)
 *
 */
public class PuzzlerMain
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private Configuration cfg = null;
    private boolean successful = false;

    public PuzzlerMain()
    {
        // nothing to do here
    }

    public static String getCommitID()
    {
        try
        {
            final InputStream s = PuzzlerMain.class.getResourceAsStream("/commit-id");
            final BufferedReader in = new BufferedReader(new InputStreamReader(s));
            final String commitId = in.readLine();
            final String changes = in.readLine();
            if(null != changes)
            {
                if(0 < changes.length())
                {
                    return commitId + "-(" + changes + ")";
                }
                else
                {
                    return commitId;
                }
            }
            else
            {
                return commitId;
            }
        }
        catch( Exception e )
        {
            return e.toString();
        }
    }

    public void printHelp()
    {
        System.out.println("Feature Puzzler [Parameters] [Project File]");
        System.out.println("Parameters:");
        System.out.println("-D<SettingName>=<Value>    : Set a value to a configuration variable.");
        System.out.println("                           : currently supported:");
        System.out.println("                           : " + CCodeGenerator.CFG_DOC_CODE_SRC + "=true  : code source in code");
        System.out.println("-e /--environment_dirctory : directory with environment configuration.");
        System.out.println("-h / --help                : print this message.");
        System.out.println("-l /--library_dirctory     : directory for library of Algorithms and APIs.");
        System.out.println("                           : This parameter can be specified multiple times.");
        System.out.println("-o /--output_dirctory      : directory for created data.");
        System.out.println("-v                         : verbose output for even more messages use -v -v");
        System.out.println("-w / --work_dirctory       : root directory for file search.");
        System.out.println("-z / --zip_out             : zip created data.");
        System.out.println("<Projectfile>.xml          : define the project to process.");
    }

    private void startLogging(final String[] args)
    {
        int numOfV = 0;
        for(int i = 0; i < args.length; i++)
        {
            if(true == "-v".equals(args[i]))
            {
                numOfV ++;
            }
        }

        // configure Logging
        switch(numOfV)
        {
        case 0: setLogLevel("warn"); break;
        case 1: setLogLevel("debug");break;
        case 2:
        default:
            setLogLevel("trace");
            System.out.println("Build from " + getCommitID());
            break;
        }
    }

    private void setLogLevel(String LogLevel)
    {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try
        {
            final JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            final String logCfg =
            "<configuration>" +
              "<appender name='STDOUT' class='ch.qos.logback.core.ConsoleAppender'>" +
                "<encoder>" +
                  "<pattern>%-5level [%logger{36}] %msg%n</pattern>" +
                "</encoder>" +
              "</appender>" +
              "<root level='" + LogLevel + "'>" +
                "<appender-ref ref='STDOUT' />" +
              "</root>" +
            "</configuration>";
            ByteArrayInputStream bin;
            bin = new ByteArrayInputStream(logCfg.getBytes(StandardCharsets.UTF_8));
            configurator.doConfigure(bin);
        }
        catch (JoranException je)
        {
          // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    public boolean parseCommandLineParameters(String[] args)
    {
        boolean foundOutputDirectory = false;
        boolean foundLibDirectory = false;
        Configuration cfg = new Configuration();
        for(int i = 0; i < args.length; i++)
        {
            if(true == args[i].startsWith("-"))
            {
                if( (true == "-h".equals(args[i])) || (true == "--help".equals(args[i])))
                {
                    // help
                    printHelp();
                    return false;
                }
                else if(true == "-v".equals(args[i]))
                {
                    // verbose output
                    // already handled -> ignore
                }
                else if( (true == "-w".equals(args[i])) || (true == "--work_dirctory".equals(args[i])))
                {
                    // working directory
                    i++;
                    String workDirectory = Tool.validatePath(args[i]);
                    if(1 > workDirectory.length())
                    {
                        System.err.println("Invalid Parameter : " + args[i]);
                        return false;
                    }
                    cfg.setString(Configuration.ROOT_PATH_CFG, workDirectory);
                    log.trace("command Line config: work Directory {}", workDirectory);
                }
                else if( (true == "-l".equals(args[i])) || (true == "--library_dirctory".equals(args[i])))
                {
                    // Library directory
                    i++;
                    String libDir = Tool.validatePath(args[i]);
                    if(1 > libDir.length())
                    {
                        System.err.println("Invalid Parameter : " + args[i]);
                        return false;
                    }
                    cfg.setString(Configuration.LIB_PATH_CFG, libDir);
                    foundLibDirectory = true;
                    log.trace("command Line config: library Directory {}", libDir);
                }
                else if( (true == "-o".equals(args[i])) || (true == "--output_dirctory".equals(args[i])))
                {
                    // output directory
                    i++;
                    String outputDirectory = Tool.validatePath(args[i]);
                    if(1 > outputDirectory.length())
                    {
                        System.err.println("Invalid Parameter : " + args[i]);
                        return false;
                    }
                    cfg.setString(Configuration.OUTPUT_PATH_CFG, outputDirectory);
                    foundOutputDirectory = true;
                    log.trace("command Line config: output Directory {}", outputDirectory);
                }
                else if( (true == "-z".equals(args[i])) || (true == "--zip_out".equals(args[i])))
                {
                    // zip output
                    i++;
                    String outputDirectory = Tool.validatePath(args[i]);
                    if(1 > outputDirectory.length())
                    {
                        System.err.println("Invalid Parameter : " + args[i]);
                        return false;
                    }
                    cfg.setString(Configuration.OUTPUT_PATH_CFG, outputDirectory);
                    cfg.setBool(Configuration.ZIP_OUTPUT, true);
                    foundOutputDirectory = true;
                    log.trace("command Line config: zip output");
                    log.trace("command Line config: output zip file name {}", outputDirectory);
                }
                else if( (true == "-e".equals(args[i])) || (true == "--environment_dirctory".equals(args[i])))
                {
                    // environment directory
                    i++;
                    String envDir = Tool.validatePath(args[i]);
                    if(1 > envDir.length())
                    {
                        System.err.println("Invalid Parameter : " + args[i]);
                        return false;
                    }
                    cfg.setString(Configuration.ENVIRONMENT_PATH_CFG, envDir);
                    log.trace("command Line config: environment Directory {}", envDir);
                }
                else if(true == args[i].startsWith("-D"))
                {
                    // Some configuration variable
                    int idx = args[i].indexOf('=');
                    if(-1 == idx)
                    {
                        System.err.println("Invalid Parameter(-D needs '=') : " + args[i]);
                        return false;
                    }
                    String settingName = args[i].substring(2, idx); // skip the "-D"
                    String settingValue = args[i].substring(idx + 1);
                    settingName = settingName.trim();
                    settingValue = settingValue.trim();

                    cfg.setString(settingName, settingValue);
                    log.trace("command Line config: {} = {}", settingName, settingValue);
                }
                else
                {
                    System.err.println("Invalid Parameter : " + args[i]);
                    return false;
                }
            }
            else
            {
                if(true == args[i].endsWith(".xml"))
                {
                    String projectName =  args[i].substring(0, args[i].length() - ".xml".length());
                    cfg.setString(Configuration.PROJECT_FILE_CFG, projectName);
                }
                else
                {
                    System.err.println("Invalid Parameter : " + args[i]);
                    return false;
                }
            }
        }
        // check parameters
        if(false == foundOutputDirectory)
        {
            System.err.println("ERROR: You need to provide the output directory or zip file name");
            return false;
        }

        cfg.setString(Configuration.PROJECT_PATH_CFG, cfg.getString(Configuration.ROOT_PATH_CFG));
        if(false == foundLibDirectory)
        {
            cfg.setString(Configuration.LIB_PATH_CFG, cfg.getString(Configuration.ROOT_PATH_CFG) + "lib/");
        }

        this.cfg = cfg;
        return true;
    }

    private Project openProject(Context ctx)
    {
        Element proElement = ctx.getElementfrom(
                ctx.cfg().getString(Configuration.PROJECT_FILE_CFG) + ".xml",
                ctx.cfg().getString(Configuration.PROJECT_PATH_CFG),
                Project.PROJECT_ROOT_ELEMENT_NAME);
        Project pro = new ProjectImpl(ctx);
        if(false == pro.loadFromElement(proElement))
        {
            return null;
        }
        else
        {
            return pro;
        }
    }

    private Environment openEnvironment(Context ctx, Element envElement)
    {
        Environment e = new Environment(ctx);
        Element envEleemnt = ctx.loadElementFrom(envElement,
                ctx.cfg().getString(Configuration.ENVIRONMENT_PATH_CFG),
                Environment.ROOT_ELEMENT_NAME);
        if(false == e.loadFromElement(envEleemnt))
        {
            return null;
        }
        else
        {
            return e;
        }
    }

    private Solution openSolution(Context ctx, Project pro)
    {
        Solution s = new SolutionImpl(ctx);
        if(false == s.getFromProject(pro))
        {
            return null;
        }

        // check if solution refers to undefined entities
        // test that all environment References are meet by the environment.
        if(false == s.checkAndTestAgainstEnvironment())
        {
            log.error("Solution does not match the environment!");
            return null;
        }
        return s;
    }

    private FileGroup createRessourcesFromSolution(Context ctx)
    {
        // create "code creator" back end (creates the Source Code in C or other languages)
        ConfiguredAlgorithm algoTree = ConfiguredAlgorithm.getTreeFrom(ctx, null);
        Generator[] gen = CodeGeneratorFactory.getGeneratorFor(algoTree, ctx);
        if(null == gen)
        {
            log.error("Could not create code generators !");
            ctx.close();
            return null;
        }
        if(1 > gen.length)
        {
            log.error("Could not get the needed code generator !");
            ctx.close();
            return null;
        }
        FileGroup allFiles = new FileGroup();
        for(int i = 0; i < gen.length; i++)
        {
            Generator curGen = gen[i];
            curGen.configure(cfg);
            // give solution to code creator to create code project
            FileGroup files = curGen.generateFor(algoTree);
            if(null == files)
            {
                log.error("Failed to generate {} source code!", curGen.getLanguageName());
                ctx.close();
                return null;
            }
            allFiles.addAll(files);
        }
        return allFiles;
    }

    private boolean createOutput(Context ctx, FileGroup allFiles)
    {
        if(true == ctx.cfg().getBool(Configuration.ZIP_OUTPUT))
        {
            if(false ==allFiles.saveToZip(ctx.cfg().getString(Configuration.OUTPUT_PATH_CFG), ctx))
            {
                log.error("Failed to create the zip file!");
                return false;
            }
        }
        else
        {
            if(false ==allFiles.saveToFolder(ctx.cfg().getString(Configuration.OUTPUT_PATH_CFG), ctx))
            {
                log.error("Failed to save the generated files!");
                return false;
            }
        }
        return true;
    }

    public void execute()
    {
        if(null == cfg)
        {
            return;
        }
        Context ctx = new ContextImpl(cfg);

        // open Project file
        Project pro = openProject(ctx);
        if(null == pro)
        {
            log.error("Failed to open project!");
            ctx.close();
            return;
        }

        // Find environment
        Environment e = openEnvironment(ctx, pro.getEnvironmentElement());
        if(null == e)
        {
            log.error("Failed to open environment!");
            ctx.close();
            return;
        }
        ctx.addEnvironment(e);

        // find solution
        Solution s = openSolution(ctx, pro);
        if(null == s)
        {
            log.error("Failed to open solution!");
            ctx.close();
            return;
        }
        ctx.addSolution(s);

        FileGroup allFiles = createRessourcesFromSolution(ctx);
        if(null == allFiles)
        {
            log.error("Could not create ressources !");
            ctx.close();
            return;
        }

        // check tool chain to create makefile
        BuildSystem make = new MakeBuildSystem(ctx);
        allFiles = make.createBuildFor(allFiles);
        if(null == allFiles)
        {
            log.error("Failed to generate build environment!");
            ctx.close();
            return;
        }

        // IDE Project Files
        allFiles = IDEProjectFileGenerator.generateFileInto(ctx, allFiles);
        if(null == allFiles)
        {
            log.error("Failed to generate IDE project files!");
            ctx.close();
            return;
        }

        if(false == createOutput(ctx, allFiles))
        {
            log.error("Failed to create the output!");
            ctx.close();
            return;
        }

        // success ?
        successful = ctx.wasSucessful();
        log.trace("successful = {}", successful);
        System.out.println(ctx.getErrors());
        ctx.close();
    }

    public static void main(String[] args)
    {
        PuzzlerMain m = new PuzzlerMain();
        m.startLogging(args);
        if(true == m.parseCommandLineParameters(args))
        {
            m.execute();
            if(true == m.successful)
            {
                // OK
                System.out.println("Project created successfully!");
                System.exit(0);
            }
            else
            {
                // ERROR
                System.out.println("ERROR: Something went wrong!");
                System.exit(1);
            }
        }
        else
        {
            System.exit(1);
        }
    }

}
