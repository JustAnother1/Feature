package de.nomagic.puzzler.BuildSystem;

import static org.junit.Assert.*;

import org.junit.Test;

import de.nomagic.puzzler.ContextStub;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.Environment.EnvironmentStub;
import de.nomagic.puzzler.configuration.Configuration;

public class BuildSystemFactoryTest
{

    @Test
    public void testGetBuildSystemFor_null()
    {
        assertNull(BuildSystemFactory.getBuildSystemFor(null));
    }

    @Test
    public void testGetBuildSystemFor_emptyContext()
    {
        ContextStub ctx = new ContextStub();
        assertNull(BuildSystemFactory.getBuildSystemFor(ctx));
    }

    @Test
    public void testGetBuildSystemFor_default()
    {
        Configuration cfg = new Configuration();
        ContextStub ctx = new ContextStub(cfg);
        Environment e = new EnvironmentStub();
        ctx.addEnvironment(e);

        BuildSystemApi res = BuildSystemFactory.getBuildSystemFor(ctx);

        assertNotNull(res);
        assertTrue(res instanceof MakeBuildSystem);
    }

    @Test
    public void testGetBuildSystemFor_Embeetle()
    {
        Configuration cfg = new Configuration();
        cfg.setString(Configuration.CFG_EMBEETLE_PROJECT, "true");
        ContextStub ctx = new ContextStub(cfg);
        Environment e = new EnvironmentStub();
        ctx.addEnvironment(e);

        BuildSystemApi res = BuildSystemFactory.getBuildSystemFor(ctx);

        assertNotNull(res);
        assertTrue(res instanceof EmbeetleMakeBuildSystem);
    }

    @Test
    public void testGetBuildSystemFor_none()
    {
        Configuration cfg = new Configuration();
        ContextStub ctx = new ContextStub(cfg);
        EnvironmentStub e = new EnvironmentStub();
        e.setBuildSystemType("none");
        ctx.addEnvironment(e);

        BuildSystemApi res = BuildSystemFactory.getBuildSystemFor(ctx);

        assertNotNull(res);
        assertTrue(res instanceof NoBuildSystem);
    }

    @Test
    public void testGetBuildSystemFor_qmake()
    {
        Configuration cfg = new Configuration();
        ContextStub ctx = new ContextStub(cfg);
        EnvironmentStub e = new EnvironmentStub();
        e.setBuildSystemType("qmake");
        ctx.addEnvironment(e);

        BuildSystemApi res = BuildSystemFactory.getBuildSystemFor(ctx);

        assertNotNull(res);
        assertTrue(res instanceof QmakeBuildSystem);
    }

}
