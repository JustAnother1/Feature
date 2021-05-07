
package de.nomagic.puzzler.progress;

import de.nomagic.puzzler.configuration.Configuration;

public final class ProgressReportFactory
{

    private ProgressReportFactory()
    {
    }

    public static ProgressReport getReportFor(Configuration cfg)
    {
        return new ProgressReport();
    }


}
