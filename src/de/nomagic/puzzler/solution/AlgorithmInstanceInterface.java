package de.nomagic.puzzler.solution;

import java.util.Iterator;

import org.jdom2.Element;

public interface AlgorithmInstanceInterface
{
    String getProperty(String name);
    String dumpProperty();
    String getParameter(String name);
    String dumpParameter();
    String getBuildIn(String word);
    boolean hasApi(String api);
    public Element getAlgorithmElement(String elementName);
    public Iterator<String> getAllChildren();
    public ConfiguredAlgorithm getChild(String name);
}
