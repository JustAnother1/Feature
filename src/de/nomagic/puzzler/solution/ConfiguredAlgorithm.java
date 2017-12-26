package de.nomagic.puzzler.solution;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGetter;
import de.nomagic.puzzler.Project;

public class ConfiguredAlgorithm extends Base implements AlgorithmInstanceInterface
{
    public final static String REQUIRED_CFG_NAME = "parameter";
    public final static String REQUIRED_CFG_ATTRIBUTE_NAME = "ref";
    public final static String REQUIRED_ALGORITHM_NAME = "childElement";
    public final static String REQUIRED_ALGORITHM_ATTRIBUTE_NAME = "type";

    public final static String ALGORITHM_REQUIREMENTS_CHILD_NAME = "required";
    public final static String ALGORITHM_PROVIDES_CHILD_NAME = "provides";
    public final static String ALGORITHM_PROVIDES_PROPERTY_VALUE = "value";

    public final static String BUILD_IN_NUM_OF_CHILDS = "numOfChilds";

    private final static Logger LOG = LoggerFactory.getLogger("ConfiguredAlgorithm");

    private final String Name;
    private final Algorithm AlgorithmDefinition;
    private final HashMap<String, Attribute> cfgAttributes = new HashMap<String, Attribute>();
    private final HashMap<String, ConfiguredAlgorithm> cfgAlgorithms = new HashMap<String, ConfiguredAlgorithm>();


    private ConditionEvaluator condiEval;
    private HashMap<String, String> properties = new  HashMap<String, String>();
    private HashMap<String, String> parameters = new  HashMap<String, String>();
    private ConfiguredAlgorithm parent;

    public ConfiguredAlgorithm(String Name,
                               Algorithm AlgorithmDefinition,
                               Context ctx,
                               ConfiguredAlgorithm parent)
    {
        super(ctx);
        this.Name = Name;
        this.AlgorithmDefinition = AlgorithmDefinition;
        this.parent = parent;
        condiEval = new ConditionEvaluator(ctx);
    }

    public String getName()
    {
        return Name;
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
            ctx.addError("ConfiguredAlgorithm.getTree",
                            "No root element in the provided solution !");
            return null;
        }
        if(false == Project.SOLUTION_ELEMENT_NAME.equals(root.getName()))
        {
            ctx.addError("ConfiguredAlgorithm.getTree",
                            "invalid root tag (" + root.getName() + ") !");
            return null;
        }
        List<Element> children = root.getChildren();
        if(0 == children.size())
        {
            ctx.addError("ConfiguredAlgorithm.getTree",
                    "No algorithm elements in the provided solution !");
            return null;
        }
        Element configElement = children.get(0);
        // get Algorithm to determine the API
        String algoName = configElement.getAttributeValue(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
        Algorithm algo = s.getAlgorithm(algoName);
        if(null == algo)
        {
            ctx.addError("ConfiguredAlgorithm.getTree",
                            "Failed to get Algorithm for " + algoName + " !");
            return null;
        }
        return getTreeFor(configElement, ctx, parent);
    }

    private static ConfiguredAlgorithm getTreeFromEnvironment(Element cfgElement, Context ctx, ConfiguredAlgorithm parent)
    {
        String tagName = cfgElement.getName();
        return getTreeFromEnvironment(tagName, ctx, parent);
    }

