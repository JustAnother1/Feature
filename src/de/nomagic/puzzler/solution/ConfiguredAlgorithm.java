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
import de.nomagic.puzzler.Project;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.C_File;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;

public class ConfiguredAlgorithm extends Base implements AlgorithmInformaton
{
    public final static String REQUIRED_CFG_NAME = "parameter";
    public final static String REQUIRED_CFG_ATTRIBUTE_NAME = "ref";
    public final static String REQUIRED_ALGORITHM_NAME = "childElement";
    public final static String REQUIRED_ALGORITHM_ATTRIBUTE_NAME = "type";
    public final static String REQUIRED_ROOT_API = "program_entry_point";

    public final static String IMPLEMENTATION_PLACEHOLDER_REGEX = "\\$\\$\\$";

    public final static String ALGORITHM_C_CODE_CHILD_NAME = "c_code";
    public final static String ALGORITHM_IF_CHILD_NAME = "if";
    public final static String ALGORITHM_FUNCTION_CHILD_NAME = "function";
    public final static String ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME = "name";
    public final static String ALGORITHM_ADDITIONAL_C_CODE_CHILD_NAME = "additional";
    public final static String ALGORITHM_ADDITIONAL_INCLUDE_CHILD_NAME = "include";
    public final static String ALGORITHM_ADDITIONAL_FUNCTION_CHILD_NAME = "function";
    public final static String ALGORITHM_REQUIREMENTS_CHILD_NAME = "required";
    public final static String ALGORITHM_PROVIDES_CHILD_NAME = "provides";
    public final static String ALGORITHM_PROVIDES_PROPERTY_VALUE = "value";

    private final static Logger LOG = LoggerFactory.getLogger("ConfiguredAlgorithm");

    private final String Name;
    private final Algorithm AlgorithmDefinition;
    private final HashMap<String, Attribute> cfgAttributes = new HashMap<String, Attribute>();
    private final HashMap<String, ConfiguredAlgorithm> cfgAlgorithms = new HashMap<String, ConfiguredAlgorithm>();
    private FileGroup libFiles = null;

    private ConditionEvaluator condiEval;
    private HashMap<String, String> properties = new  HashMap<String, String>();  // TODO
    private HashMap<String, String> parameters = new  HashMap<String, String>(); // TODO
    private Element cCode = null; // TODO
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
        Solution s = ctx.getSolution();
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
        root = root.getChildren().get(0);
        if(null == root)
        {
            ctx.addError("ConfiguredAlgorithm.getTree",
                            "No algorithm elements in the provided solution !");
            return null;
        }
        // root is now the configElement -> get Algorithm to determine the API
        String algoName = root.getAttributeValue(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
        Algorithm algo = s.getAlgorithm(algoName);
        if(null == algo)
        {
            ctx.addError("ConfiguredAlgorithm.getTree",
                            "Failed to get Algorithm for " + algoName + " !");
            return null;
        }
        if(false == algo.hasApi(REQUIRED_ROOT_API))
        {
            LOG.trace("algo: {}", algo);
            LOG.trace("root: {}", root);
            ctx.addError("ConfiguredAlgorithm.getTree",
                            "Root element of the solution is not an program entry point !");
            return null;
        }

        return getTreeFor(root, ctx, parent);
    }

