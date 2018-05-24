package de.nomagic.puzzler.solution;

public interface AlgorithmInstanceInterface
{
    String getProperty(String name);
    String dumpProperty();
    String getParameter(String name);
    String dumpParameter();
    String getBuildIn(String word);
    boolean hasApi(String api);
}
