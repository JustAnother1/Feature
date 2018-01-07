package de.nomagic.puzzler.FileGroup;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import de.nomagic.puzzler.solution.Function;

public class FunctionHandler extends ElementHandler
{
    public final static int TYPE_DECLARATION = 0;
    public final static int TYPE_IMPLEMENTATION = 1;

    public FunctionHandler()
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
        Function first = (Function)it.next(); // we just checked that it is not empty, so this should work.
        while(it.hasNext())
        {
            Function next = (Function)it.next();
            if(true == first.getName().equals(next.getName()))
            {
                if(false == first.sameAs(next))
                {
                    // TODO
                    System.out.println("! ! ! ERROR ! ! !");
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
