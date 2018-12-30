package de.nomagic.puzzler;

import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.Solution;

public interface Context
{
    boolean wasSucessful();
    String getErrors();
    void close();
    void addError(Object ref, String msg);
    Configuration cfg();
    Environment getEnvironment();
    void addEnvironment(Environment e);
    void addSolution(Solution s);
    Solution getSolution();
}
