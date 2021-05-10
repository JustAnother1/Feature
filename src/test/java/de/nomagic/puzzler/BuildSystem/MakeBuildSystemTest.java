package de.nomagic.puzzler.BuildSystem;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.nomagic.puzzler.ContextImpl;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.CFile;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;

public class MakeBuildSystemTest
{
    Configuration cfg = new Configuration();
    ContextImpl ctx = new ContextImpl(cfg);
    MakeBuildSystem dut;

    @Before
    public void setUp()
    {
        dut = new MakeBuildSystem(ctx);
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testMakeBuildSystem()
    {
        assertNotNull(dut);
    }

    @Test
    public void testCreateBuildForNull()
    {
        assertNull("No Parameter given", dut.createBuildFor(null));
    }

    @Test
    public void testCreateBuildFor()
    {
        FileGroup files = new FileGroup();
        assertNull("Created Solution without Files", dut.createBuildFor(files));
    }
    
    @Test
    public void testCreateBuildForMain()
    {
        FileGroup files = new FileGroup();
        CFile main = new CFile("main.c");
        files.add(main);
        assertNull(dut.createBuildFor(files));
    }
    
    @Test
    public void testCreateBuildForProjectName()
    {
        FileGroup files = new FileGroup();
        CFile main = new CFile("main.c");
        files.add(main);
        cfg.setString("projectName", "bla");
        assertNull(dut.createBuildFor(files));
    }

    @Test
    public void testCreateBuildForEnvironment()
    {
        FileGroup files = new FileGroup();
        CFile main = new CFile("main.c");
        files.add(main);
        cfg.setString("projectName", "bla");
        Environment e = new Environment(ctx);
        ctx.addEnvironment(e);
        assertNull(dut.createBuildFor(files));
    }
    
    
    @Test
    public void testHasTargetForEmptySystem()
    {
        assertFalse("Found Target in Empty Build System", dut.hasTargetFor("%c"));
    }

    @Test
    public void testHasTargetForNoMatch()
    {
        Target trg = new Target("bla");
        dut.addTarget(trg);
        assertFalse("Found wrong Target", dut.hasTargetFor("%c"));
    }

    @Test
    public void testHasTargetForMatch()
    {
        Target trg = new Target("%c");
        dut.addTarget(trg);
        assertTrue("Did not find available Target", dut.hasTargetFor("%c"));
    }

}
