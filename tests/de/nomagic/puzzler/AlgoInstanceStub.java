package de.nomagic.puzzler;

import java.util.HashMap;

import de.nomagic.puzzler.solution.AlgorithmInstanceInterface;

public class AlgoInstanceStub implements AlgorithmInstanceInterface
{
    private HashMap<String, String> properties = new  HashMap<String, String>();
    private HashMap<String, String> parameters = new  HashMap<String, String>();
    private HashMap<String, String> buildin = new  HashMap<String, String>();

    public AlgoInstanceStub()
    {

    }

    public void addProperty(String name, String value)
    {
        properties.put(name, value);
    }

    public void addParameter(String name, String value)
    {
        parameters.put(name, value);
    }

    public void addBuildIn(String name, String value)
    {
        buildin.put(name, value);
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

    @Override
    public String getBuildIn(String word)
    {
        return buildin.get(word);
    }

}
