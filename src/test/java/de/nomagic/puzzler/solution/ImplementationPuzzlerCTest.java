package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.junit.Test;

import de.nomagic.puzzler.ContextStub;
import de.nomagic.puzzler.configuration.Configuration;

public class ImplementationPuzzlerCTest {

    @Test
    public void testImplementationPuzzlerC_NoContext_NoAlgo()
    {
        ImplementationPuzzlerC dut = new ImplementationPuzzlerC(null, null);
        assertNotNull(dut);
    }

    @Test
    public void testImplementationPuzzlerC_NoConfig_NoAlgo()
    {
        ContextStub ctx = new ContextStub(null);
        ImplementationPuzzlerC dut = new ImplementationPuzzlerC(ctx, null);
        assertNotNull(dut);
    }

    @Test
    public void testImplementationPuzzlerC_NoAlgo()
    {
        Configuration cfg = new Configuration();
        ContextStub ctx = new ContextStub(cfg);
        ImplementationPuzzlerC dut = new ImplementationPuzzlerC(ctx, null);
        assertNotNull(dut);
    }

    @Test
    public void testImplementationPuzzlerC_docCode_NoAlgo()
    {
        Configuration cfg = new Configuration();
        cfg.setString(Configuration.CFG_DOC_CODE_SRC, "true");
        ContextStub ctx = new ContextStub(cfg);
        ImplementationPuzzlerC dut = new ImplementationPuzzlerC(ctx, null);
        assertNotNull(dut);
    }

    @Test
    public void testImplementationPuzzlerC_NodocCode_NoAlgo()
    {
        Configuration cfg = new Configuration();
        cfg.setString(Configuration.CFG_DOC_CODE_SRC, "false");
        ContextStub ctx = new ContextStub(cfg);
        ImplementationPuzzlerC dut = new ImplementationPuzzlerC(ctx, null);
        assertNotNull(dut);
    }

    @Test
    public void testGetImplementationOf_Null()
    {
        Configuration cfg = new Configuration();
        cfg.setString(Configuration.CFG_DOC_CODE_SRC, "false");
        ContextStub ctx = new ContextStub(cfg);
        ImplementationPuzzlerC dut = new ImplementationPuzzlerC(ctx, null);
        assertNotNull(dut);
        dut.getImplementationOf(null);
    }

    @Test
    public void testReplacePlaceHolders_null_null()
    {
        Configuration cfg = new Configuration();
        cfg.setString(Configuration.CFG_DOC_CODE_SRC, "false");
        ContextStub ctx = new ContextStub(cfg);
        ImplementationPuzzlerC dut = new ImplementationPuzzlerC(ctx, null);
        assertNotNull(dut);
        dut.replacePlaceHolders(null, null);
    }

}
