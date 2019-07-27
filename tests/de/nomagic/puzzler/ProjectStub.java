package de.nomagic.puzzler;

import org.jdom2.Element;

public class ProjectStub implements Project 
{
    private Element environmentEle = null;

    @Override
    public Element getEnvironmentElement() 
    {
        return environmentEle;
    }

    @Override
    public Element getSolutionElement() 
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setEnvironmentElement(Element rootEle) 
    {
        environmentEle = rootEle;
    }

    @Override
    public boolean loadFromElement(Element root) 
    {
        // TODO Auto-generated method stub
        return false;
    }

}
