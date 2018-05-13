package de.nomagic.puzzler.Generator;

import static org.junit.Assert.*;

import org.junit.Test;

import de.nomagic.puzzler.solution.ConfiguredAlgorithmStub;
import de.nomagic.puzzler.solution.ContextStub;

public class CodeGeneratorFactoryTest {

    @Test
    public void testNoAlgos()
    {
        ContextStub ctx = new ContextStub();
        CodeGeneratorFactory genFactory = new CodeGeneratorFactory();
        assertNotNull(genFactory);
        Generator[] res = genFactory.getGeneratorFor(null, ctx);
        assertNull(res);
    }

    @Test
    public void testOK()
    {
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub();
        CodeGeneratorFactory genFactory = new CodeGeneratorFactory();
        assertNotNull(genFactory);
        Generator[] res = genFactory.getGeneratorFor(algo, ctx);
        assertNotNull(res);
    }
}
