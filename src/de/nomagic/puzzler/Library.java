package de.nomagic.puzzler;

import org.jdom2.Element;

public interface Library {
    Element getAlgorithmElement(String AlgorithmName, Context ctx);
    Element getApiElement(String ApiName, Context ctx);
}
