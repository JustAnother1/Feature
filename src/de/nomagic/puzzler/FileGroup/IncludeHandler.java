package de.nomagic.puzzler.FileGroup;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

public class IncludeHandler extends ElementHandler
{

    public IncludeHandler()
    {
        super();
    }

    @Override
    protected Vector<C_element> makeUnique()
    {
        Vector<C_element> unique = new Vector<C_element>();
        // remove duplicates
        Collections.sort(elements);
        Iterator<C_element> it = elements.iterator();
        C_include first = (C_include)it.next(); // we just checked that it is not empty, so this should work.
        while(it.hasNext())
        {
            C_include next = (C_include)it.next();
            if(true == first.getName().equals(next.getName()))
            {
                first.addComment(next.getComment());
            }
            else
            {
                unique.add(first);
                first = next;
            }
        }
        unique.add(first);
        return unique;
    }

}
