
package de.nomagic.puzzler;

import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.progress.ProgressReport;

public class FileGetter 
{
	private static final Logger log = LoggerFactory.getLogger("FileGetter");
	
	public static Document getXmlFile(String path, String name, ProgressReport report) 
	{
		String xmlSource;
		if(null == path)
		{
			xmlSource = name;
		}
		else
		{
			xmlSource = path + File.separator + name;
		}
		SAXBuilder jdomBuilder = new SAXBuilder();
		Document jdomDocument = null;
		try 
		{
			jdomDocument = jdomBuilder.build(xmlSource);
		}
		catch (JDOMException e) 
		{
			report.addError("FileGetter", "JDOM Exception");
			log.trace(Tool.fromExceptionToString(e));
		}
		catch (IOException e) 
		{
			report.addError("FileGetter", e.getMessage());
			log.trace(Tool.fromExceptionToString(e));
		}
		return jdomDocument;
	}

}
