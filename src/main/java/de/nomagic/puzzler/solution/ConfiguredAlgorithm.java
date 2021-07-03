package de.nomagic.puzzler.solution;

import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Project;

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

    private static final Logger LOG = LoggerFactory.getLogger("ConfiguredAlgorithm");

    private final String name;
    private final Algorithm algorithmDefinition;
    private final ConfigurationHandler cfgHandler;



    public ConfiguredAlgorithm(String name,
                               Algorithm algorithmDefinition,
                               Context ctx,
                               AlgorithmInstanceInterface parent)
    {
        super(ctx);
        this.name = name;
        this.algorithmDefinition = algorithmDefinition;
        cfgHandler = new ConfigurationHandler(this.toString());
        if(null != parent)
        {
            cfgHandler.setParentHandler(parent.getCfgHandler());
        }
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        if(null != algorithmDefinition)
        {
            return name + " (" + algorithmDefinition.toString() + ")";
        }
        else
        {
            return name + "(no algorithm attached)";
        }
    }
    public String getApis()
    {
        if(null == algorithmDefinition)
        {
            LOG.trace("No algorithm available.");
            return null;
        }
        else
        {
            return algorithmDefinition.getApis();
        }
    }

    public boolean hasApi(String api)
    {
        if(null == algorithmDefinition)
        {
            return false;
        }
        else
        {
            return algorithmDefinition.hasApi(api);
        }
    }

    @Override
    public String toString()
    {
        return name + "(" + algorithmDefinition + ")";
    }

    private void addConfiguration(Attribute curAttribute)
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
        if(null == algorithmDefinition)
        {
            return null;
        }
        else
        {
            return algorithmDefinition.getChild(elementName);
        }
    }

    public List<Element> getAlgorithmElements(String elementName)
    {
        if(null == algorithmDefinition)
        {
            return null;
        }
        else
        {
            return algorithmDefinition.getChildren(elementName);
        }
    }

    @Override
    public ConfigurationHandler getCfgHandler()
    {
        return cfgHandler;
    }

    public static ConfiguredAlgorithm getTreeFrom(Context ctx, ConfiguredAlgorithm parent)
    {
        if(null == ctx)
        {
            return null;
        }
        Solution s = ctx.getSolution();
        if(null == s)
        {
            return null;
        }
        Element root = s.getRootElement();
        if(null == root)
        {
            ctx.addError("ConfiguredAlgorithm.getTreeFrom",
                            "No root element in the provided solution !");
            return null;
        }
        if(false == Project.SOLUTION_ELEMENT_NAME.equals(root.getName()))
        {
            ctx.addError("ConfiguredAlgorithm.getTreeFrom.1",
                            "invalid root tag (" + root.getName() + ") !");
            return null;
        }
        List<Element> children = root.getChildren();
        if(true == children.isEmpty())
        {
            ctx.addError("ConfiguredAlgorithm.getTreeFrom.2",
                    "No algorithm elements in the provided solution !");
            return null;
        }
        Element configElement = children.get(0);
        // get Algorithm to determine the API
        String algoName = configElement.getAttributeValue(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
        Algorithm algo = s.getAlgorithm(algoName);
        if(null == algo)
        {
            ctx.addError("ConfiguredAlgorithm.getTreeFrom.3",
                            "Failed to get Algorithm for " + algoName + " !");
            return null;
        }
        return getTreeFor(configElement, ctx, parent);
    }

    private static ConfiguredAlgorithm getTreeFromEnvironment(Element cfgElement,
            Context ctx, AlgorithmInstanceInterface parent)
    {
        String tagName = cfgElement.getName();
        return getTreeFromEnvironment(tagName, ctx, parent);
    }

    public static ConfiguredAlgorithm getTreeFromEnvironment(String name,
            Context ctx, AlgorithmInstanceInterface parent)
    {
        Element evnElement = ctx.getEnvironment().getAlgorithmCfg(name);
        if(null == evnElement)
        {
            // Some implementations might need additional algorithms.
            // we then have no configurations for them, but that is OK as they are "libraries"
            //as long as we find the Algorithm for this then it is OK.
            evnElement = new Element(name);
            evnElement.setAttribute(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME, name);
        }
        // else OK
        String algoName = evnElement.getAttributeValue(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
        if(null == algoName)
        {
            ctx.addError("ConfiguredAlgorithm.getTreeFromEnvironment.1",
                            "Failed to get the algorithm for " + name
                            + " from the environment (" + evnElement + ")!");
            return null;
        }

        Algorithm algo = ctx.getSolution().getAlgorithm(algoName);
        if(null == algo)
        {
            // algorithm might be a Library
            algo = Algorithm.getFromFile(evnElement, ctx);
            if(null == algo)
            {
                ctx.addError("ConfiguredAlgorithm.getTreeFromEnvironment.2",
                                "Failed to get Algorithm for " + algoName + " !");
                return null;
            }
        }
        // else OK

        ConfiguredAlgorithm res = new ConfiguredAlgorithm(name, algo, ctx, parent);

        List<Attribute> attribs = evnElement.getAttributes();
        for(int i = 0; i < attribs.size(); i++)
        {
            Attribute curAttribute = attribs.get(i);
            res.addConfiguration(curAttribute);
        }

        // environment elements have no children !

        if(false == res.allRequiredDataAvailable())
        {
            ctx.addError("ConfiguredAlgorithm.getTreeFromEnvironment.3",
                            "Data missing for " + res.toString() + " !");
            return null;
        }
        else
        {
            return res;
        }
    }

    private static ConfiguredAlgorithm getTreeFor(Element cfgElement,
            Context ctx, AlgorithmInstanceInterface parent)
    {
        String algoName = cfgElement.getAttributeValue(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
        if(null == algoName)
        {
            // this child is not an Algorithm, but something provided by the Environment!
            // -> So get configuration from the Environment
            return getTreeFromEnvironment(cfgElement, ctx, parent);
        }
        // else get tree from this element

        Algorithm algo = ctx.getSolution().getAlgorithm(algoName);
        if(null == algo)
        {
            ctx.addError("ConfiguredAlgorithm.getTreeFor",
                            "Failed to get Algorithm for " + algoName + " !");
            return null;
        }
        // else OK

        ConfiguredAlgorithm res = new ConfiguredAlgorithm(cfgElement.getName(), algo, ctx, parent);

        List<Attribute> attribs = cfgElement.getAttributes();
        for(int i = 0; i < attribs.size(); i++)
        {
            Attribute curAttribute = attribs.get(i);
            res.addConfiguration(curAttribute);
        }

        List<Element> children = cfgElement.getChildren();
        for(int i = 0; i < children.size(); i++)
        {
            Element nextAlgo = children.get(i);
            // !!! recursion !!!
            ConfiguredAlgorithm nextCfgAlgo = getTreeFor(nextAlgo, ctx, res);
            if(null == nextCfgAlgo)
            {
                ctx.addError("ConfiguredAlgorithm.getTreeFor",
                                "Failed Tree resolve for " + nextAlgo.getName() + " !");
                return null;
            }
            res.addChild(nextCfgAlgo);
        }

        if(false == res.allRequiredDataAvailable())
        {
            ctx.addError("ConfiguredAlgorithm.getTreeFor",
                            "Data missing for " + res.toString() + " !");
            return null;
        }
        else
        {
            return res;
        }
    }

    private boolean checkRequiredConfigurationParametersAvailable(Element requirements)
    {
        List<Element> cfgReq = requirements.getChildren(REQUIRED_CFG_NAME);
        LOG.trace("{} has {} required parameters.", algorithmDefinition, cfgReq.size());
        for(int i = 0; i < cfgReq.size(); i++)
        {
            Element curE = cfgReq.get(i);
            String attrName = curE.getAttributeValue(REQUIRED_CFG_ATTRIBUTE_NAME);
            if(null == attrName)
            {
                LOG.error("Missing attribute in {}", curE);
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
        LOG.trace("{} has {} required children.", algorithmDefinition, algoReq.size());
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
            LOG.trace("required API for child {} is '{}'", i, reqApi);
            boolean found = false;
            Iterator<String> it = getAllChildren();
            while(it.hasNext())
            {
                AlgorithmInstanceInterface curAlgo = cfgHandler.getAlgorithm(it.next());
                LOG.trace("checking child {}", curAlgo);
                if(true == curAlgo.hasApi(reqApi))
                {
                    found = true;
                    break; // the while
                }
                else
                {
                    LOG.trace("API {} not found in {}.",reqApi,  curAlgo.getApis());
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

    private boolean allRequiredDataAvailable()
    {
        addProvidedData();

        Element requirements = algorithmDefinition.getChild(ALGORITHM_REQUIREMENTS_CHILD_NAME);
        if(null == requirements)
        {
            LOG.trace("{} has no requirements!", algorithmDefinition);
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
        Element provides = algorithmDefinition.getChild(ALGORITHM_PROVIDES_CHILD_NAME);
        if(null != provides)
        {
            // this algorithm provides some informations
            List<Element> data = provides.getChildren();
            for(int i = 0; i < data.size(); i++)
            {
                Element curE = data.get(i);
                String propertyName = curE.getName();
                String propertyValue = curE.getAttributeValue(ALGORITHM_PROVIDES_PROPERTY_VALUE);
                LOG.trace("Property {} : {}", propertyName, propertyValue);
                // evaluate PropertyValue
                CondEvalResult res = ConditionEvaluator.evaluateConditionParenthesis(propertyValue, this, null, null);
                if(true == res.isValid())
                {
                    propertyValue = res.getResult();
                    LOG.trace("Property {} evaluated to {}", propertyName, propertyValue);
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

}
