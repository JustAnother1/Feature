package de.nomagic.puzzler.FileGroup;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.solution.Function;

public class FunctionHandler extends ElementHandler
{
    public static final int TYPE_DECLARATION = 0;
    public static final int TYPE_IMPLEMENTATION = 1;

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public FunctionHandler()
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
        Function first = (Function)it.next(); // we just checked that it is not empty, so this should work.
        while(it.hasNext())
        {
            Function next = (Function)it.next();
            if(true == first.getName().equals(next.getName()))
            {
                if(false == first.sameAs(next))
                {
                    log.error("! ! ! ERROR ! ! !");
                }
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
