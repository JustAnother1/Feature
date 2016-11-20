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
import de.nomagic.puzzler.progress.ProgressReport;
import de.nomagic.puzzler.progress.ProgressReportFactory;
import de.nomagic.puzzler.solution.Solution;

/**
 * @author Lars P&ouml;tter
 * (<a href=mailto:Lars_Poetter@gmx.de>Lars_Poetter@gmx.de</a>)
 *
 */
public class PuzzlerMain 
{
	public final static String ROOT_PATH_CFG = "work_directory";
	public final static String LIB_PATH_CFG = "library_path";
	public final static String OUTPUT_PATH_CFG = "output_path";
	
	private Configuration cfg = null;
	private ProgressReport report = null;
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
        System.out.println("Feature Puzzler");
        System.out.println("Parameters:");
        System.out.println("-h / --help                : print this message.");
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
		Configuration cfg = new Configuration();
        for(int i = 0; i < args.length; i++)
        {
            if(true == args[i].startsWith("-"))
            {
                if( (true == "-h".equals(args[i])) || (true == "--help".equals(args[i])))
                {
                	printHelp();
                    return false;
                }
                else if(true == "-v".equals(args[i]))
                {
                    // already handled -> ignore
                }
                else if( (true == "-w".equals(args[i])) || (true == "--work_dirctory".equals(args[i])))
                {
                	i++;
                	String workDirectory = args[i];
                	cfg.setString(ROOT_PATH_CFG, workDirectory);
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
            		cfg.setString(Project.PROJECT_FILE_CFG, args[i]);
            	}
            	else
            	{
            		System.err.println("Invalid Parameter : " + args[i]);
                	return false;
            	}
            }
        }
		// TODO check parameters
        // TODO give them command line parameters
    	cfg.setString(Project.PROJECT_PATH_CFG, cfg.getString(ROOT_PATH_CFG));
    	cfg.setString(LIB_PATH_CFG, cfg.getString(ROOT_PATH_CFG) + "lib/");
        
        this.cfg = cfg;
        return true;
	}	
	
	public void execute() 
	{
		if(null == cfg)
		{
			return;
		}
		report = ProgressReportFactory.getReportFor(cfg);
		// open Project file
		Project pro = new Project(report);
		pro.setConfiguration(cfg);
		if(false == pro.getFromFiles())
		{
			report.close();
			return;
		}
		
		// Find environment
		Environment e = new Environment(report);
		e.setConfiguration(cfg);
		if(false == e.getFromProject(pro))
		{
			report.close();
			return;
		}
		
		// find solution
		Solution s = new Solution(report);
		s.setConfiguration(cfg);
		if(false == s.getFromProject(pro))
		{
			report.close();
			return;
		}
		
		// check if solution refers to undefined entities
		// test that all environment References are meet by the environment.
		if(false == s.checkAndTestAgainst(e))
		{
			report.close();
			return;
		}
		
		// create "code creator" back end (creates the C Source Code)
		Generator gen = new C_CodeGenerator(report);
		gen.setConfiguration(cfg);
		// give solution to code creator to create code project
		FileGroup files = gen.generateFor(s);
		if(null == files)
		{
			report.close();
			return;
		}
		// check tool chain to create makefile
		BuildSystem make = new MakeBuildSystem(report);
		make.setConfiguration(cfg);
		if(false == make.createBuildFor(files))
		{
			report.close();
			return;
		}
		
		if(false ==files.saveToFolder(cfg.getString(OUTPUT_PATH_CFG), report))
		{
			report.close();
			return;
		}
		// success !
		report.setSucessful();
		report.close();
		successful = true;
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
				System.exit(1);
			}
		}
		else
		{
			System.exit(1);
		}
	}

}
