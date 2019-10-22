
package de.nomagic.puzzler.progress;

import java.util.LinkedList;

/** Reports the progress to System.out and System.err
 */
public class ProgressReport
{
    private boolean sawAnError = false;
    private LinkedList<String> reports = new LinkedList<String>();

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
            String report = "ERROR(" + name + ") : " + msg;
            reports.add(report);
            System.err.println(report);
        }
        else
        {
            String report = null;
            if(null == ref)
            {
                report = "ERROR(null) : " + msg;
            }
            else
            {
                report = "ERROR(" + ref.getClass().getName() + ") : " + msg;
            }
            reports.add(report);
            System.err.println(report);
        }
    }

    public String getAllReports()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < reports.size(); i++)
        {
            sb.append(reports.get(i));
            sb.append("\n");
        }
        return sb.toString();
    }

}
