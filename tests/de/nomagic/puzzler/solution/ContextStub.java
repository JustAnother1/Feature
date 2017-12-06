package de.nomagic.puzzler.solution;

import java.util.Vector;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.configuration.Configuration;

public class ContextStub implements Context
{
    private Vector<String> errors = null;
    private Solution s = null;

    public ContextStub()
    {
    }

    @Override
    public boolean wasSucessful()
    {
        return false;
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

    public String[] getErrors()
    {
        if(null == errors)
        {
            return new String[0];
        }
        else
        {
            return errors.toArray(new String[0]);
        }
    }

    @Override
    public Configuration cfg()
    {
        return null;
    }

    @Override
    public Environment getEnvironment()
    {
        return null;
    }

    @Override
    public void addEnvironment(Environment e)
    {
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

}
