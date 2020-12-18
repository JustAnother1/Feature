package de.nomagic.puzzler.solution;

import java.util.Iterator;

import org.jdom2.Element;

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
    ConfiguredAlgorithm getChild(String name);
}
