package de.nomagic.puzzler.BuildSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Environment.Environment;

public class BuildSystemFactory
{
    private static final Logger log = LoggerFactory.getLogger("BuildSystemFactory");

    private BuildSystemFactory()
    {
        // not used
    }

    public static BuildSystemApi getBuildSystemFor(Context ctx)
    {
        if(null == ctx)
        {
            return null;
        }

        Environment e = ctx.getEnvironment();
        String BuldSystemType = e.getBuldSystemType();
        log.trace("Build System from environment : {}", BuldSystemType);
        if(true == "none".equals(BuldSystemType))
        {
            log.trace("Build System selected: no build system");
            return new NoBuildSystem();
        }

        // New Build Systems go here!

        // Default is Make
        log.trace("Build System selected: make");
        return new MakeBuildSystem(ctx);
    }

}
