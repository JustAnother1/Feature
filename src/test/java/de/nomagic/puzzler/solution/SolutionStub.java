package de.nomagic.puzzler.solution;

import de.nomagic.puzzler.Project;

public class SolutionStub implements Solution
{
    private String availableElement = null;

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

	@Override
	public AlgorithmInstanceInterface getAlgorithm(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
