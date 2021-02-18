package de.nomagic.puzzler.Generator;

import static org.junit.Assert.*;

import org.junit.Test;

import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.ConfiguredAlgorithmStub;
import de.nomagic.puzzler.solution.ContextStub;

public class CCodeGeneratorTest
{

    @Test
    public void testCCodeGenerator()
    {
        CCodeGenerator gen = new CCodeGenerator(null);
        assertNotNull(gen);
    }

    @Test
    public void testLanguageName()
    {
        CCodeGenerator gen = new CCodeGenerator(null);
        assertEquals("C", gen.getLanguageName());
    }

    @Test
    public void testGenerateForNull()
    {
        ContextStub ctx = new ContextStub();
        CCodeGenerator gen = new CCodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(null);
        assertNull(fg);
    }

    @Test
    public void testGenerateForNotRoot()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub();
        CCodeGenerator gen = new CCodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);
        assertNull(fg);
    }

}
