package de.nomagic.puzzler.solution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Content.CType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Project;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.Generator.CCodeGenerator;
import de.nomagic.puzzler.Generator.CFunctionCall;
import de.nomagic.puzzler.Generator.FunctionCall;
import de.nomagic.puzzler.Generator.Generator;
import de.nomagic.puzzler.configuration.Configuration;

public class ConfiguredAlgorithm extends Base implements AlgorithmInstanceInterface
{
    public static final String REQUIRED_CFG_NAME = "parameter";
    public static final String REQUIRED_CFG_ATTRIBUTE_NAME = "name";
    public static final String REQUIRED_ALGORITHM_NAME = "childElement";
    public static final String REQUIRED_ALGORITHM_ATTRIBUTE_NAME = "type";

    public static final String ALGORITHM_REQUIREMENTS_CHILD_NAME = "required";
    public static final String ALGORITHM_PROVIDES_CHILD_NAME = "provides";
    public static final String ALGORITHM_PROVIDES_PROPERTY_VALUE = "value";

    public static final String ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME = "name";

    public static final String ALGORITHM_IF_CHILD_NAME = "if";
    public static final String ALGORITHM_FOR_CHILDS_CHILD_NAME = "forChilds";
    public static final String ALGORITHM_FOR_CHILDS_CHILD_API_ATTRIBUTE = "api";
    public static final String ALGORITHM_FOR_CHILDS_CHILD_CALL_ATTRIBUTE = "call";
    public static final String IMPLEMENTATION_PLACEHOLDER_REGEX = "€";

    public static final String BUILD_IN_NUM_OF_CHILDS = "numOfChilds";

    private static final Logger LOG = LoggerFactory.getLogger("ConfiguredAlgorithm");

    private final String name;
    private final Algorithm algorithmDefinition;
    private final HashMap<String, Attribute> cfgAttributes = new HashMap<String, Attribute>();
    private final HashMap<String, ConfiguredAlgorithm> cfgAlgorithms = new HashMap<String, ConfiguredAlgorithm>();


    private ConditionEvaluator condiEval;
    private HashMap<String, String> properties = new  HashMap<String, String>();
    private HashMap<String, String> parameters = new  HashMap<String, String>();
    private AlgorithmInstanceInterface parent;

    // if this is true then all code snippet will be wrapped into comment lines
    // explaining where they came from.
    private boolean documentCodeSource = false;
    private HashSet<AlgorithmInstanceInterface> extraAlgoList = new HashSet<AlgorithmInstanceInterface>();




