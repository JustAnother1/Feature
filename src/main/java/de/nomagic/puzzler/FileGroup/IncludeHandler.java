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
    protected List<CElement> makeUnique()
    {
        LinkedList<CElement> unique = new LinkedList<CElement>();
        // remove duplicates
        Collections.sort(elements);
        Iterator<CElement> it = elements.iterator();
        CInclude first = (CInclude)it.next(); // we just checked that it is not empty, so this should work.
        while(it.hasNext())
        {
            CInclude next = (CInclude)it.next();
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
