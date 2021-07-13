package de.nomagic.puzzler.solution;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.jdom2.Element;


public class ConfiguredAlgorithmStub implements AlgorithmInstanceInterface
{
    private HashMap<String, String> properties = new HashMap<String, String>();
    private HashMap<String, String> parameters = new HashMap<String, String>();
    private HashMap<String, Element> elements = new HashMap<String, Element>();
    private HashMap<String, String> buildIn = new HashMap<String, String>();
    private HashMap<String, AlgorithmInstanceInterface> apiChilds = new HashMap<String,AlgorithmInstanceInterface>();

    private String api = "";
    private Algo_c_code code = null;

    public ConfiguredAlgorithmStub()
    {
    }
    
    @Override
    public String toString()
    {
    	return "ConfiguredAlgorithmStub";
        // return name + "(" + algorithmDefinition + ")";
    }

    @Override
    public String getProperty(String name)
    {
        return properties.get(name);
    }

    @Override
    public String dumpProperty()
    {
        return null;
    }

    @Override
    public String getParameter(String name)
    {
        return parameters.get(name);
    }

    @Override
    public String dumpParameter()
    {
        return null;
    }

    public void addPropertie(String name, String value)
    {
        properties.put(name, value);
    }

    public void addParameter(String name, String value)
    {
        parameters.put(name, value);
    }
    
    public void addBuildIn(String name, String val)
    {
    	buildIn.put(name, val);
    }

    @Override
    public String getBuildIn(String word)
    {
        return buildIn.get(word);
    }

    @Override
    public boolean hasApi(String searchedApi)
    {
        return api.contains(searchedApi);
    }

    public void setApi(String enabledApi)
    {
        api = api + enabledApi + ", ";
    }

    public void addAlgorithmElement(String Name, Element data)
    {
        elements.put(Name, data);
    }

    @Override
    public Element getAlgorithmElement(String elementName)
    {
        return elements.get(elementName);
    }

    @Override
    public Iterator<String> getAllChildren()
    {
        return null;
    }

    @Override
    public ConfiguredAlgorithm getChild(String name)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public String getApis()
    {
        return null;
    }

    @Override
    public ConfigurationHandler getCfgHandler()
    {
        return null;
    }

    public void setAlgo_c_code(Algo_c_code val)
    {
    	code = val;
    }
    
    @Override
    public Algo_c_code get_c_code()
    {
        return code;
    }


	public void addChildWithApi(String api, ConfiguredAlgorithmStub algo) 
	{
		apiChilds.put(api, algo);
	}
    
	@Override
	public AlgorithmInstanceInterface[] getChildsWithAPI(String apiStr) 
	{
		Vector<AlgorithmInstanceInterface> res = new Vector<AlgorithmInstanceInterface>();
		AlgorithmInstanceInterface child = apiChilds.get(apiStr);
		if(null != child)
		{
			res.add(child);
		}
		return res.toArray(new AlgorithmInstanceInterface[0]);
	}

	@Override
	public boolean containsElement(String tagName) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allRequiredDataAvailable()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
