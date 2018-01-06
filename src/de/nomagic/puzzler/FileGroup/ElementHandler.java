package de.nomagic.puzzler.FileGroup;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ElementHandler
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    protected List<C_element> elements = new Vector<C_element>();

    public ElementHandler()
    {
    }

    public void add(C_element inc)
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

    protected abstract Vector<C_element> makeUnique();

    public Vector<String> getCode(int type, String lineSperator)
    {
        if(true == isEmpty())
        {
            return new Vector<String>();
        }
        Vector<C_element> unique = makeUnique();

        log.trace("{} unique entries!", unique.size());
        // expand to valid statements
        Vector<String> res = new Vector<String>();
        for(int i = 0; i < unique.size(); i++)
        {
            C_element ele = unique.get(i);
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
