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

}
