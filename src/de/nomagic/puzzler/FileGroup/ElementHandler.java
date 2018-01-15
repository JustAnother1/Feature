package de.nomagic.puzzler.FileGroup;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ElementHandler
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    protected List<CElement> elements = new Vector<CElement>();

    public ElementHandler()
    {
    }

    public void add(CElement inc)
    {
        elements.add(inc);
    }

    public boolean isEmpty()
    {
        if(0 == elements.size())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    protected abstract Vector<CElement> makeUnique();

    public Vector<String> getCode(int type, String lineSperator)
    {
        if(true == isEmpty())
        {
            return new Vector<String>();
        }
        Vector<CElement> unique = makeUnique();

        log.trace("{} unique entries!", unique.size());
        // expand to valid statements
        Vector<String> res = new Vector<String>();
        for(int i = 0; i < unique.size(); i++)
        {
            CElement ele = unique.get(i);
            String line = ele.getCode(type, lineSperator);
            res.add(line);
        }
        return res;
    }

    public void addAll(ElementHandler eleHandler)
    {
        elements.addAll(eleHandler.elements);
    }

}
