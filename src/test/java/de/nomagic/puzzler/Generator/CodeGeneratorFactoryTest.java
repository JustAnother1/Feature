package de.nomagic.puzzler.Generator;

import static org.junit.Assert.*;

import org.junit.Test;

import de.nomagic.puzzler.ContextStub;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.SolutionStub;

public class CodeGeneratorFactoryTest {

    @Test
    public void testNoContext()
    {
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(null);
        assertNotNull(res);
        assertEquals(0, res.length);
    }
    
    @Test
    public void testNoSolution()
    {
        ContextStub ctx = new ContextStub(null);
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(ctx);
        assertNotNull(res);
        assertEquals(0, res.length);
    }

    @Test
    public void test_C()
    {
        ContextStub ctx = new ContextStub(new Configuration());
        SolutionStub solstub = new SolutionStub();
        solstub.setHasElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        ctx.addSolution(solstub);
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(ctx);
        assertNotNull(res);
        assertEquals(1, res.length);
        assertTrue(res[0] instanceof C_CodeGenerator);
    }
    
    @Test
    public void test_Cpp()
    {
        ContextStub ctx = new ContextStub(new Configuration());
        SolutionStub solstub = new SolutionStub();
        solstub.setHasElement(Cpp_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        ctx.addSolution(solstub);
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(ctx);
        assertNotNull(res);
        assertEquals(1, res.length);
        assertTrue(res[0] instanceof Cpp_CodeGenerator);
    }
    
    @Test
    public void test_Verilog()
    {
        ContextStub ctx = new ContextStub(new Configuration());
        SolutionStub solstub = new SolutionStub();
        solstub.setHasElement(Verilog_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        ctx.addSolution(solstub);
        Generator[] res = CodeGeneratorFactory.getGeneratorFor(ctx);
        assertNotNull(res);
        assertEquals(1, res.length);
        assertTrue(res[0] instanceof Verilog_CodeGenerator);
    }
}
