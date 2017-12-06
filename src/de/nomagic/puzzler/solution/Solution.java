package de.nomagic.puzzler.solution;

import org.jdom2.Element;

import de.nomagic.puzzler.Project;

public interface Solution
{
    Element getRootElement();
    Algorithm getAlgorithm(String Name);
    boolean getFromProject(Project pro);
    boolean checkAndTestAgainstEnvironment();
}
