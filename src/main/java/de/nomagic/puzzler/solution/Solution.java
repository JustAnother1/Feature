package de.nomagic.puzzler.solution;

import de.nomagic.puzzler.Project;

public interface Solution
{
    boolean getFromProject(Project pro);
    boolean checkAndTestAgainstEnvironment();
	boolean treeContainsElement(String TagName);
	AlgorithmInstanceInterface getRootAlgorithm();
    AlgorithmInstanceInterface getAlgorithm(String name);
}
