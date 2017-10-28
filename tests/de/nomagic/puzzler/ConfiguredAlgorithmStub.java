package de.nomagic.puzzler;

import java.util.HashMap;

import de.nomagic.puzzler.solution.AlgorithmInformaton;

public class ConfiguredAlgorithmStub implements AlgorithmInformaton
{
	private HashMap<String, String> properties = new HashMap<String, String>();
	private HashMap<String, String> parameters = new HashMap<String, String>();

	public ConfiguredAlgorithmStub() 
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getProperty(String name) 
	{
		return properties.get(name);
	}

	@Override
	public String dumpProperty() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameter(String name) 
	{
		return parameters.get(name);
	}

	@Override
	public String dumpParameter() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void addPropertie(String name, String value) 
	{
		properties.put(name, value);
	}

	public void addParameter(String name, String value) 
	{
		parameters.put(name, value);
	}

}
