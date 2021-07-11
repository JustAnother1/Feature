package de.nomagic.puzzler.solution;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGetterImpl;

public class ConfiguredAlgorithm extends Base implements AlgorithmInstanceInterface
{
    public static final String REQUIRED_CFG_NAME = "parameter";
    public static final String REQUIRED_CFG_ATTRIBUTE_NAME = "name";
    public static final String REQUIRED_ALGORITHM_NAME = "childElement";
    public static final String REQUIRED_ALGORITHM_ATTRIBUTE_NAME = "type";

    public static final String ALGORITHM_REQUIREMENTS_CHILD_NAME = "required";
    public static final String ALGORITHM_PROVIDES_CHILD_NAME = "provides";
    public static final String ALGORITHM_PROVIDES_PROPERTY_VALUE = "value";

    public static final String BUILD_IN_NUM_OF_CHILDS = "algorithm.numOfChilds";
    public static final String BUILD_IN_NAME = "algorithm.InstanceName";

    public static final String ALGORITHM_REFFERENCE_ATTRIBUTE_NAME = "algorithm";
    public static final String ALGORITHM_NAME_ATTRIBUTE_NAME = "name";
    public static final String ALGORITHM_API_ATTRIBUTE_NAME = "api";
    
    // private static final Logger LOG = LoggerFactory.getLogger("ConfiguredAlgorithm");
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final String name;
    private final ConfigurationHandler cfgHandler;
    private final Element root;


    public ConfiguredAlgorithm(String name,
    						   Element root,
                               Context ctx,
                               AlgorithmInstanceInterface parent,
                               Element cfgElement)
    {
        super(ctx);
        this.name = name;
        this.root = root;
        cfgHandler = new ConfigurationHandler(this.toString());
        if(null != parent)
        {
            cfgHandler.setParentHandler(parent.getCfgHandler());
        }
        if(null != cfgElement)
        {
	        List<Attribute> attribs = cfgElement.getAttributes();
	        for(int i = 0; i < attribs.size(); i++)
	        {
	            Attribute curAttribute = attribs.get(i);
	            addConfiguration(curAttribute);
	        }
        }
    }

    public String getName()
    {
        return name;
    }  
    
    @Override
    public String toString()
    {
        if(null == root)
        {
            return name + "(" + "ERROR: unconfigured Algorithm" + ")";
        }
        else
        {
            String apis = root.getAttributeValue(ALGORITHM_API_ATTRIBUTE_NAME);
            if(null != apis)
            {
                return name + "(" + "Algorithm " + root.getAttributeValue(ALGORITHM_NAME_ATTRIBUTE_NAME)
                    + " implementing " + apis + ")";
            }
            else
            {
                return name + "(" + "Algorithm " + root.getAttributeValue(ALGORITHM_NAME_ATTRIBUTE_NAME) + ")";
            }
        }
    }

