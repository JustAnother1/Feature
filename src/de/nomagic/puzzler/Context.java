package de.nomagic.puzzler;

import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.Solution;

public interface Context
{
    public boolean wasSucessful();
    public void close();
    public void addError(Object ref, String msg);
    public Configuration cfg();
    public Environment getEnvironment();
    public void addEnvironment(Environment e);
    public void addSolution(Solution s);
    public Solution getSolution();

}
