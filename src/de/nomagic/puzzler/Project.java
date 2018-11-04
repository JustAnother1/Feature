package de.nomagic.puzzler;

import org.jdom2.Element;

public interface Project 
{
    public final static String PROJECT_ROOT_ELEMENT_NAME = "project";
    public final static String ENVIRONMENT_ELEMENT_NAME = "environment";
    public final static String SOLUTION_ELEMENT_NAME = "solution";
    public boolean getFromFiles();
    public Element getEnvironmentElement();
    public Element getSolutionElement();
}
