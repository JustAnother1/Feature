package de.nomagic.puzzler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.xmlrpc.XmlRpcGetter;

public class CommandLineParser
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final Configuration cfg;
    private boolean foundOutputDirectory = false;
    private boolean foundLibDirectory = false;
    private String[] args = null;

    public CommandLineParser()
    {
        cfg = new Configuration();
    }


    public Configuration getConfiguration()
    {
        return cfg;
    }

    private void printHelp()
    {
        System.out.println("Feature Puzzler [Parameters] [Project File]");
        System.out.println("Parameters:");
        System.out.println("-D<SettingName>=<Value>    : Set a value to a configuration variable.");
        System.out.println("--list_conf_variables      : list all currently supported configuration variables.");
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
        System.out.println("--dump_remote_ressource <path>");
        System.out.println("                           : print the content of the linked ressource.");
        System.out.println("-z <filename> / --zip <filename>");
        System.out.println("                           : zip created data (ignores output folder setting).");
        System.out.println("--zip_to_stdout            : zip created data and write zip file to stdout.");
        System.out.println("--prj_name <name>          : project name to use when zip to stdout.");
        System.out.println("<Projectfile>.xml          : define the project to process.");
        System.out.println("                           : if missing the project is read from stdin.");
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

    private boolean parseSwitches(int idx)
    {
        switch(args[idx])
        {
        case "--dump_remote_ressource":
            idx++;
            String ressource = args[idx];
            XmlRpcGetter xrg = new XmlRpcGetter(cfg.getString(Configuration.XML_RPC_URL));
            xrg.getAsDocument(ressource);
            return false;

        case "-e":
        case "--environment_dirctory":
            // environment directory
            idx++;
            if(false == cmdln_handleDirectory(args[idx], Configuration.ENVIRONMENT_PATH_CFG))
            {
                return false;
            }
            break;

        case "-h":
        case "--help":
            // help
            printHelp();
            return false;

        case "-l":
        case "--library_directory":
            // Library directory
            idx++;
            if(false == cmdln_handleDirectory(args[idx], Configuration.LIB_PATH_CFG))
            {
                return false;
            }
            break;

        case "--list_conf_variables":
            // list currently supported configuration variables
            System.out.println("currently supported configuration variables:");
            System.out.println(Configuration.listAllConfigurationVariables());
            return false;

        case "-o":
        case "--output_directory":
            // output directory
            idx++;
            if(false == cmdln_handleDirectory(args[idx], Configuration.OUTPUT_PATH_CFG))
            {
                return false;
            }
            break;

        case "--prj_name":
            // project name
            idx++;
            cfg.setString(Configuration.PROJECT_NAME_CFG, args[idx]);
            break;

        case "-p":
        case "--project_directory":
            // project directory
            idx++;
            if(false == cmdln_handleDirectory(args[idx], Configuration.PROJECT_PATH_CFG))
            {
                return false;
            }
            break;

        case "-s":
        case "--solution_directory":
            // solution directory
            idx++;
            if(false == cmdln_handleDirectory(args[idx], Configuration.SOLUTION_PATH_CFG))
            {
                return false;
            }
            break;

        case "-v":
            // verbose output
            // already handled -> ignore
            break;

        case "-x":
            cfg.setBool(Configuration.USE_XML_RPC, true);
            idx++;
            if(false == cmdln_xmlRpcUrl(args[idx]))
            {
                return false;
            }
            break;

        case "-z":
        case "--zip":
            // zip output
            idx++;
            if(false == cmdln_handleDirectory(args[idx], Configuration.ZIP_OUTPUT))
            {
                return false;
            }
            break;

        case "--zip_to_stdout":
            // zip output to stdout
            cfg.setBool(Configuration.ZIP_OUTPUT_TO_STDOUT, true);
            foundOutputDirectory = true;
            log.trace("command Line config: zip output to stdout");
            break;

        default:
            if(true == args[idx].startsWith("-D"))
            {
                // Some configuration variable
                int pos = args[idx].indexOf('=');
                if(-1 == pos)
                {
                    System.err.println("Invalid Parameter(-D needs '=') : " + args[idx]);
                    return false;
                }
                String settingName = args[idx].substring(2, pos); // skip the "-D"
                String settingValue = args[idx].substring(pos + 1);
                settingName = settingName.trim();
                settingValue = settingValue.trim();

                cfg.setString(settingName, settingValue);
                log.trace("command Line config: {} = {}", settingName, settingValue);
            }
            else
            {
                System.err.println("Invalid Parameter : " + args[idx]);
                return false;
            }
        }
        // OK
        return true;
    }

    public boolean parse(String[] args)
    {
        this.args = args;
        int idx = 0;
        while(idx < args.length)
        {
            if(true == args[idx].startsWith("-"))
            {
                if(false == parseSwitches(idx))
                {
                    return false;
                }
            }
            else
            {
                if(true == args[idx].endsWith(".xml"))
                {
                    String projectName =  args[idx].substring(0, args[idx].length() - ".xml".length());
                    cfg.setString(Configuration.PROJECT_FILE_CFG, projectName);
                }
                else
                {
                    System.err.println("Invalid Parameter : " + args[idx]);
                    return false;
                }
            }
            idx++;
        }

        // check parameters:
        // - did we found everything we need to find? (configuration complete?)
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

}
