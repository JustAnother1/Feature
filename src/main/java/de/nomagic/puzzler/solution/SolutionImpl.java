package de.nomagic.puzzler.solution;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Project;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.configuration.Configuration;

public class SolutionImpl extends Base implements Solution
{
    public static final String EXTERNAL_REFFERENCE_ATTRIBUTE_NAME = "ref";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private HashMap<String, AlgorithmInstanceInterface> referencedAlgorithms = new HashMap<String,AlgorithmInstanceInterface>();
    private AlgorithmInstanceInterface rootAlgorithm = null;


    public SolutionImpl(Context ctx)
    {
        super(ctx);
    }
    
    private Element getSolutionRoot(Project pro)
    {
        if(null == pro)
        {
            ctx.addError(this, "No Project provided !");
            return null;
        }
        Element solutionRoot = pro.getSolutionElement();
        if(null == solutionRoot)
        {
            ctx.addError(this, "No Solution Tag in Project !");
            return null;
        }

        if(true == solutionRoot.hasAttributes())
        {
            Attribute attr = solutionRoot.getAttribute(EXTERNAL_REFFERENCE_ATTRIBUTE_NAME);
            if(null != attr)
            {
                String externalReferenceFileName = attr.getValue();
                if(null == externalReferenceFileName)
                {
                    ctx.addError(this, "Invalid external reference !");
                    return null;
                }
                // read external Reference
                Document externalReferenceDocument = ctx.getFileGetter().getXmlFile(ctx.cfg().getString(Configuration.SOLUTION_PATH_CFG),
                                                                  externalReferenceFileName);
                if(null == externalReferenceDocument)
                {
                    ctx.addError(this, "Could not read referenced File : " + externalReferenceFileName);
                    return null;
                }

                Element extRefEleemnt  = externalReferenceDocument.getRootElement();
                if(null == extRefEleemnt)
                {
                    ctx.addError(this, "Could not read Root Element from : " + externalReferenceFileName);
                    return null;
                }

                if(false == Project.SOLUTION_ELEMENT_NAME.equals(extRefEleemnt.getName()))
                {
                    ctx.addError(this, "Solution File '" + externalReferenceFileName
                            + "' has an invalid root tag (" + extRefEleemnt.getName() + ") !");
                    return null;
                }
                solutionRoot = extRefEleemnt;
                ctx.cfg().setString(Configuration.SOLUTION_FILE_CFG, externalReferenceFileName);
            }
            else
            {
                // no external Reference - all data in this node
                ctx.cfg().setString(Configuration.SOLUTION_FILE_CFG, ctx.cfg().getString(Configuration.PROJECT_FILE_CFG));
            }
        }
        else
        {
            // no external Reference - all data in this node
            ctx.cfg().setString(Configuration.SOLUTION_FILE_CFG, ctx.cfg().getString(Configuration.PROJECT_FILE_CFG));
        }
        return solutionRoot;
    }
    
    private ConfiguredAlgorithm addAlgorithmFromElement(Element val, ConfiguredAlgorithm parent)
    {
        if(null == val)
        {
        	return null;
        }
        ConfiguredAlgorithm algo = ConfiguredAlgorithm.getFromFile(val, ctx, parent);
        if(null == algo)
        {
        	// could be something provided by the environment.
        	Environment e = ctx.getEnvironment();
        	if(null == e)
        	{
                log.error("no Environment !");
                return null;
        	}
        	Element environmentAlgo = e.getAlgorithmCfg(val.getName());
        	algo = ConfiguredAlgorithm.getFromFile(environmentAlgo, ctx, parent);
        }
        if(null == algo)
        {
        	log.error("Could not geth the Algorithm {} !", val.getName());
        }
        else
        {
        	referencedAlgorithms.put(algo.getName(), algo);
        }
        List<Element> childs = val.getChildren();
        for(int i = 0; i < childs.size(); i++)
        {
        	Element cur = childs.get(i);
        	// !!! recursion !!!
        	ConfiguredAlgorithm foundChild = addAlgorithmFromElement(cur, algo);
        	if(null != algo)
        	{
        		algo.addChild(foundChild);
        	}
        }
        return algo;
    }

    @Override
    public boolean getFromProject(Project pro)
    {
        if(null == ctx)
        {
            log.error("no Context !");
            return false;
        }
        Element solutionRoot = getSolutionRoot(pro);
        if(null == solutionRoot)
        {
            log.error("no Solution !");
            return false;
        }
        List<Element> rootElements = solutionRoot.getChildren();
        for(int i = 0; i < rootElements.size(); i++)
        {
        	Element cur = rootElements.get(i);
        	rootAlgorithm = addAlgorithmFromElement(cur, null);
        	if(null != rootAlgorithm)
        	{
        		return true;
        	}
        	else
        	{
        		log.trace("Could not get Algorithm from {} !", cur);
        	}
        }
    	log.error("Could not get Algorithm from {} !", solutionRoot);
    	return false;
    }

    @Override
    public boolean checkAndTestAgainstEnvironment()
    {
        if(null == ctx)
        {
            log.error("no Context !");
            return false;
        }
        
        if(null == ctx.getEnvironment())
        {
            ctx.addError(this, "No Environment provided !");
            return false;
        }
        
        if(1 > referencedAlgorithms.size())
        {
            ctx.addError(this, "No Algorithms found !");
            return false;
        }
        
    	Collection<AlgorithmInstanceInterface> all = referencedAlgorithms.values();
    	Iterator<AlgorithmInstanceInterface> it = all.iterator();
    	while(it.hasNext())
    	{
    		AlgorithmInstanceInterface cur = it.next();
    		
            if(false == cur.allRequiredDataAvailable())
            {
                ctx.addError(this, "Data missing for " + cur.toString() + " !");
                return false;
            }
    	}
    	
        // additional tests go here
        return true;
    }
    
