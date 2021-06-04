package de.nomagic.puzzler.Generator;

import static org.junit.Assert.*;

import org.junit.Test;

import de.nomagic.puzzler.ContextStub;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.ConfiguredAlgorithmStub;

public class C_CodeGeneratorTest
{

    @Test
    public void testCCodeGenerator()
    {
        C_CodeGenerator gen = new C_CodeGenerator(null);
        assertNotNull(gen);
    }

    @Test
    public void testLanguageName()
    {
        C_CodeGenerator gen = new C_CodeGenerator(null);
        assertEquals("C", gen.getLanguageName());
    }

    @Test
    public void testGenerateForNull()
    {
        ContextStub ctx = new ContextStub(new Configuration());
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(null);
        assertNull(fg);
    }

    @Test
    public void testGenerateForNotRoot()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);
        assertNull(fg);
    }

}
