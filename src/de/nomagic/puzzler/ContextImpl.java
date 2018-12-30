package de.nomagic.puzzler;

import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.progress.ProgressReport;
import de.nomagic.puzzler.progress.ProgressReportFactory;
import de.nomagic.puzzler.solution.Solution;

public class ContextImpl implements Context
{
    private Configuration cfg;
    private final ProgressReport report;
    private Environment e;
    private Solution s;

    public ContextImpl(Configuration cfg)
    {
        this.cfg = cfg;
        report = ProgressReportFactory.getReportFor(cfg);
    }

    public boolean wasSucessful()
    {
        return report.wasSucessful();
    }

    public String getErrors()
    {
        if(true == wasSucessful())
        {
            return "Context has no Errors!";
        }
        else
        {
            return "Context saw theses errors:\n" + report.getAllReports();
        }
    }

    public void close()
    {
        report.close();
    }

    public void addError(Object ref, String msg)
    {
        report.addError(ref, msg);
    }

    public Configuration cfg()
    {
        if(null == cfg)
        {
            cfg = new Configuration();
        }
        return cfg;
    }

    public Environment getEnvironment()
    {
        return e;
    }

    public void addEnvironment(Environment e)
    {
        this.e = e;
    }

    public void addSolution(Solution s)
    {
        this.s = s;
    }

    public Solution getSolution()
    {
        return s;
    }

}