    public ConfiguredAlgorithm(String name,
                               Algorithm algorithmDefinition,
                               Context ctx,
                               AlgorithmInstanceInterface parent)
    {
        super(ctx);
        this.name = name;
        this.algorithmDefinition = algorithmDefinition;
        this.parent = parent;
        condiEval = new ConditionEvaluator(ctx);
        if("true".equals(ctx.cfg().getString(Configuration.CFG_DOC_CODE_SRC)))
        {
            LOG.trace("Switching on documentation of source code");
            documentCodeSource = true;
        }
        extraAlgoList.add(this);
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

    private static ConfiguredAlgorithm getTreeFromEnvironment(Element cfgElement, Context ctx, AlgorithmInstanceInterface parent)
    {
        String tagName = cfgElement.getName();
        return getTreeFromEnvironment(tagName, ctx, parent);
    }

    public static ConfiguredAlgorithm getTreeFromEnvironment(String name, Context ctx, AlgorithmInstanceInterface parent)
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

    private static ConfiguredAlgorithm getTreeFor(Element cfgElement, Context ctx, AlgorithmInstanceInterface parent)
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
                LOG.error("Missing attribute in " + curE.toString());
                ctx.addError("ConfiguredAlgorithm.allRequiredDataAvailable",
                                "Attribute \"" + REQUIRED_CFG_ATTRIBUTE_NAME
                                + "\" missing for required configuration !");
                return false;
            }
            Attribute at = cfgAttributes.get(attrName);
            if(null == at)
            {
                ctx.addError("ConfiguredAlgorithm.allRequiredDataAvailable",
                                "required attribute " + attrName + " has not been given !");
                StringBuilder sb = new StringBuilder();
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
                ConfiguredAlgorithm curAlgo = cfgAlgorithms.get(it.next());
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
                propertyValue = condiEval.evaluateConditionParenthesis(propertyValue, this, null, null);
                LOG.trace("Property {} evaluated to {}", propertyName, propertyValue);
                properties.put(propertyName, propertyValue);
            }
        }
        // else this algorithm provides nothing and that might be OK.
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
        cfgAttributes.put(curAttribute.getName(), curAttribute);
    }

    public void addChild(ConfiguredAlgorithm algo)
    {
        if(null != algo)
        {
            cfgAlgorithms.put(algo.getName(), algo);
        }
    }

    public ConfiguredAlgorithm getChild(String name)
    {
        return cfgAlgorithms.get(name);
    }

    public Iterator<String> getAllChildren()
    {
        return cfgAlgorithms.keySet().iterator();
    }

    public String getProperty(String name)
    {
        String res = properties.get(name);
        if((null == res) && (null != parent))
        {
            res = parent.getProperty(name);
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

    public String getParameter(String name)
    {
        String res = parameters.get(name);
        if((null == res) && (null != parent))
        {
            res = parent.getParameter(name);
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

    public String getBuildIn(String word)
    {
        // numOfChilds
        if(true == BUILD_IN_NUM_OF_CHILDS.equals(word))
        {
            return "" + cfgAlgorithms.size();
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
    public String getImplementationOf(FunctionCall fc)
    {
        if(fc instanceof CFunctionCall)
        {
            CFunctionCall functionToCall = (CFunctionCall)fc;
            LOG.trace("getting the C implemention of the function {} from {}",
                    functionToCall, this);

            String searchedFunctionName = functionToCall.getName();
            if(null == searchedFunctionName)
            {
                ctx.addError(this, "" + this + " : Function call to unknown function!");
                return null;
            }
            if(1 > searchedFunctionName.length())
            {
                ctx.addError(this, "" + this + " : Function call to unnamed function!");
                return null;
            }

            String api = functionToCall.getApi();
            if(null != api)
            {
                if(false == this.hasApi(api))
                {
                    LOG.warn("{} : Function call to wrong API!(API: {})", this, api);
                    LOG.warn("valid APIs : {}",  this.getApis());
                    LOG.warn("Function called: {}",  functionToCall.getName());
                    return null;
                }
            }
            // else API unknown -> can not check

            if(false == ctx.wasSucessful())
            {
                return null;
            }

            functionToCall.setFunctionArguments(functionToCall.getArguments());

            Element functionElement = getFunctionElement(searchedFunctionName, functionToCall.getArguments());
            if(null == functionElement)
            {
                return null;
            }
            String implementation = getImplementationFromFunctionElement(functionElement, functionToCall.getArguments());

            if(null == implementation)
            {
                return null;
            }

            implementation = replacePlaceholders(implementation, functionToCall.getArguments(), this, functionElement);
            if(false == ctx.wasSucessful())
            {
                return null;
            }
            else
            {
                if(true == documentCodeSource)
                {
                    return "// from " + toString() + System.getProperty("line.separator")
                           + implementation   // Implementation always comes with a line end
                           + "// end of " + toString() + System.getProperty("line.separator");
                }
                else
                {
                    return implementation;
                }
            }
        }
        // new languages go here
        else
        {
            LOG.error("unknown Function Call Class -> unsupported language!");
            return null;
        }
    }

    private String getImplementationFromFunctionElement(Element function, String FunctionArguments)
    {
        // some part of the Implementation might be conditional, So only select the valid parts
        StringBuilder sb = new StringBuilder();
        List<Content> parts = function.getContent();
        for (int i = 0; i < parts.size() ; i++)
        {
            Content curC = parts.get(i);
            if(CType.Element == curC.getCType())
            {
                Element curE = (Element)curC;
                if(ALGORITHM_IF_CHILD_NAME.equals(curE.getName()))
                {
                    // this part is conditional -> check if we need it.
                    Element active = condiEval.getBest(curE, this, FunctionArguments, function);
                    if(null != active)
                    {
                        String impl = active.getText();
                        LOG.trace("adding the conditioned parts to the implementation : {}", impl);
                        sb.append(impl);
                        if(false == impl.endsWith("\n"))
                        {
                            sb.append("\n");
                        }
                    }
                    else
                    {
                        LOG.trace("The condition {} is not true", curE.getAttribute(ConditionEvaluator.CONDITION_EVALUATOR_CONDITION_ATTRIBUTE_NAME));
                    }
                    // else this part is not used this time.
                }
                else  if(ALGORITHM_FOR_CHILDS_CHILD_NAME.equals(curE.getName()))
                {
                    String api = curE.getAttributeValue(ALGORITHM_FOR_CHILDS_CHILD_API_ATTRIBUTE);
                    if(null == api)
                    {
                        ctx.addError(this, "" + this + " : for childs element with missing api attribute !)");
                        return null;
                    }
                    Api theApi = Api.getFromFile(api, ctx);
                    if(null == theApi)
                    {
                        ctx.addError(this, "" + this + " : for childs element with invalid api attribute ! (" + api + "))");
                        return null;
                    }
                    String FuncToCall = curE.getAttributeValue(ALGORITHM_FOR_CHILDS_CHILD_CALL_ATTRIBUTE);
                    if(null == FuncToCall)
                    {
                        // if the API has only one function then we can call that
                        if(1 == theApi.getNumberOfFunctions())
                        {
                            Function first = theApi.getFunctionIndex(0);
                            FuncToCall = first.getName();
                            LOG.trace("Element: {}",Tool.getXMLRepresentationFor(curE));
                            LOG.trace("{} : No function to call specified, but API({}) has only one function, so taking that.", this, api);
                        }
                        else
                        {
                            ctx.addError(this, "" + this + " : the API (" + api + ") has more than one Function. You must specify the function to call!)");
                            return null;
                        }
                    }
                    Iterator<String> it = this.getAllChildren();
                    while(it.hasNext())
                    {
                        String childName = it.next();
                        AlgorithmInstanceInterface curChild = this.getChild(childName);
                        if(true == curChild.hasApi(api))
                        {
                            CFunctionCall fc = new CFunctionCall(FuncToCall);
                            String implementation = curChild.getImplementationOf(fc);
                            if(null == implementation)
                            {
                                String error = "Could not get an Implementation for " + FuncToCall;
                                LOG.error(error);
                                ctx.addError(this, error);
                                return null;
                            }
                            else
                            {
                                implementation = implementation + "\n";
                                extraAlgoList.add(curChild);
                                sb.append(implementation);
                                if(false == implementation.endsWith("\n"))
                                {
                                    sb.append("\n");
                                }
                            }
                        }
                        // else don't care for that child
                    }
                }
                else
                {
                    String impl = curE.getText();
                    sb.append(impl);
                    if(false == impl.endsWith("\n"))
                    {
                        sb.append("\n");
                    }
                    LOG.warn("Adding non conditional Element data to implementation ! text:  {} element: {}", impl, curE);
                }
            }
            else
            {
                // Not an element, therefore can not have if conditions,
                // therefore we can just extract all the text.
                String impl = curC.getValue();
                String trimmed = impl.trim();
                if(0 < trimmed.length())
                {
                    sb.append(impl);
                    if(false == trimmed.endsWith("\n"))
                    {
                        sb.append("\n");
                    }
                    LOG.trace("adding non element code to implmentation: '{}' from '{}'", impl, curC);
                }
                // else whitespace in between tags
            }
        }
        return sb.toString();
    }


    private Element getFunctionElement(String searchedFunctionName, String FunctionArguments)
    {
        Element cCode = getAlgorithmElement(CCodeGenerator.ALGORITHM_C_CODE_CHILD_NAME);
        if(null == cCode)
        {
            ctx.addError(this,
                "Could not read implementation for " + searchedFunctionName +
                " from " + toString());
            return null;
        }

        List<Element> funcs = cCode.getChildren(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        LOG.trace("func.size() : {}", funcs.size());
        for(int i = 0; i < funcs.size(); i++)
        {
            // check all functions
            Element curElement = funcs.get(i);
            String curName = curElement.getAttributeValue(ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME);
            LOG.trace("func.name : {}", curName);
            if(true == searchedFunctionName.equals(curName))
            {
                // found the correct function
                return curElement;
            }
        }

        // search also additional Functions
        Element additional = cCode.getChild(Generator.ALGORITHM_ADDITIONAL_CHILD_NAME);
        if(null != additional)
        {
            List<Element> addlist = additional.getChildren();
            if(null != addlist)
            {
                for(int i = 0; i < addlist.size(); i++)
                {
                    Element curElement = addlist.get(i);
                    String type = curElement.getName();
                    if(type ==  Generator.ALGORITHM_FUNCTION_CHILD_NAME)
                    {
                        String curName = curElement.getAttributeValue(ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME);
                        LOG.trace("func.name : {}", curName);
                        if(true == searchedFunctionName.equals(curName))
                        {
                            // found the correct function
                            return curElement;
                        }
                    }
                    // else -> ignore
                }
            }
        }

        // function not found
        ctx.addError(this, "Function call to missing function! (" + this
                        + ", function name : " + searchedFunctionName + " )");
        return null;
    }

    @Override
    public Iterable<AlgorithmInstanceInterface> getAdditionals()
    {
        return extraAlgoList;
    }

    private String replacePlaceholders(String implementation,
            String FunctionParameters,
            AlgorithmInstanceInterface logic,
            Element functionElement)
    {
        int numEuros = 0;
        int numOpenBraces = 0;
        int numClosingBaces = 0;

        if(null == implementation)
        {
            ctx.addError(this,"Implementation is null ! ");
            return null;
        }
        if(null == FunctionParameters)
        {
            LOG.trace("Function Parameters are null, changing to empty String !");
            LOG.trace("Implementation is : {} !", implementation);
            FunctionParameters = "";
        }
        if(null == logic)
        {
            ctx.addError(this,"logic is null ! ");
            return null;
        }
        if(null == functionElement)
        {
            ctx.addError(this,"function element is null ! ");
            return null;
        }

        for(int i = 0; i < implementation.length(); i++)
        {
            switch(implementation.charAt(i))
            {
            case '€': numEuros++; break;
            case '(': numOpenBraces++; break;
            case ')': numClosingBaces++; break;
            default: break;
            }
        }

        if(0 == numEuros)
        {
            return implementation;
        }

        if(0 != numEuros%2)
        {
            ctx.addError(this,"Invalid Syntax: odd number of € ! ");
            return null;
        }
        if(numOpenBraces != numClosingBaces)
        {
            ctx.addError(this,"Invalid Syntax: braces don't match"
                    + " (open: " + numOpenBraces + "; close: " +  numClosingBaces + ") !");
            return null;
        }

        if(0 != numOpenBraces)
        {
            return handleBracesInImplementation(implementation, FunctionParameters, functionElement);
        }
        else
        {
            if(true == implementation.contains(IMPLEMENTATION_PLACEHOLDER_REGEX))
            {
                return replacePlaceholdersInPart(implementation, FunctionParameters, functionElement);
            }
            else
            {
                // nothing to replace
                return implementation;
            }
        }
    }

    private String handleBracesInImplementation(String implementation,
            String FunctionParameters,
            Element functionElement)
    {
        HashMap<Integer,StringBuilder> partsMap = new HashMap<Integer,StringBuilder>();
        Integer level = 0;
        StringBuilder curPart =  new StringBuilder();
        partsMap.put(level, curPart);
        for(int i = 0; i < implementation.length(); i++)
        {
            char c = implementation.charAt(i);
            switch(c)
            {
            case'(':
                partsMap.put(level, curPart);
                level++;
                curPart =  new StringBuilder();
                break;

            case')':
                String part = curPart.toString();
                if(true == part.contains(IMPLEMENTATION_PLACEHOLDER_REGEX))
                {
                    part = replacePlaceholdersInPart(part, FunctionParameters, functionElement);
                    if(null == part)
                    {
                        return null;
                    }
                }
                else
                {
                    // nothing to replace
                }
                level--;
                curPart = partsMap.get(level);
                curPart.append('(' + part + ')');
                break;

            default:
                curPart.append(c);
                break;
            }
        }
        String part = curPart.toString();
        if(true == part.contains(IMPLEMENTATION_PLACEHOLDER_REGEX))
        {
            part = replacePlaceholdersInPart(part, FunctionParameters, functionElement);
        }
        else
        {
            // nothing to replace
        }
        return part;
    }

    private String replacePlaceholdersInPart(String implementation,
            String FunctionParameters,
            Element functionElement)
    {
        StringBuilder res = new StringBuilder();
        String[] parts = implementation.split(IMPLEMENTATION_PLACEHOLDER_REGEX);
        for(int i = 0; i < parts.length; i++)
        {
            if(0 == i%2)
            {
                res.append(parts[i]);
            }
            else
            {
                if(true == parts[i].endsWith(")"))
                {
                    if(false == parts[i].contains("("))
                    {
                        ctx.addError(this,
                            "Invalid Function Name (missing open brace?) " + parts[i] );
                        return null;
                    }
                    else
                    {
                        // we found a reference to a function name
                        String functionName = parts[i];
                        String help = fillInFunctionCall(functionName);
                        if(null == help)
                        {
                            return null;
                        }
                        else
                        {
                            res.append(help);
                        }
                    }
                }
                else
                {
                    String paramValue = getFunctionParameterValue(parts[i],
                                                                  functionElement,
                                                                  FunctionParameters);
                    if(null == paramValue)
                    {
                        // Not a parameter passed in the function call,
                        // but a parameter in the algorithm configuration?
                        paramValue = this.getParameter(parts[i]);
                        if(null != paramValue)
                        {
                            LOG.trace("Found {} as value for {} in algorithm configuration.", paramValue, parts[i]);
                        }
                    }

                    if(null == paramValue)
                    {
                        ctx.addError(this,
                            "Invalid parameter requested : " + parts[i] );
                        ctx.addError(this,"available parameters: " + this.dumpParameter());
                        ctx.addError(this,"available properties: " + this.dumpProperty());
                        return null;
                    }
                    else
                    {
                        res.append(paramValue);
                    }
                }
            }
        }
        return res.toString();
    }

    private String getFunctionParameterValue(String ParameterName,
            Element functionElement,
            String FunctionParameters )
    {
        if(null == ParameterName)
        {
            LOG.error("Function parameter name is null !");
            return null;
        }
        if(null == functionElement)
        {
            LOG.error("Function element is null !");
            return null;
        }
        if(null == FunctionParameters)
        {
            LOG.error("Function parameters are null !");
            return null;
        }
        // Reference to Algorithm parameter
        // get which parameter this is (1st 2nd 3rd,..)
        int paramIndex = 0;
        do {
            Attribute attr = functionElement.getAttribute("param" + paramIndex + "_name");
            if(null == attr)
            {
                LOG.trace("Function parameter {} not found !(Algorithm configuration?)", ParameterName);
                return null;
            }
            if(true == ParameterName.equals(attr.getValue()))
            {
                break;
            }
            else
            {
                paramIndex++;
            }
        }while(true);

        // get value for that parameter from FunctionParameters
        String[] parameters = FunctionParameters.split(",");
        if(paramIndex < parameters.length)
        {
            return parameters[paramIndex];
        }
        else
        {
            ctx.addError(this, "Could not get the " + paramIndex
                + ". parameter to this function from the parameters given as "
                + FunctionParameters );
            return null;
        }
    }


    protected String fillInFunctionCall(String functionName)
    {
        LOG.trace("filling in the code for the function call to {} in {}", functionName, this);
        // we now need to make sure that that function exists an can be called.
        // we therefore need to extract the function out of the children of this algorithm
        Iterator<String> it = this.getAllChildren();
        StringBuilder res = new StringBuilder();

        CFunctionCall fc = new CFunctionCall(functionName);
        String params = fc.getArguments();
        fc.setFunctionArguments(params);

        boolean found = false;
        while(it.hasNext())
        {
            String childName = it.next();
            ConfiguredAlgorithm childAlgo = this.getChild(childName);
            if(true == childAlgo.hasApi(fc.getApi()))
            {
                String impl = childAlgo.getImplementationOf(fc);
                if(null == impl)
                {
                    continue;
                }
                else
                {
                    found = true;
                }
                extraAlgoList.add(childAlgo);
                res.append(impl);
            }
            // else this child is for something else
        }
        if(false == found)
        {
            // The Implementation was not in one of the child elements !
            // -> it can only be in the required Library Algorithms
            if(true == functionName.contains(":"))
            {
                String libAlgoName = functionName.substring(0, functionName.indexOf(':'));
                // include the library
                ConfiguredAlgorithm libAlgo = ConfiguredAlgorithm.getTreeFromEnvironment(libAlgoName, ctx, this);
                if(null == libAlgo)
                {
                    ctx.addError(this, "" + this + " : The Environment does not provide the needed library (" + libAlgoName + ") !");
                    ctx.addError(this, "" + this + " : We needed to call the function " + functionName + " !");
                    return null;
                }
                CFunctionCall libfc = new CFunctionCall(functionName);
                String impl = libAlgo.getImplementationOf(libfc);
                if(null == impl)
                {
                    ctx.addError(this, "" + this + " : Function call to missing (lib) function (" + functionName + ") !");
                    return null;
                }
                extraAlgoList.add(libAlgo);
                res.append(impl);
            }
            else
            {
                ctx.addError(this, "" + this + " : Function call to missing function (" + functionName + ") !");
                return null;
            }
        }
        return res.toString();
    }
}
