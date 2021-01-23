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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import org.jdom2.Element;

import de.nomagic.puzzler.BuildSystem.BuildSystemApi;
import de.nomagic.puzzler.BuildSystem.BuildSystemFactory;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.Generator.CodeGeneratorFactory;
import de.nomagic.puzzler.Generator.Generator;
import de.nomagic.puzzler.Generator.IDEProjectFileGenerator;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.ConfiguredAlgorithm;
import de.nomagic.puzzler.solution.Solution;
import de.nomagic.puzzler.solution.SolutionImpl;
import de.nomagic.puzzler.xmlrpc.XmlRpcGetter;

/** Main function of puzzler.
 */
public class PuzzlerMain
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private Configuration cfg = null;
    private boolean successful = false;
    private boolean foundOutputDirectory = false;
    private boolean foundLibDirectory = false;
    private boolean useXCmlRpc = false;

    public PuzzlerMain()
    {
        // nothing to do here
    }

    public void printHelp()
    {
        System.out.println("Feature Puzzler [Parameters] [Project File]");
        System.out.println("Parameters:");
        System.out.println("-D<SettingName>=<Value>    : Set a value to a configuration variable.");
        System.out.println("-list_conf_variables       : list all currently supported configuration variables.");
        System.out.println("-e <path> /--environment_directory <path>");
        System.out.println("                           : directory with environment configuration.");
        System.out.println("-h / --help                : print this message.");
        System.out.println("-l <path> /--library_directory <path>");
        System.out.println("                           : directory for library of Algorithms and APIs.");
        System.out.println("                           : This parameter can be specified multiple times.");
        System.out.println("-o <path> /--output_directory <path>");
        System.out.println("                           : directory for created data.");
        System.out.println("-v                         : verbose output for even more messages use -v -v");
        System.out.println("-p <path> / --project_directory <path>");
        System.out.println("                           : directory that contains the project file.");
        System.out.println("-s <path> / --solution_directory <path>");
        System.out.println("                           : directory that contains the solution.");
        System.out.println("-x <URL>                   : read from XML-RPC source at URL.");
        System.out.println("-z <filename> / --zip <filename>");
        System.out.println("                           : zip created data (ignores output folder setting).");
        System.out.println("--zip_to_stdout            : zip created data and write zip file to stdout.");
        System.out.println("--prj_name <name>          : project name to use when zip to stdout.");
        System.out.println("<Projectfile>.xml          : define the project to process.");
        System.out.println("                           : if missing the project is read from stdin.");
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
            System.err.println("Build from " + Tool.getCommitID());
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
              "<appender name='STDERR' class='ch.qos.logback.core.ConsoleAppender'>" +
              "<target>System.err</target>" +
                "<encoder>" +
                  "<pattern>%-5level [%logger{36}] %msg%n</pattern>" +
                "</encoder>" +
              "</appender>" +
              "<root level='" + LogLevel + "'>" +
                "<appender-ref ref='STDERR' />" +
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


    private boolean cmdln_xmlRpcUrl(String value)
    {
        if(1 > value.length())
        {
            System.err.println("Invalid XML-RPC URL !");
            return false;
        }
        cfg.setString(Configuration.XML_RPC_URL, value);
        log.trace("command Line config: xml-rpc url {}", value);
        return true;
    }

    private boolean cmdln_handleDirectory(String directoryValue, String CfgSetting)
    {
        String directory = Tool.validatePath(directoryValue);
        if(1 > directory.length())
        {
            System.err.println("Invalid Parameter : " + directoryValue);
            return false;
        }
        cfg.setString(CfgSetting, directory);
        log.trace("command Line config: {} {}", CfgSetting, directory);
        return true;
    }

    public boolean parseCommandLineParameters(String[] args)
    {
        cfg = new Configuration();
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
                else if(true == "-x".equals(args[i]))
                {
                    useXCmlRpc = true;
                    i++;
                    if(false == cmdln_xmlRpcUrl(args[i]))
                    {
                        return false;
                    }
                }
                else if( (true == "-p".equals(args[i])) || (true == "--project_directory".equals(args[i])))
                {
                    // project directory
                    i++;
                    if(false == cmdln_handleDirectory(args[i], Configuration.PROJECT_PATH_CFG))
                    {
                        return false;
                    }
                }
                else if( (true == "-s".equals(args[i])) || (true == "--solution_directory".equals(args[i])))
                {
                    // solution directory
                    i++;
                    if(false == cmdln_handleDirectory(args[i], Configuration.SOLUTION_PATH_CFG))
                    {
                        return false;
                    }
                }
                else if( (true == "-l".equals(args[i])) || (true == "--library_directory".equals(args[i])))
                {
                    // Library directory
                    i++;
                    if(false == cmdln_handleDirectory(args[i], Configuration.LIB_PATH_CFG))
                    {
                        return false;
                    }
                }
                else if( (true == "-o".equals(args[i])) || (true == "--output_directory".equals(args[i])))
                {
                    // output directory
                    i++;
                    if(false == cmdln_handleDirectory(args[i], Configuration.OUTPUT_PATH_CFG))
                    {
                        return false;
                    }
                }
                else if( (true == "-z".equals(args[i])) || (true == "--zip".equals(args[i])))
                {
                    // zip output
                    i++;
                    if(false == cmdln_handleDirectory(args[i], Configuration.ZIP_OUTPUT))
                    {
                        return false;
                    }
                }
                else if (true == "--zip_to_stdout".equals(args[i]))
                {
                    // zip output to stdout
                    cfg.setBool(Configuration.ZIP_OUTPUT_TO_STDOUT, true);
                    foundOutputDirectory = true;
                    log.trace("command Line config: zip output to stdout");
                }
                else if (true == "--prj_name".equals(args[i]))
                {
                    // project name
                    i++;
                    cfg.setString(Configuration.PROJECT_NAME_CFG, args[i]);
                }
                else if( (true == "-e".equals(args[i])) || (true == "--environment_dirctory".equals(args[i])))
                {
                    // environment directory
                    i++;
                    if(false == cmdln_handleDirectory(args[i], Configuration.ENVIRONMENT_PATH_CFG))
                    {
                        return false;
                    }
                }
                else if (true == "-list_conf_variables".equals(args[i]))
                {
                    // list currently supported configuration variables
                    System.out.println("currently supported configuration variables:");
                    System.out.println(Configuration.listAllConfigurationVariables());
                    return false;
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

        if(false == foundLibDirectory)
        {
            cfg.setString(Configuration.LIB_PATH_CFG, cfg.getString(Configuration.PROJECT_PATH_CFG) + "lib/");
        }

        return true;
    }

    private String getInputFromInputStream()
    {
        StringBuilder sb = new StringBuilder();
        InputStream in = System.in;
        Reader inputStreamReader = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(inputStreamReader);
        for(;;)
        {
            String line;
            try {
                line = br.readLine();
                if(false == line.startsWith("<<<EOF>>>"))
                {
                    sb.append(line);
                    sb.append("\n");
                }
                else
                {
                    break;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                break;
            }
        }
        return sb.toString();
    }

    private Project openProject(Context ctx)
    {
        Project pro = new ProjectImpl(ctx);
        String projectFile = ctx.cfg().getString(Configuration.PROJECT_FILE_CFG);
        Element proElement = null;
        if(0 < projectFile.length())
        {
            proElement = ctx.getElementfrom(
                            projectFile + ".xml",
                            ctx.cfg().getString(Configuration.PROJECT_PATH_CFG),
                            Project.PROJECT_ROOT_ELEMENT_NAME);
        }
        else
        {
            // read project from stdin
            String prjName = ctx.cfg().getString(Configuration.PROJECT_NAME_CFG);
            cfg.setString(Configuration.PROJECT_FILE_CFG, prjName);
            String input = getInputFromInputStream();
            log.trace("Input from stdin was: {}", input);
            proElement = ctx.getElementfrom(
                    input,
                    Project.PROJECT_ROOT_ELEMENT_NAME);
        }

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

        // check tool chain to create makefile
        BuildSystemApi make = BuildSystemFactory.getBuildSystemFor(ctx);
        if(null == make)
        {
            log.error("Failed to find build environment!");
            ctx.close();
            return null;
        }
        allFiles = make.createBuildFor(allFiles);
        if(null == allFiles)
        {
            log.error("Build environment failed to generate files !");
            ctx.close();
            return null;
        }

        // IDE Project Files
        allFiles = IDEProjectFileGenerator.generateFileInto(ctx, allFiles);
        if(null == allFiles)
        {
            log.error("Failed to generate IDE project files!");
            ctx.close();
            return null;
        }

        // the environment may specify files that it needs - create those now
        Environment e = ctx.getEnvironment();
        allFiles = e.addRequiredFiles(ctx, allFiles);
        if(null == allFiles)
        {
            log.error("Failed to generate required files provided by the environment !");
            ctx.close();
            return null;
        }

        return allFiles;
    }

    private boolean createOutput(Context ctx, FileGroup allFiles)
    {
        if(true == ctx.cfg().getBool(Configuration.ZIP_OUTPUT_TO_STDOUT))
        {
            if(false ==allFiles.zipToStdout(ctx.cfg().getString(Configuration.PROJECT_FILE_CFG)))
            {
                log.error("Failed to zip to stdout!");
                return false;
            }
        }
        else if(true == ctx.cfg().getBool(Configuration.ZIP_OUTPUT))
        {
            if(false ==allFiles.saveToZip(ctx.cfg().getString(Configuration.OUTPUT_PATH_CFG),
                                          ctx.cfg().getString(Configuration.PROJECT_FILE_CFG)))
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
        FileGetter fg = new FileGetter(ctx);
        if(true == useXCmlRpc)
        {
            String url = cfg.getString(Configuration.XML_RPC_URL);
            XmlRpcGetter xrg = new XmlRpcGetter(url);
            fg.addGetter(xrg);
        }
        ctx.addFileGetter(fg);

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

        // create all needed files in memory
        FileGroup allFiles = createRessourcesFromSolution(ctx);
        if(null == allFiles)
        {
            log.error("Could not create ressources !");
            ctx.close();
            return;
        }

        // write created files out
        if(false == createOutput(ctx, allFiles))
        {
            log.error("Failed to create the output!");
            ctx.close();
            return;
        }

        // success ?
        successful = ctx.wasSucessful();
        log.trace("successful = {}", successful);
        if(false == successful)
        {
            log.trace("{}", ctx.getErrors());
        }
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
                System.exit(0);
            }
            else
            {
                // ERROR
                System.err.println("ERROR: Something went wrong!");
                System.exit(1);
            }
        }
        else
        {
            System.exit(1);
        }
    }

}
