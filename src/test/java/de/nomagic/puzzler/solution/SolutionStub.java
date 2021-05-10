package de.nomagic.puzzler.solution;

import java.util.HashMap;
import org.jdom2.Element;

import de.nomagic.puzzler.Project;

public class SolutionStub implements Solution
{
    private Element root = null;
    private final HashMap<String, Algorithm> algos = new HashMap<String, Algorithm>();

    public SolutionStub()
    {
        // TODO Auto-generated constructor stub
    }

    public void setRootElement(Element newRoot)
    {
        root = newRoot;
    }

    @Override
    public Element getRootElement()
    {
        return root;
    }

    public void addAlgorithm(String name, Algorithm algo)
    {
        algos.put(name, algo);
    }

    @Override
    public Algorithm getAlgorithm(String name)
    {
        return algos.get(name);
    }

    @Override
    public boolean getFromProject(Project pro)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean checkAndTestAgainstEnvironment()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getAlgorithmForElement(Element cfgElement)
    {
        // TODO Auto-generated method stub
        return false;
    }

}