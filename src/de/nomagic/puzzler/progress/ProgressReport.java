
package de.nomagic.puzzler.progress;

/** Reports the progress to System.out and System.err
 *
 *
 */
public class ProgressReport
{
    private boolean sawAnError = false;

    public ProgressReport()
    {
        // nothing to do here
    }

    public void close()
    {
        // nothing to do here
    }

    public boolean wasSucessful()
    {
        // nothing to do here (No news are good news!)
        if(false == sawAnError)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void addError(Object ref, String msg)
    {
        sawAnError = true;
        if(ref instanceof String)
        {
            String name = (String)ref;
            System.err.println("ERROR(" + name + ") : " + msg);
        }
        else
        {
            System.err.println("ERROR(" + ref.getClass().getName() + ") : " + msg);
        }
    }

}
