
package de.nomagic.puzzler;

import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.Element;

import de.nomagic.puzzler.xmlrpc.XmlRpcGetter;

public interface FileGetter
{
	Document getXmlFromString(String in);
	Document getXmlFromStream(InputStream in);
	Document getXmlFile(String path, String name);
	Document tryToGetXmlFile(String path, String name, boolean failureIsError);
	Element getFromFile(String Name, String type, String rootElementName);
	void addGetter(XmlRpcGetter xrg);
}
