package de.nomagic.puzzler.FileGroup;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class IncludeHandler extends ElementHandler
{

    public IncludeHandler()
    {
        super();
    }

    @Override
    protected List<C_Element> makeUnique()
    {
        LinkedList<C_Element> unique = new LinkedList<C_Element>();
        // remove duplicates
        Collections.sort(elements);
        Iterator<C_Element> it = elements.iterator();
        C_Include first = (C_Include)it.next(); // we just checked that it is not empty, so this should work.
        while(it.hasNext())
        {
            C_Include next = (C_Include)it.next();
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
