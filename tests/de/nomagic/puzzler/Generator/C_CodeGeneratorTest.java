package de.nomagic.puzzler.Generator;

import static org.junit.Assert.*;

import org.junit.Test;

import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.ContextStub;

public class C_CodeGeneratorTest
{

    @Test
    public void testC_CodeGenerator()
    {
        C_CodeGenerator gen = new C_CodeGenerator(null);
        assertNotNull(gen);
    }

    @Test
    public void testConfigure_null()
    {
        ContextStub ctx = new ContextStub();
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        gen.configure(null);
    }

    @Test
    public void testConfigure()
    {
        ContextStub ctx = new ContextStub();
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        Configuration cfg = new Configuration();
        gen.configure(cfg);
    }

    @Test
    public void testConfigure_docMode()
    {
        ContextStub ctx = new ContextStub();
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        Configuration cfg = new Configuration();
        cfg.setString(C_CodeGenerator.CFG_DOC_CODE_SRC, "true");
        gen.configure(cfg);
    }

    @Test
    public void testGenerateFor_null()
    {
        ContextStub ctx = new ContextStub();
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(null);
        assertNull(fg);
    }

}
