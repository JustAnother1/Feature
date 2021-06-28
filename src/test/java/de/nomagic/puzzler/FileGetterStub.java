package de.nomagic.puzzler;

import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.Element;

import de.nomagic.puzzler.xmlrpc.XmlRpcGetter;

public class FileGetterStub implements FileGetter 
{
	private Element getFromFileResult = null;
	
	public FileGetterStub()
	{
		
	}

	@Override
	public Document getXmlFromString(String in) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document getXmlFromStream(InputStream in) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document getXmlFile(String path, String name) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document tryToGetXmlFile(String path, String name, boolean failureIsError) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setGetFtromFileResult(Element val)
	{
		getFromFileResult = val;
	}
	
	@Override
	public Element getFromFile(String Name, String type, String rootElementName) 
	{		
		return getFromFileResult;
	}

	@Override
	public void addGetter(XmlRpcGetter xrg) 
	{
		// TODO Auto-generated method stub

	}

}
