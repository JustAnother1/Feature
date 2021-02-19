package de.nomagic.puzzler.solution;

import java.util.Iterator;

import org.jdom2.Element;

import de.nomagic.puzzler.Generator.FunctionCall;

public interface AlgorithmInstanceInterface
{
    String getName();
    String getDescription();
    String getProperty(String name);
    String dumpProperty();
    String getParameter(String name);
    String dumpParameter();
    String getBuildIn(String word);
    String getApis();
    boolean hasApi(String api);
    Element getAlgorithmElement(String elementName);
    Iterator<String> getAllChildren();
    AlgorithmInstanceInterface getChild(String name);
    String getImplementationOf(FunctionCall fc);
    void addExtraAlgo(AlgorithmInstanceInterface algo);
    Iterable<AlgorithmInstanceInterface> getAdditionals();
    ConfigurationHandler getCfgHandler();
}
