package de.nomagic.puzzler.solution;

import org.jdom2.Element;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Library;

public class LibraryStub implements Library 
{
	private Element result = null;

	public LibraryStub() 
	{
	}
	
	public void setResult(Element result)
	{
		this.result = result;
	}

	@Override
	public Element getAlgorithmElement(String AlgorithmName, Context ctx) 
	{
		return result;
	}
	
	public Element getApiElement(String ApiName, Context ctx)
	{
		return result;
	}

}
