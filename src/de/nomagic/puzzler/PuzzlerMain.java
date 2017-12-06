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
/** puzzler "compiles" solutions into binaries.
 *
 */
package de.nomagic.puzzler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import de.nomagic.puzzler.BuildSystem.BuildSystem;
import de.nomagic.puzzler.BuildSystem.MakeBuildSystem;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.Generator.C_CodeGenerator;
import de.nomagic.puzzler.Generator.Generator;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.Solution;

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
        System.out.println("-e /--environment_dirctory : directory with environment configuration.");

        System.out.println("-h / --help                : print this message.");

        System.out.println("-l /--library_dirctory     : directory for library of Algorithms and APIs.");
        System.out.println("                           : This parameter can be specified multiple times.");

        System.out.println("-o /--output_dirctory      : directory for created data.");

        System.out.println("-v                         : verbose output for even more messages use -v -v");

        System.out.println("-w / --work_dirctory       : root directory for file search.");

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
                  "<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>" +
                "</encoder>" +
              "</appender>" +
              "<root level='" + LogLevel + "'>" +
                "<appender-ref ref='STDOUT' />" +
              "</root>" +
            "</configuration>";
            ByteArrayInputStream bin;
            try
            {
                bin = new ByteArrayInputStream(logCfg.getBytes("UTF-8"));
                configurator.doConfigure(bin);
            }
            catch(UnsupportedEncodingException e)
            {
                // A system without UTF-8 ? - No chance to do anything !
                e.printStackTrace();
                System.exit(1);
            }
        }
        catch (JoranException je)
        {
          // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    public boolean parseCommandLineParameters(String[] args)
    {
        boolean found_outputDirectory = false;
        boolean found_libDirectory = false;
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
                    found_libDirectory = true;
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
                    found_outputDirectory = true;
                    log.trace("command Line config: output Directory {}", outputDirectory);
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
                    String ProjectName =  args[i].substring(0, args[i].length() - ".xml".length());
                    cfg.setString(Configuration.PROJECT_FILE_CFG, ProjectName);
                }
                else
                {
                    System.err.println("Invalid Parameter : " + args[i]);
                    return false;
                }
            }
        }
        // check parameters
        if(false == found_outputDirectory)
        {
            System.err.println("ERROR: You need to provide the output directory");
            return false;
        }
        // TODO give them command line parameters
        cfg.setString(Configuration.PROJECT_PATH_CFG, cfg.getString(Configuration.ROOT_PATH_CFG));
        if(false == found_libDirectory)
        {
            cfg.setString(Configuration.LIB_PATH_CFG, cfg.getString(Configuration.ROOT_PATH_CFG) + "lib/");
        }

        this.cfg = cfg;
        return true;
    }

    public void execute()
    {
        if(null == cfg)
        {
            return;
        }
        Context ctx = new Context(cfg);
        // open Project file
        Project pro = new Project(ctx);
        if(false == pro.getFromFiles())
        {
            ctx.close();
            return;
        }

        // Find environment
        Environment e = new Environment(ctx);
        if(false == e.getFromProject(pro))
        {
            ctx.close();
            return;
        }
        ctx.addEnvironment(e);

        // find solution
        Solution s = new Solution(ctx);
        if(false == s.getFromProject(pro))
        {
            ctx.close();
            return;
        }

        // check if solution refers to undefined entities
        // test that all environment References are meet by the environment.
        if(false == s.checkAndTestAgainstEnvironment())
        {
            ctx.close();
            return;
        }
        ctx.addSolution(s);

        // create "code creator" back end (creates the C Source Code)
        Generator gen = new C_CodeGenerator(ctx);
        // give solution to code creator to create code project
        FileGroup files = gen.generateFor();
        if(null == files)
        {
            ctx.close();
            return;
        }
        // check tool chain to create makefile
        BuildSystem make = new MakeBuildSystem(ctx);
        files = make.createBuildFor(files);
        if(null == files)
        {
            ctx.close();
            return;
        }

        if(false ==files.saveToFolder(ctx.cfg().getString(Configuration.OUTPUT_PATH_CFG), ctx))
        {
            ctx.close();
            return;
        }
        // success ?
        successful = ctx.wasSucessful();
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
