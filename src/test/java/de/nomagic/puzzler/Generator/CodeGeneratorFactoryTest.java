package de.nomagic.puzzler.Generator;

import static org.junit.Assert.*;

import org.junit.Test;

import de.nomagic.puzzler.ContextStub;
import de.nomagic.puzzler.solution.ConfiguredAlgorithmStub;

public class CodeGeneratorFactoryTest {

    @Test
    public void testNoAlgos()
    {
        ContextStub ctx = new ContextStub(null);
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(null, ctx);
        assertNull(res);
    }

    @Test
    public void testNotC()
    {
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(null);
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(algo, ctx);
        assertNotNull(res);
        assertEquals(0, res.length);
    }

    @Test
    public void test_no_Api()
    {
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(null);
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(algo, ctx);
        assertNotNull(res);
        assertEquals(0, res.length);
    }
}
