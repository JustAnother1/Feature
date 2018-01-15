package de.nomagic.puzzler.configuration;

import java.util.HashMap;
import java.util.Vector;

public class Configuration
{
    public final static String ROOT_PATH_CFG = "work_directory";
    public final static String LIB_PATH_CFG = "library_path";
    public final static String OUTPUT_PATH_CFG = "output_path";
    public final static String PROJECT_FILE_CFG = "projectName";
    public final static String PROJECT_PATH_CFG = "projectPath";
    public final static String SOLUTION_FILE_CFG = "solutionFile";
    public final static String ENVIRONMENT_PATH_CFG = "environment_path";

    private HashMap<String, Vector<String>> stringArraySettings = new HashMap<String,Vector<String>>();

    public Configuration()
    {
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
