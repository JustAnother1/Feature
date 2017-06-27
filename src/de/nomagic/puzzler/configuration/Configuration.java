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


    private HashMap<String, String> StringSettings = new HashMap<String,String>();
    private HashMap<String, Vector<String>> StringArraySettings = new HashMap<String,Vector<String>>();

    public Configuration()
    {
    }

    public String getString(String setting)
    {
        String res = StringSettings.get(setting);
        if(null == res)
        {
            return "";
        }
        else
        {
            return res;
        }
    }

    public void setString(String name, String value)
    {
        StringSettings.put(name, value);
    }

    public void addStringTo(String name, String value)
    {
        Vector<String> curVector = StringArraySettings.get(name);
        if(null == curVector)
        {
            // first String for this setting
            curVector = new Vector<String>();
            StringArraySettings.put(name, curVector);
        }
        curVector.add(value);
    }

    public String[] getStringsOf(String setting)
    {
        Vector<String> curVector = StringArraySettings.get(setting);
        if(null == curVector)
        {
            // A single String setting
            String help = getString(setting);
            if(null != help)
            {
                String[] res = new String[1];
                res[0] = help;
                return res;
            }
            else
            {
                return new String[0];
            }
        }
        else
        {
            return curVector.toArray(new String[0]);
        }
    }

}
