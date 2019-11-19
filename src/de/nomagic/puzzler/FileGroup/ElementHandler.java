package de.nomagic.puzzler.FileGroup;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ElementHandler
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    protected List<CElement> elements = new LinkedList<CElement>();

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

    protected abstract List<CElement> makeUnique();

    public List<String> getCode(int type, String lineSperator)
    {
        if(true == isEmpty())
        {
            return new LinkedList<String>();
        }
        List<CElement> unique = makeUnique();

        log.trace("{} unique entries!", unique.size());
        // expand to valid statements
        LinkedList<String> res = new LinkedList<String>();
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
