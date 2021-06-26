package de.nomagic.puzzler.Generator;

import static org.junit.Assert.*;

import org.jdom2.Element;
import org.junit.Test;

import de.nomagic.puzzler.ContextStub;
import de.nomagic.puzzler.configuration.Configuration;
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
    public void testNoElements()
    {
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(null);
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(algo, ctx);
        assertNotNull(res);
        assertEquals(0, res.length);
    }

    @Test
    public void test_C()
    {
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        Element code = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(algo, ctx);
        assertNotNull(res);
        assertEquals(1, res.length);
        assertTrue(res[0] instanceof C_CodeGenerator);
    }
    
    @Test
    public void test_Cpp()
    {
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        Element code = new Element(Cpp_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        algo.addAlgorithmElement(Cpp_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(algo, ctx);
        assertNotNull(res);
        assertEquals(1, res.length);
        assertTrue(res[0] instanceof Cpp_CodeGenerator);
    }
    
    @Test
    public void test_Verilog()
    {
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        Element code = new Element(Verilog_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        algo.addAlgorithmElement(Verilog_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(algo, ctx);
        assertNotNull(res);
        assertEquals(1, res.length);
        assertTrue(res[0] instanceof Verilog_CodeGenerator);
    }
}