    public static ConfiguredAlgorithm getFromFile(Element cfgElement, Context ctx, ConfiguredAlgorithm parent)
    {
        if(null == ctx)
        {
            return null;
        }
        if(null == cfgElement)
        {
            return null;
        }
        Attribute algoAttr = cfgElement.getAttribute(ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
        if(null == algoAttr)
        {
            return null;
        }
        String Name = algoAttr.getValue();
        Element root = ctx.getFileGetter().getFromFile(Name,
                "algorithm",
                FileGetterImpl.ALGORITHM_ROOT_ELEMENT_NAME);
        if(null == root)
        {
            return null;
        }
        ConfiguredAlgorithm res = new ConfiguredAlgorithm(Name, root, ctx, parent, cfgElement);
        return res;
    }

    public String getApis()
    {
        if(null != root)
        {
            String apis = root.getAttributeValue(ALGORITHM_API_ATTRIBUTE_NAME);
            return apis;
        }
        else
        {
            log.trace("No API attribute.");
            return null;
        }
    }

    public boolean hasApi(String api)
    {
        if(null == api)
        {
            return false;
        }
        if(null != root)
        {
            String apis = root.getAttributeValue(ALGORITHM_API_ATTRIBUTE_NAME);
            if(null == apis)
            {
                return false;
            }
            String[] apiArr = apis.split(",");
            for(int i = 0; i < apiArr.length; i++)
            {
                apiArr[i] = apiArr[i].trim();
                if(apiArr[i].equals(api))
                {
                    return true;
                }
            }
            // check if one of the apis implement the searched API
            for(int i = 0; i < apiArr.length; i++)
            {
                Api curApi = Api.getFromFile(apiArr[i], ctx);
                if(null == curApi)
                {
                    log.error("Reference to the invalid API {} in {} !", apiArr[i], this.toString());
                }
                else
                {
                    if(true == curApi.alsoImplements(api))
                    {
                        return true;
                    }
                    // else continue search
                }
            }
            return false;
        }
        else
        {
            return false;
        }
    }

   
	@Override
	public AlgorithmInstanceInterface[] getChildsWithAPI(String apiStr) 
	{
		Vector<AlgorithmInstanceInterface> res = new Vector<AlgorithmInstanceInterface>();
		Iterator<String> it = getAllChildren();
		while(it.hasNext())
		{
			String name = it.next();
			AlgorithmInstanceInterface algo = getChild(name);
			if(true == algo.hasApi(apiStr))
			{
				res.add(algo);				
			}
			// else this algorithm does not implement the requested API
		}
		return res.toArray(new AlgorithmInstanceInterface[0]);
	}
    

    public void addConfiguration(Attribute curAttribute)
    {
        cfgHandler.addAttribute(curAttribute);
    }

    public void addChild(AlgorithmInstanceInterface algo)
    {
        cfgHandler.addAlgorithm(algo);
    }

    public AlgorithmInstanceInterface getChild(String name)
    {
        return cfgHandler.getAlgorithm(name);
    }

    public Iterator<String> getAllChildren()
    {
        return cfgHandler.getAllAlgorithmNameInterator();
    }

    public String getProperty(String name)
    {
        return cfgHandler.getProperty(name);
    }

    public String dumpProperty()
    {
        return cfgHandler.dumpProperty();
    }

    public String getParameter(String name)
    {
        return cfgHandler.getParameter(name);
    }

    public String dumpParameter()
    {
        return cfgHandler.dumpParameter();
    }

    public String getBuildIn(String word)
    {
        if(true == BUILD_IN_NUM_OF_CHILDS.equals(word))
        {
            return "" + cfgHandler.getNumberOfAlgorithms();
        }
        else if(true == BUILD_IN_NAME.equals(word))
        {
            return "" + this.name;
        }
        return null;
    }

    public Element getAlgorithmElement(String elementName)
    {
    	return root.getChild(elementName);
    }

    @Override
    public ConfigurationHandler getCfgHandler()
    {
        return cfgHandler;
    }
    
    private boolean checkRequiredConfigurationParametersAvailable(Element requirements)
    {
        List<Element> cfgReq = requirements.getChildren(REQUIRED_CFG_NAME);
        log.trace("{} has {} required parameters.", name, cfgReq.size());
        for(int i = 0; i < cfgReq.size(); i++)
        {
            Element curE = cfgReq.get(i);
            String attrName = curE.getAttributeValue(REQUIRED_CFG_ATTRIBUTE_NAME);
            if(null == attrName)
            {
                log.error("Missing attribute in {}", curE);
                ctx.addError("ConfiguredAlgorithm.allRequiredDataAvailable",
                                "Attribute \"" + REQUIRED_CFG_ATTRIBUTE_NAME
                                + "\" missing for required configuration !");
                return false;
            }
            Attribute at = cfgHandler.getAttribute(attrName);
            if(null == at)
            {
                ctx.addError("ConfiguredAlgorithm.allRequiredDataAvailable",
                             "required attribute " + attrName + " has not been given !");
                ctx.addError("ConfiguredAlgorithm.allRequiredDataAvailable",
                             cfgHandler.dumpAttribute());
                return false;
            }
            else
            {
                cfgHandler.addParameter(attrName, at.getValue());
            }
        }
        return true;
    }

    private boolean checkRequiredChildrensAvailable(Element requirements)
    {
        List<Element> algoReq = requirements.getChildren(REQUIRED_ALGORITHM_NAME);
        log.trace("{} has {} required children.", name, algoReq.size());
        for(int i = 0; i < algoReq.size(); i++)
        {
            Element curE = algoReq.get(i);
            String reqApi = curE.getAttributeValue(REQUIRED_ALGORITHM_ATTRIBUTE_NAME);
            if(null == reqApi)
            {
                ctx.addError("ConfiguredAlgorithm.allRequiredDataAvailable",
                                "Attribute " + REQUIRED_ALGORITHM_ATTRIBUTE_NAME
                                + " missing for required child element !");
                return false;
            }
            log.trace("required API for child {} is '{}'", i, reqApi);
            boolean found = false;
            Iterator<String> it = getAllChildren();
            while(it.hasNext())
            {
                AlgorithmInstanceInterface curAlgo = cfgHandler.getAlgorithm(it.next());
                log.trace("checking child {}", curAlgo);
                if(true == curAlgo.hasApi(reqApi))
                {
                    found = true;
                    break; // the while
                }
                else
                {
                    log.trace("API {} not found in {}.",reqApi,  curAlgo.getApis());
                }
            }
            if(false == found)
            {
                ctx.addError("ConfiguredAlgorithm.allRequiredDataAvailable",
                                toString() + " required child element of type '" + reqApi + "' not present !");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean allRequiredDataAvailable()
    {
        addProvidedData();

        Element requirements = root.getChild(ALGORITHM_REQUIREMENTS_CHILD_NAME);
        if(null == requirements)
        {
            log.trace("{} has no requirements!", name);
            return true;
        }
        // else :

        // required configuration parameters
        if(false == checkRequiredConfigurationParametersAvailable(requirements))
        {
            return false;
        }

        // required children(algorithms)
        if(false == checkRequiredChildrensAvailable(requirements))
        {
            return false;
        }

        return true;
    }

    private void addProvidedData()
    {
        Element provides = root.getChild(ALGORITHM_PROVIDES_CHILD_NAME);
        if(null != provides)
        {
            // this algorithm provides some informations
            List<Element> data = provides.getChildren();
            for(int i = 0; i < data.size(); i++)
            {
                Element curE = data.get(i);
                String propertyName = curE.getName();
                String propertyValue = curE.getAttributeValue(ALGORITHM_PROVIDES_PROPERTY_VALUE);
                log.trace("Property {} : {}", propertyName, propertyValue);
                // evaluate PropertyValue
                CondEvalResult res = ConditionEvaluator.evaluateConditionParenthesis(propertyValue, this, null, null);
                if(true == res.isValid())
                {
                    propertyValue = res.getResult();
                    log.trace("Property {} evaluated to {}", propertyName, propertyValue);
                    cfgHandler.addProperty(propertyName, propertyValue);
                }
                // else invalid Property
            }
        }
        // else this algorithm provides nothing and that might be OK.
    }

    @Override
    public Algo_c_code get_c_code()
    {
        Algo_c_code res = new Algo_c_code_impl(ctx, this);
        return res;
    }

	@Override
	public boolean containsElement(String tagName) 
	{
		if( null == root.getChild(tagName))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

}