    private static ConfiguredAlgorithm getTreeFromEnvironment(Element cfgElement, Context ctx, ConfiguredAlgorithm parent)
    {
        String tagName = cfgElement.getName();
        Element evnElement = ctx.getEnvironment().getAlgorithmCfg(tagName);
        if(null == evnElement)
        {
            ctx.addError("ConfiguredAlgorithm.getTree",
                            "Failed to get Configuration for " + tagName + " from the environment !");
            return null;
        }
        // else OK
        String algoName = evnElement.getAttributeValue(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME);
        if(null == algoName)
        {
            ctx.addError("ConfiguredAlgorithm.getTree",
                            "Failed to get the algorithm for " + tagName
                            + " from the environment (" + evnElement + ")!");
            return null;
        }

        Algorithm algo = ctx.getSolution().getAlgorithm(algoName);
        if(null == algo)
        {
            ctx.addError("ConfiguredAlgorithm.getTree",
                            "Failed to get Algorithm for " + algoName + " !");
            return null;
        }
        // else OK

        ConfiguredAlgorithm res = new ConfiguredAlgorithm(cfgElement.getName(), algo, ctx, parent);

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
            res.addAlgorithm(nextCfgAlgo);
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
        LOG.trace("{} required parameters.", cfgReq.size());
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
        LOG.trace("{} required children.", algoReq.size());
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
            Iterator<String> it = cfgAlgorithms.keySet().iterator();
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
                                "required child element of type " + reqApi + " not present !");
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
                // TODO evaluate PropertyValue
                properties.put(propertyName, propertyValue);
            }
        }
        // else this algorithm provides nothing and that might be OK.
    }

    private boolean hasApi(String Api)
    {
        return AlgorithmDefinition.hasApi(Api);
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

    public void addAlgorithm(ConfiguredAlgorithm algo)
    {
        cfgAlgorithms.put(algo.getName(), algo);
    }

    public FileGroup getCImplementationInto(FileGroup codeGroup)
    {
        // we will need at least one *.c file. So create that now.
        C_File mainC = new C_File("main.c");

        // there should be a file comment explaining what this is
        mainC.addLines(C_File.C_FILE_FILE_COMMENT_SECTION_NAME,
                       new String[] {"/*",
                                     "  automatically created main.c",
                                     "  created at: " + Tool.curentDateTime(),
                                     "  created from " + ctx.cfg().getString(Configuration.SOLUTION_FILE_CFG),
                                     "*/"});

        Api api = Api.getFromFile(REQUIRED_ROOT_API, ctx);
        addCodeToFile(mainC, api);
        FileGroup newCodeFiles = getAdditionalFiles();
        if(null != newCodeFiles)
        {
            codeGroup.addAll(newCodeFiles);
        }
        codeGroup.add(mainC);
        return codeGroup;
    }

    private void addCodeToFile(C_File codeFile, Api api)
    {
        // TODO
        // TODO parse provides
        // TODO get Implementation from XML
        Function[] funcs = api.getRequiredFunctions();
        for(int i = 0; i < funcs.length; i++)
        {
            String declaration = funcs[i].getDeclaration();
            declaration = declaration.trim();
            String implementation = getCImplementationOf(funcs[i].getName(), codeFile);
            if(null == implementation)
            {
                return;
            }
            codeFile.addLine(C_File.C_FILE_LOCAL_FUNCTION_DEFINITION_SECTION_NAME, declaration + ";");
            codeFile.addLine(C_File.C_FILE_FUNCTIONS_SECTION_NAME, declaration + "\n" + implementation);
        }
    }

    private String replacePlaceholders(String implementation, C_File codeFile)
    {
        StringBuffer res = new StringBuffer();
        String[] parts = implementation.split(IMPLEMENTATION_PLACEHOLDER_REGEX);
        for(int i = 0; i < parts.length; i++)
        {
            if(0 == i%2)
            {
                res.append(parts[i]);
            }
            else
            {
                // parts[i] is function name of child to call
                Iterator<String> it = cfgAlgorithms.keySet().iterator();
                if(false == it.hasNext())
                {
                    // We need a child to call the funtion !
                    ctx.addError("ConfiguredAlgorithm.replacePlaceholders",
                            "Function call to missing child!");
                    return null;
                }
                while(it.hasNext())
                {
                    String ChildName = it.next();
                    ConfiguredAlgorithm childAlgo = cfgAlgorithms.get(ChildName);
                    String impl = childAlgo.getCImplementationOf(parts[i], codeFile);
                    res.append(impl);
                }
            }
        }
        return res.toString();
    }

    protected String getCImplementationOf(String functionName, C_File codeFile)
    {
        String implementation = getFunctionCcode(functionName);
        if(null == implementation)
        {
            return null;
        }
        implementation = implementation.trim();
        // add additional Stuff
        addAdditionalsTo(codeFile);
        // TODO replace all place holders with configuration values
        // TODO add code from configuredAlgorithms into place holders.
        implementation = replacePlaceholders(implementation, codeFile);
        return implementation;
    }

    private FileGroup getAdditionalFiles()
    {
        return libFiles;
    }

    public void addCodeFile(AbstractFile additionalFile)
    {
        if(null == libFiles)
        {
            libFiles = new FileGroup();
        }
        libFiles.add(additionalFile);
    }

    private void findImplementation()
    {
        cCode = AlgorithmDefinition.getChild(ALGORITHM_C_CODE_CHILD_NAME);
        if(null == cCode)
        {
            // the algorithm might have the functions wrapped into if condition tags.
            List<Element> conditions = AlgorithmDefinition.getChildren(ALGORITHM_IF_CHILD_NAME);
            if(null == conditions)
            {
                ctx.addError("ConfiguredAlgorithm.findImplementation",
                        "No Implementation found!");
                return;
            }
            else
            {
                Element best = condiEval.getBest(conditions, this);  // TODO
                if(null == best)
                {
                    ctx.addError("ConfiguredAlgorithm.findImplementation",
                            "no valid condition!");
                    ctx.addError("ConditionEvaluation", this.toString());
                    ctx.addError("ConditionEvaluation", this.dumpParameter());
                    ctx.addError("ConditionEvaluation", this.dumpProperty());
                    return;
                }
                cCode = best.getChild(ALGORITHM_C_CODE_CHILD_NAME);
                if(null == cCode)
                {
                    ctx.addError("ConfiguredAlgorithm.findImplementation",
                            "Valid condition(" + best.toString() + ") did not have an Implementation!");
                    return;
                }
            }
        }
    }

    public String getFunctionCcode(String functionName)
    {
        if(null == cCode)
        {
            findImplementation();
            if(null == cCode)
            {
                ctx.addError("ConfiguredAlgorithm.getFunctionCcode",
                        "No implementation available !");
                return null;
            }
        }
        List<Element> funcs = cCode.getChildren(ALGORITHM_FUNCTION_CHILD_NAME);
        String searchedFunctionName = null;
        if(true == functionName.contains("("))
        {
            searchedFunctionName = functionName.substring(0, functionName.indexOf('('));
        }
        else
        {
            searchedFunctionName = functionName;
        }
        if(null == searchedFunctionName)
        {
            ctx.addError("ConfiguredAlgorithm.getFunctionCcode",
                    "Function call to unknown function!");
            return null;
        }
        if(1 > searchedFunctionName.length())
        {
            ctx.addError("ConfiguredAlgorithm.getFunctionCcode",
                    "Function call to unnamed function!");
            return null;
        }
        for(int i = 0; i < funcs.size(); i++)
        {
            Element curElement = funcs.get(i);
            String name = curElement.getAttributeValue(ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME);
            if(true == searchedFunctionName.equals(name))
            {
                 List<Element> cond = curElement.getChildren(ALGORITHM_IF_CHILD_NAME);
                if((null == cond) || (true == cond.isEmpty()))
                {
                    // TODO handle parameter in function
                    return curElement.getText();
                }
                else
                {
                    Element best = condiEval.getBest(cond, this); // TODO
                    if(null == best)
                    {
                        // function not found
                        ctx.addError("ConfiguredAlgorithm.getFunctionCcode",
                                "no valid condition found!");
                        return null;
                    }
                    return best.getText();
                }
            }
        }
        // function not found
        ctx.addError("ConfiguredAlgorithm.getFunctionCcode",
                "Function call to missing function! (" + this.toString()
                        + ", function name : " + functionName + " )");
        return null;
    }

    public void addAdditionalsTo(C_File codeFile)
    {
        if(null == cCode)
        {
            findImplementation();
            if(null == cCode)
            {
                ctx.addError("ConfiguredAlgorithm.addAdditionalsTo",
                        "No implementation available !");
                return;
            }
        }
        Element additional = cCode.getChild(ALGORITHM_ADDITIONAL_C_CODE_CHILD_NAME);
        if(null == additional)
        {
            LOG.trace("no addionals for algorithm" + this.toString());
            return;
        }
        List<Element> addlist = additional.getChildren();
        if(null == addlist)
        {
            return;
        }
        for(int i = 0; i < addlist.size(); i++)
        {
            Element curElement = addlist.get(i);
            String type = curElement.getName();
            switch(type)
            {
            case ALGORITHM_ADDITIONAL_INCLUDE_CHILD_NAME:
                codeFile.addLine(C_File.C_FILE_INCLUDE_SECTION_NAME, curElement.getText());
                break;

            case ALGORITHM_ADDITIONAL_FUNCTION_CHILD_NAME:
                Function func = new Function(curElement);
                String declaration = func.getDeclaration();
                codeFile.addLine(C_File.C_FILE_LOCAL_FUNCTION_DEFINITION_SECTION_NAME,
                                 declaration + ";");
                codeFile.addLine(C_File.C_FILE_FUNCTIONS_SECTION_NAME,
                                 declaration + "\n" + curElement.getText());
                break;

            default: // ignore
                break;
            }
        }
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

}