    @Override
    public AlgorithmInstanceInterface getAlgorithm(String name)
    {
        return referencedAlgorithms.get(name);
    }

    @Override
	public boolean treeContainsElement(String TagName)
	{
    	Collection<AlgorithmInstanceInterface> all = referencedAlgorithms.values();
    	Iterator<AlgorithmInstanceInterface> it = all.iterator();
    	while(it.hasNext())
    	{
    		AlgorithmInstanceInterface cur = it.next();
    		if(true == cur.containsElement(TagName))
    		{
    			return true;
    		}
    	}
		return false;
	}
	
    @Override
	public AlgorithmInstanceInterface getRootAlgorithm()
	{
    	return rootAlgorithm;
	}
    
    /*
        if(null == solutionRoot)
        {
            ctx.addError(this, "No Solution Tag in Project !");
            return null;
        }
        
        if(false == Project.SOLUTION_ELEMENT_NAME.equals(solutionRoot.getName()))
        {
            ctx.addError("ConfiguredAlgorithm.getTreeFrom.1",
                            "invalid root tag (" + solutionRoot.getName() + ") !");
            return null;
        }
        List<Element> children = solutionRoot.getChildren();
        if(true == children.isEmpty())
        {
            ctx.addError("ConfiguredAlgorithm.getTreeFrom.2",
                    "No algorithm elements in the provided solution !");
            return null;
        }
        Element configElement = children.get(0);
        // get Algorithm to determine the API        
        String algoName = ConfiguredAlgorithm.getAlgorithmNameFromElement(configElement);        
        AlgorithmInstanceInterface algo = getAlgorithm(algoName);
        if(null == algo)
        {
            ctx.addError("ConfiguredAlgorithm.getTreeFrom.3",
                            "Failed to get Algorithm for " + algoName + " !");
            return null;
        }
        return getTreeFor(configElement, ctx, null);
    }*/

    /*
    private boolean getAlgorithmForElement(Element cfgElement)
    {
    	String algoName = ConfiguredAlgorithm.getAlgorithmNameFromElement(cfgElement);   
        if(null == algoName)
        {
            // no reference to algorithm -> Failure !
            log.trace("Element '{}' does not have an algorithm attribute!", cfgElement.getName());
            return false;
        }
        else
        {
            // load Algorithm
        	AlgorithmInstanceInterface algo = AlgorithmInstanceInterface.getFromFile(cfgElement, ctx);

            if(null == algo)
            {
                return false;
            }
            else
            {
                referencedAlgorithms.put(algoName, algo);
            }
        }
        return true;
    }
    */
    
    /*
    private static ConfiguredAlgorithm getTreeFor(Element cfgElement,
            Context ctx, AlgorithmInstanceInterface parent)
    {        
        String algoName = ConfiguredAlgorithm.getAlgorithmNameFromElement(cfgElement);
        if(null == algoName)
        {
            // this child is not an Algorithm, but something provided by the Environment!
            // -> So get configuration from the Environment
            // TODO return getTreeFromEnvironment(cfgElement, ctx, parent);
        }
        // else get tree from this element

        AlgorithmInstanceInterface algo = ctx.getSolution().getAlgorithm(algoName);
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
    
    private ConfiguredAlgorithm getTreeFromEnvironment(String name,
            Context ctx, AlgorithmInstanceInterface parent)
    {
        Element evnElement = ctx.getEnvironment().getAlgorithmCfg(name);
        if(null == evnElement)
        {
            // Some implementations might need additional algorithms.
            // we then have no configurations for them, but that is OK as they are "libraries"
            //as long as we find the Algorithm for this then it is OK.
            evnElement = new Element(name);            
            ConfiguredAlgorithm.setAlgorithmNameInElement(evnElement, name); 
        }
        // else OK
        
        String algoName = ConfiguredAlgorithm.getAlgorithmNameFromElement(evnElement);        
        if(null == algoName)
        {
            ctx.addError("ConfiguredAlgorithm.getTreeFromEnvironment.1",
                            "Failed to get the algorithm for " + name
                            + " from the environment (" + evnElement + ")!");
            return null;
        }

        AlgorithmInstanceInterface algo = ctx.getSolution().getAlgorithm(algoName);
        if(null == algo)
        {
            // algorithm might be a Library
            algo = AlgorithmInstanceInterface.getFromFile(evnElement, ctx);
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
    }*/
    
    
    /*
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


    */
    
    /*
    private String fillInFunctionCallFromLibrary(C_FunctionCall libfc, AlgorithmInstanceInterface algo)
    {
        // include the library
    	FileGetter fg = ctx.getFileGetter();
    	
        xxxx AlgorithmInstanceInterface libAlgo = ConfiguredAlgorithm.getTreeFromEnvironment(libfc.getApi(), ctx, algo);
        if(null == libAlgo)
        {
            ctx.addError(this, "" + algo +
                    " : The Environment does not provide the needed library (" + libfc.getApi() + ") !");
            ctx.addError(this, "" + algo +
                    " : We needed to call the function " + libfc.getName() + " !");
            return null;
        }
        log.trace("adding the library algorithm {}", libAlgo.getName());
        extraAlgoList.add(libAlgo);
        String impl = getImplementationOf(libfc, libAlgo);
        if(null == impl)
        {
            ctx.addError(this, "" + algo +
                    " : Function call to missing (lib) function (" + libfc.getName() + ") !");
            return null;
        }
        return impl;
    }
    */
    
}
