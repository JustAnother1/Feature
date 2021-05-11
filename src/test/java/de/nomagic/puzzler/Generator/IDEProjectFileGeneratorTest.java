package de.nomagic.puzzler.Generator;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import de.nomagic.puzzler.ContextStub;
import de.nomagic.puzzler.Environment.EnvironmentStub;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;

public class IDEProjectFileGeneratorTest {

    @Test
    public void test_generateFileInto_null()
    {
        FileGroup res = IDEProjectFileGenerator.generateFileInto(null, null);
        assertNull(res);
    }

    @Test
    public void test_generateFileInto_null_empty()
    {
        FileGroup in = new FileGroup();
        FileGroup res = IDEProjectFileGenerator.generateFileInto(null, in);
        assertNull(res);
    }

    @Test
    public void test_generateFileInto_ctx_null()
    {
        ContextStub ctx = new ContextStub();
        FileGroup res = IDEProjectFileGenerator.generateFileInto(ctx, null);
        assertNull(res);
    }

    @Test
    public void test_generateFileInto_empty()
    {
        Configuration cfg = new Configuration();
        ContextStub ctx = new ContextStub(cfg);
        FileGroup in = new FileGroup();
        FileGroup res = IDEProjectFileGenerator.generateFileInto(ctx, in);
        assertNull(res);
    }

    @Test
    public void test_generateFileInto_ProjectName()
    {
        Configuration cfg = new Configuration();
        cfg.setString(Configuration.PROJECT_FILE_CFG, "");
        ContextStub ctx = new ContextStub(cfg);
        FileGroup in = new FileGroup();
        FileGroup res = IDEProjectFileGenerator.generateFileInto(ctx, in);
        assertNull(res);
    }

    @Test
    public void test_generateFileInto_ProjectName_OK()
    {
        Configuration cfg = new Configuration();
        cfg.setString(Configuration.PROJECT_FILE_CFG, "blinky");
        ContextStub ctx = new ContextStub(cfg);
        FileGroup in = new FileGroup();
        FileGroup res = IDEProjectFileGenerator.generateFileInto(ctx, in);
        assertNotNull(res);
    }

    @Test
    public void test_generateFileInto_ProjectName_path()
    {
        Configuration cfg = new Configuration();
        cfg.setString(Configuration.PROJECT_FILE_CFG, "foo" + File.separator + "blinky");
        ContextStub ctx = new ContextStub(cfg);
        FileGroup in = new FileGroup();
        FileGroup res = IDEProjectFileGenerator.generateFileInto(ctx, in);
        assertNotNull(res);
    }

    @Test
    public void test_generateFileInto_embeetle()
    {
        Configuration cfg = new Configuration();
        cfg.setString(Configuration.PROJECT_FILE_CFG, "blinky");
        cfg.setString(Configuration.CFG_EMBEETLE_PROJECT, "true");
        EnvironmentStub e = new EnvironmentStub();
        e.SetPlatformParts(new String[]{"foo", "bar"});
        ContextStub ctx = new ContextStub(cfg);
        ctx.addEnvironment(e);
        FileGroup in = new FileGroup();
        FileGroup res = IDEProjectFileGenerator.generateFileInto(ctx, in);
        assertNotNull(res);
    }
}
