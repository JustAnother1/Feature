package de.nomagic.puzzler;

import java.io.InputStream;

import org.jdom2.Element;
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
    Element getElementfrom(InputStream in, String elementName);
    Element getElementfrom(String fileName, String path, String elementName);
    Element loadElementFrom(Element uncheckedElement, String path, String elementName);
    void addFileGetter(FileGetter fg);
    FileGetter getFileGetter();
}