    public static ConfiguredAlgorithm getTreeFromEnvironment(String name, Context ctx, ConfiguredAlgorithm parent)
    {
        Element evnElement = ctx.getEnvironment().getAlgorithmCfg(name);
        if(null == evnElement)
        {
            ctx.addError("ConfiguredAlgorithm.getTree",
                            "Failed to get Configuration for " + name + " from the environment !");
            return null;
        }
        // else OK
        String algoName = evnElement.getAttributeValue(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
        if(null == algoName)
        {
            ctx.addError("ConfiguredAlgorithm.getTree",
                            "Failed to get the algorithm for " + name
                            + " from the environment (" + evnElement + ")!");
            return null;
        }

        Algorithm algo = ctx.getSolution().getAlgorithm(algoName);
        if(null == algo)
        {
            // algorithm might be a Library
            FileGetter lib = new FileGetter();
            algo = Algorithm.getFromFile(evnElement, lib, ctx);
            if(null == algo)
            {
                ctx.addError("ConfiguredAlgorithm.getTree",
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
            ctx.addError("ConfiguredAlgorithm.getTree.Env",
                            "Data missing for " + res.toString() + " !");
            return null;
        }
        else
        {
            return res;
        }
    }

    private static ConfiguredAlgorithm getTreeFor(Element cfgElement, Context ctx, ConfiguredAlgorithm parent)
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

    private boolean allRequiredDataAvailable()
    {
        addProvidedData();

        Element Requirements = AlgorithmDefinition.getChild(ALGORITHM_REQUIREMENTS_CHILD_NAME);
        if(null == Requirements)
        {
            LOG.trace("{} has no requirements!", AlgorithmDefinition);
            return true;
        }
        // else :

        // required configuration parameters
        List<Element> cfgReq = Requirements.getChildren(REQUIRED_CFG_NAME);
        LOG.trace("{} has {} required parameters.", AlgorithmDefinition, cfgReq.size());
        for(int i = 0; i < cfgReq.size(); i++)
        {
            Element curE = cfgReq.get(i);
            String attrName = curE.getAttributeValue(REQUIRED_CFG_ATTRIBUTE_NAME);
            if(null == attrName)
            {
                ctx.addError("ConfiguredAlgorithm.allRequiredDataAvailable",
                                "Attribute " + REQUIRED_CFG_ATTRIBUTE_NAME
                                + " missing for required configuration !");
                return false;
            }
            Attribute at = cfgAttributes.get(attrName);
            if(null == at)
            {
                ctx.addError("ConfiguredAlgorithm.allRequiredDataAvailable",
                                "required attribute " + attrName + " has not been given !");
                StringBuffer sb = new StringBuffer();
                Iterator<String> it = cfgAttributes.keySet().iterator();
                sb.append("Given Attributes: ");
                while(it.hasNext())
                {
                    sb.append(it.next());
                    sb.append(" ");
                }
                sb.append("!");
                ctx.addError("ConfiguredAlgorithm.allRequiredDataAvailable",
                                sb.toString());
                return false;
            }
            else
            {
                parameters.put(attrName, at.getValue());
            }
        }

        // required children(algorithms)
        List<Element> algoReq = Requirements.getChildren(REQUIRED_ALGORITHM_NAME);
        LOG.trace("{} has {} required children.", AlgorithmDefinition, algoReq.size());
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
            boolean found = false;
            Iterator<String> it = getAllChildren();
            while(it.hasNext())
            {
                ConfiguredAlgorithm curAlgo = cfgAlgorithms.get(it.next());
                if(true == curAlgo.hasApi(reqApi))
                {
                    found = true;
                    break; // the while
                }
            }
            if(false == found)
            {
                ctx.addError("ConfiguredAlgorithm.allRequiredDataAvailable",
                                toString() + "required child element of type " + reqApi + " not present !");
                return false;
            }
        }

        return true;
    }

    private void addProvidedData()
    {
        Element provides = AlgorithmDefinition.getChild(ALGORITHM_PROVIDES_CHILD_NAME);
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
                propertyValue = condiEval.evaluateConditionParenthesis(propertyValue, this, null, null);
                properties.put(propertyName, propertyValue);
            }
        }
        // else this algorithm provides nothing and that might be OK.
    }

    public boolean hasApi(String Api)
    {
        if(null == AlgorithmDefinition)
        {
            return false;
        }
        else
        {
            return AlgorithmDefinition.hasApi(Api);
        }
    }

    @Override
    public String toString()
    {
        return "ConfiguredAlgorithm " + Name + "(" + AlgorithmDefinition + ")";
    }

    private void addConfiguration(Attribute curAttribute)
    {
        cfgAttributes.put(curAttribute.getName(), curAttribute);
    }

    public void addChild(ConfiguredAlgorithm algo)
    {
        if(null != algo)
        {
            cfgAlgorithms.put(algo.getName(), algo);
        }
    }

    public ConfiguredAlgorithm getChild(String Name)
    {
        return cfgAlgorithms.get(Name);
    }

    public Iterator<String> getAllChildren()
    {
        return cfgAlgorithms.keySet().iterator();
    }

    public String getProperty(String name)
    {
        String res = properties.get(name);
        if(null == res)
        {
            if(null != parent)
            {
                res = parent.getProperty(name);
            }
        }
        return res;
    }

    public String dumpProperty()
    {
        StringBuffer sb = new StringBuffer();
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

    public String getParameter(String name)
    {
        String res = parameters.get(name);
        if(null == res)
        {
            if(null != parent)
            {
                res = parent.getParameter(name);
            }
        }
        return res;
    }

    public String dumpParameter()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Parameter:\n");
        Iterator<String> keys = parameters.keySet().iterator();
        while(keys.hasNext())
        {
            String key = keys.next();
            sb.append(key + " : " + parameters.get(key) + "\n");
        }
        return sb.toString();
    }

    public String getBuildIn(String word)
    {
        // numOfChilds
        if(true == BUILD_IN_NUM_OF_CHILDS.equals(word))
        {
            return "" + cfgAlgorithms.size();
        }
        return null;
    }

    public Element getAlgorithmElement(String ElementName)
    {
        if(null == AlgorithmDefinition)
        {
            return null;
        }
        else
        {
            return AlgorithmDefinition.getChild(ElementName);
        }
    }

    public List<Element> getAlgorithmElements(String ElementName)
    {
        if(null == AlgorithmDefinition)
        {
            return null;
        }
        else
        {
            return AlgorithmDefinition.getChildren(ElementName);
        }
    }

}
