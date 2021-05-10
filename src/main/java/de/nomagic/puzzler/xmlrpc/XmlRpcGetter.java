package de.nomagic.puzzler.xmlrpc;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class XmlRpcGetter
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private XmlRpcClient client;
    private final XmlRpcClientConfigImpl config;
    private final DokuwikiParser parser;
    private boolean logXmlFile = false;

    public XmlRpcGetter(String url)
    {
        parser = new DokuwikiParser();
        config = new XmlRpcClientConfigImpl();
        try {
            config.setServerURL(new URL(url));
            client = new XmlRpcClient();
            client.setConfig(config);
        }
        catch (MalformedURLException e)
        {
            log.error("Malformed URL when trying : " + url + " !");
            log.error("Details:");
            e.printStackTrace();
            client = null;
        }
        logXmlFile = log.isTraceEnabled();
    }

    public void setLogXmlFile(boolean setting)
    {
        logXmlFile = setting;
    }

    public Document getAsDocument(String Source)
    {
        if((null == client) || (null == Source))
        {
            return null;
        }
        if(true == Source.endsWith(".xml"))
        {
            Source =  Source.substring(0, Source.length() - ".xml".length());
        }

        String location = Source.replace(File.separatorChar, ':');
        log.trace("trying to read from {}", location);
        Object[] params = new Object[]{location};
        try
        {
            String result = (String) client.execute(DokuwikiParser.GET_INFO, params);
            Document jdomDocument = parser.convertToXml(result);
            if( (true == logXmlFile) && (null != jdomDocument) )
            {
                // we have an XML file and we should log it.
                XMLOutputter xmlOutput = new XMLOutputter();
                xmlOutput.setFormat(Format.getPrettyFormat());
                xmlOutput.output(jdomDocument, System.err);
            }
            // else don't log it.
            return jdomDocument;
        }
        catch (XmlRpcException | IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
