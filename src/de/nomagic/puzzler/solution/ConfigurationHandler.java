package de.nomagic.puzzler.solution;

import java.util.HashMap;
import java.util.Iterator;

import org.jdom2.Attribute;

public class ConfigurationHandler
{
    private final String name;
    private final HashMap<String, String> properties = new  HashMap<String, String>();
    private final HashMap<String, String> parameters = new  HashMap<String, String>();
    private final HashMap<String, Attribute> cfgAttributes = new HashMap<String, Attribute>();
    private final HashMap<String, AlgorithmInstanceInterface> cfgAlgorithms = new HashMap<String, AlgorithmInstanceInterface>();

    private ConfigurationHandler parent = null;

    public ConfigurationHandler(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return name;
    }

    public void addParameter(String name, String value)
    {
        parameters.put(name, value);
    }

    public void addProperty(String name, String value)
    {
        properties.put(name, value);
    }

    public void setParentHandler(ConfigurationHandler cfgHandler)
    {
        parent = cfgHandler;
    }

    public String getProperty(String propertyName)
    {
        String res = properties.get(propertyName);
        if((null == res) && (null != parent))
        {
            res = parent.getProperty(propertyName);
        }
        return res;
    }

    public String dumpProperty()
    {
        if(true == properties.isEmpty())
        {
            return "Properties: <empty>";
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Properties:\n");
            Iterator<String> keys = properties.keySet().iterator();
            while(keys.hasNext())
            {
                String key = keys.next();
                sb.append(key + " : " + properties.get(key) + "\n");
            }
            if(null != parent)
            {
                sb.append("Parent: " + parent.toString());
                sb.append(parent.dumpProperty());
            }
            return sb.toString();
        }
    }

    public String getParameter(String parameterName)
    {
        String res = parameters.get(parameterName);
        if((null == res) && (null != parent))
        {
            res = parent.getParameter(parameterName);
        }
        return res;
    }

    public String dumpParameter()
    {
        if(true == parameters.isEmpty())
        {
            return "Parameter: <empty>";
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Parameter:\n");
            Iterator<String> keys = parameters.keySet().iterator();
            while(keys.hasNext())
            {
                String key = keys.next();
                sb.append(key + " : " + parameters.get(key) + "\n");
            }
            return sb.toString();
        }
    }

    public void addAttribute(Attribute curAttribute)
    {
        cfgAttributes.put(curAttribute.getName(), curAttribute);
    }

    public Attribute getAttribute(String attrName)
    {
        return cfgAttributes.get(attrName);
    }

    public String dumpAttribute()
    {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = cfgAttributes.keySet().iterator();
        sb.append("Given Attributes: ");
        while(it.hasNext())
        {
            sb.append(it.next());
            sb.append(" ");
        }
        sb.append("!");
        return sb.toString();
    }

    public AlgorithmInstanceInterface getAlgorithm(String algorithmName)
    {
        return cfgAlgorithms.get(algorithmName);
    }

    public Iterator<String> getAllAlgorithmNameInterator()
    {
        return cfgAlgorithms.keySet().iterator();
    }

    public void addAlgorithm(AlgorithmInstanceInterface algo)
    {
        if(null != algo)
        {
            cfgAlgorithms.put(algo.getName(), algo);
        }
    }

    public int getNumberOfAlgorithms()
    {
        return  cfgAlgorithms.size();
    }
}
