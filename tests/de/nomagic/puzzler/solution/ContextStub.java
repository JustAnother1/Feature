package de.nomagic.puzzler.solution;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.configuration.Configuration;

public class ContextStub implements Context
{

    public ContextStub()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean wasSucessful()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void close()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addError(Object ref, String msg)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Configuration cfg()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Environment getEnvironment()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addEnvironment(Environment e)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addSolution(Solution s)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Solution getSolution()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
