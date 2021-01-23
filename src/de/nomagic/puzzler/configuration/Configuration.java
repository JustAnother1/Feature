package de.nomagic.puzzler.configuration;

import java.util.HashMap;
import java.util.Vector;

public class Configuration
{
    // names of Settings:

    // command line switches
    public static final String SOLUTION_PATH_CFG = "solution_directory";
    public static final String LIB_PATH_CFG = "library_path";
    public static final String OUTPUT_PATH_CFG = "output_path";
    public static final String PROJECT_FILE_CFG = "projectName";
    public static final String PROJECT_NAME_CFG = "zip_stdout_projectName";
    public static final String PROJECT_PATH_CFG = "projectPath";
    public static final String SOLUTION_FILE_CFG = "solutionFile";
    public static final String ENVIRONMENT_PATH_CFG = "environment_path";
    public static final String ZIP_OUTPUT = "zip_output";
    public static final String ZIP_OUTPUT_TO_STDOUT = "zip_output_to_stdout";
    public static final String XML_RPC_URL = "xml_rpc_url";
    // -D
    public static final String CFG_DOC_CODE_SRC = "document_code_source";
    public static final String CFG_EMBEETLE_PROJECT = "embeetle_project";
    // if you add variables here then also add them to listAllConfigurationVariables()

    private HashMap<String, Vector<String>> stringArraySettings = new HashMap<String,Vector<String>>();
    private HashMap<String, Boolean> boolArraySettings = new HashMap<String, Boolean>();

    public static final String listAllConfigurationVariables()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-30s : %s%n",
            // Variable name and parameter
            CFG_DOC_CODE_SRC + "=true",
            // Description
            "define in comments which algorithm cretaed the source code lines"));
        sb.append(String.format("%-30s : %s%n",
            CFG_EMBEETLE_PROJECT + "=true",
            "create a Embetle IDE(https://embeetle.com/) project"));
        // new variables go here !
        return sb.toString();
    }

    public Configuration()
    {
    }

    public boolean getBool(String setting)
    {
        Boolean res = boolArraySettings.get(setting);
        if(null == res)
        {
            // we do not have that setting so it is false
            res = false;
        }
        return res;
    }

    public void setBool(String name, Boolean value)
    {
        boolArraySettings.put(name, value);
    }

    public String getString(String setting)
    {
        Vector<String> curVector = stringArraySettings.get(setting);
        if(null == curVector)
        {
            return "";
        }
        else
        {
            return curVector.get(0);
        }
    }

    public void setString(String name, String value)
    {
        Vector<String> curVector = stringArraySettings.get(name);
        if(null == curVector)
        {
            // first String for this setting
            curVector = new Vector<String>();
            stringArraySettings.put(name, curVector);
        }
        curVector.add(value);
    }

    public String[] getStringsOf(String setting)
    {
        Vector<String> curVector = stringArraySettings.get(setting);
        if(null == curVector)
        {
            return new String[0];
        }
        else
        {
            return curVector.toArray(new String[0]);
        }
    }

}
