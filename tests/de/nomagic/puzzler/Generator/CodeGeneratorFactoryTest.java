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
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(null, ctx);
        assertNull(res);
    }

    @Test
    public void testNotC()
    {
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub();
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(algo, ctx);
        assertNotNull(res);
        assertEquals(0, res.length);
    }

    @Test
    public void test_no_Api()
    {
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        // algo.setApi(CCodeGenerator.REQUIRED_ROOT_API);
        ContextStub ctx = new ContextStub();
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(algo, ctx);
        assertNotNull(res);
        assertEquals(1, res.length);
    }
}
