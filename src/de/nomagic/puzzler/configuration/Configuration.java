package de.nomagic.puzzler.configuration;

import java.util.HashMap;

public class Configuration 
{
    private HashMap<String,String> StringSettings = new HashMap<String,String>();

	public Configuration() 
	{
	}

	public String getString(String setting) 
	{
		return StringSettings.get(setting);
	}

	public void setString(String name, String value) 
	{
		StringSettings.put(name, value);
	}

}
