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
import de.nomagic.puzzler.FileGetter;
import de.nomagic.puzzler.FileGetterImpl;
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
    
    private AlgorithmInstanceInterface addAlgorithmFromElement(Element val, AlgorithmInstanceInterface parent)
    {
        if(null == val)
        {
        	return null;
        }
        AlgorithmInstanceInterface algo = ConfiguredAlgorithm.getFromFile(val, ctx, parent);
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
        	AlgorithmInstanceInterface foundChild = addAlgorithmFromElement(cur, algo);
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
    public AlgorithmInstanceInterface getAlgorithm(String name, AlgorithmInstanceInterface parent)
    {
    	AlgorithmInstanceInterface result = referencedAlgorithms.get(name); 
    	if(null == result)
    	{
            if(null == ctx)
            {
                log.error("no Context !");
                return null;
            }
    		FileGetter fg = ctx.getFileGetter();
            if(null == fg)
            {
                log.error("no File Getter !");
                return null;
            }
    		Element environmentAlgo = fg.getFromFile(name, "algorithm", FileGetterImpl.ALGORITHM_ROOT_ELEMENT_NAME);
    		log.trace("receved Element {} from filesystem !", environmentAlgo);
    		result = new ConfiguredAlgorithm(name, environmentAlgo, ctx, parent, null);
    	}
    	// else
        return result;
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
    
}
