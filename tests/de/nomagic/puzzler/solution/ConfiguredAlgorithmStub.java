package de.nomagic.puzzler.solution;

import java.util.HashMap;
import java.util.Iterator;

import org.jdom2.Element;

public class ConfiguredAlgorithmStub implements AlgorithmInstanceInterface
{
    private HashMap<String, String> properties = new HashMap<String, String>();
    private HashMap<String, String> parameters = new HashMap<String, String>();
    private String api = "";

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

    @Override
    public String getBuildIn(String word)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasApi(String searchedApi)
    {
        return api.contains(searchedApi);
    }

    public void setApi(String enabledApi)
    {
        api = api + enabledApi;
    }

    @Override
    public Element getAlgorithmElement(String elementName) 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<String> getAllChildren() 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfiguredAlgorithm getChild(String name) 
    {
        // TODO Auto-generated method stub
        return null;
    }

}
