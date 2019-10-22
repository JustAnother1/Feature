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
    }

    @Override
    public String getProperty(String name)
    {
        return properties.get(name);
    }

    @Override
    public String dumpProperty()
    {
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
        return null;
    }

    @Override
    public Iterator<String> getAllChildren()
    {
        return null;
    }

    @Override
    public ConfiguredAlgorithm getChild(String name)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public String getDescription()
    {
        return null;
    }

}
