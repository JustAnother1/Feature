package de.nomagic.puzzler.solution;

import java.util.HashMap;

import de.nomagic.puzzler.Project;

public class SolutionStub implements Solution
{
    private String availableElement = null;
    private HashMap<String, AlgorithmInstanceInterface> availableAlgorithms = new HashMap<String,AlgorithmInstanceInterface>();

    public SolutionStub()
    {
    }

    @Override
    public boolean getFromProject(Project pro)
    {
        return false;
    }

    @Override
    public boolean checkAndTestAgainstEnvironment()
    {
        return false;
    }
    
	public void setHasElement(String element) 
	{
		availableElement = element;
	}

	@Override
	public boolean treeContainsElement(String element) 
	{
		if(null == element)
		{
			return false;
		}
		if(true == element.equals(availableElement))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public AlgorithmInstanceInterface getRootAlgorithm() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void addAlgorithm(String name, AlgorithmInstanceInterface algo)
	{
		availableAlgorithms.put(name, algo);
	}
	
	@Override
	public AlgorithmInstanceInterface getAlgorithm(String name)
	{
		return availableAlgorithms.get(name);
	}

}
