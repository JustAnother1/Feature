package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.jdom2.Element;
import org.junit.Test;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.ConditionEvaluator;

public class ConditionEvaluationTest {

    /*
     * valid:
     * "is(singleTask)"
     * "(frequency smallerThan 1001) and has(msTimer)"
     * "true equals param(on)"
     */


    @Test
    public void testGetBest()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        Element result = dut.getBest(null, null);
        assertEquals(null, result);
    }
    
    @Test
    public void testevaluateConditionParenthesis_empty_string()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        String result = dut.evaluateConditionParenthesis("", caStub);
        assertEquals("", result);
    }

    @Test
    public void testevaluateConditionParenthesis_null()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        String result = dut.evaluateConditionParenthesis("true", null);
        assertEquals("false", result);
    }
    
    @Test
    public void testevaluateConditionParenthesis_true()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        String result = dut.evaluateConditionParenthesis("true", caStub);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_fasle()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        String result = dut.evaluateConditionParenthesis("false", caStub);
        assertEquals("false", result);
    }

    @Test
    public void testevaluateConditionParenthesis_is()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addPropertie("singleTask", "true");
        String result = dut.evaluateConditionParenthesis("is(singleTask)", caStub);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_complex()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addPropertie("frequency", "1000");
        caStub.addPropertie("msTimer", "available");
        String result = dut.evaluateConditionParenthesis("(frequency smallerThan 1001) and has('msTimer')", caStub);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_param_true()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addParameter("on", "true");
        String result = dut.evaluateConditionParenthesis("true equals param(on)", caStub);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_param_false()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addParameter("on", "false");
        String result = dut.evaluateConditionParenthesis("true equals param(on)", caStub);
        assertEquals("false", result);
    }

}
