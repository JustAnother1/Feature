package de.nomagic.puzzler.configuration;

import java.util.HashMap;
import java.util.Vector;

public class Configuration
{
    // names of Settings
    public static final String ROOT_PATH_CFG = "work_directory";
    public static final String LIB_PATH_CFG = "library_path";
    public static final String OUTPUT_PATH_CFG = "output_path";
    public static final String PROJECT_FILE_CFG = "projectName";
    public static final String PROJECT_PATH_CFG = "projectPath";
    public static final String SOLUTION_FILE_CFG = "solutionFile";
    public static final String ENVIRONMENT_PATH_CFG = "environment_path";
    public static final String ZIP_OUTPUT = "zip_output";
    

    private HashMap<String, Vector<String>> stringArraySettings = new HashMap<String,Vector<String>>();
    private HashMap<String, Boolean> boolArraySettings = new HashMap<String, Boolean>();

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
