package de.nomagic.puzzler;

import java.util.Vector;

import org.jdom2.Element;

import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.Solution;

public class ContextStub implements Context
{
    private Vector<String> errors = null;
    private Solution s = null;
    private Configuration cfg;
    private Environment e = null;
    private FileGetter fg = null;

    public ContextStub()
    {
        this.cfg = null;
    }

    public ContextStub(Configuration cfg)
    {
        this.cfg = cfg;
    }

    @Override
    public boolean wasSucessful()
    {
    	if(null == errors)
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }

    @Override
    public void close()
    {
    }

    @Override
    public void addError(Object ref, String msg)
    {
        String error = ref.toString() + " : " + msg;
        if(null == errors)
        {
            errors = new Vector<String>();
        }
        errors.add(error);
    }

    public String getErrors()
    {
        if(null == errors)
        {
            return "";
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < errors.size(); i++)
            {
                sb.append(errors.get(i) + System.getProperty("line.separator"));
            }
            return sb.toString();
        }
    }

    @Override
    public Configuration cfg()
    {
        return cfg;
    }

    @Override
    public Environment getEnvironment()
    {
        return e;
    }

    @Override
    public void addEnvironment(Environment e)
    {
        this.e = e;
    }

    @Override
    public void addSolution(Solution s)
    {
        this.s = s;
    }

    @Override
    public Solution getSolution()
    {
        return s;
    }

    @Override
    public Element getElementfrom(String fileName, String path,
            String elementName)
    {
        return null;
    }

    @Override
    public Element loadElementFrom(Element uncheckedElement, String path,
            String elementName)
    {
        return null;
    }

    @Override
    public Element getElementfrom(String in, String elementName)
    {
        return null;
    }

    @Override
    public void addFileGetter(FileGetter fg)
    {
    	this.fg = fg;
    }

    @Override
    public FileGetter getFileGetter()
    {
        return fg;
    }

}
