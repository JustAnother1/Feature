package de.nomagic.puzzler.BuildSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.configuration.Configuration;

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
        if(null == e)
        {
            return null;
        }
        String BuldSystemType = e.getBuldSystemType();
        log.trace("Build System from environment : {}", BuldSystemType);

        if(true == "none".equals(BuldSystemType))
        {
            log.trace("Build System selected: no build system");
            return new NoBuildSystem();
        }

        if(true == "qmake".equals(BuldSystemType))
        {
            log.trace("Build System selected: Qmake");
            return new QmakeBuildSystem(ctx);
        }

        // New Build Systems go here!

        // Default is Make
        if("true".equals(ctx.cfg().getString(Configuration.CFG_EMBEETLE_PROJECT)))
        {
            log.trace("Build System selected: Embeetle make");
            return new EmbeetleMakeBuildSystem(ctx);
        }
        else
        {
            log.trace("Build System selected: make");
            return new MakeBuildSystem(ctx);
        }
    }

}
