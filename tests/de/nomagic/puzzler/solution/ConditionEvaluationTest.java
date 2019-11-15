package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.jdom2.Element;
import org.junit.Test;

import de.nomagic.puzzler.ContextImpl;
import de.nomagic.puzzler.configuration.Configuration;

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
        ContextImpl ctx = new ContextImpl(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        Element condition = new Element("bla");
        Element result = dut.getBest(condition, null, null, null);
        assertEquals(null, result);
    }

    @Test
    public void testevaluateConditionParenthesisEmptyString()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        String result = dut.evaluateConditionParenthesis("", caStub, null, null);
        assertEquals("", result);
    }

    @Test
    public void testevaluateConditionParenthesisNull()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        String result = dut.evaluateConditionParenthesis("true", null, null, null);
        assertEquals("false", result);
    }

    @Test
    public void testevaluateConditionParenthesisTrue()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        String result = dut.evaluateConditionParenthesis("true", caStub, null, null);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesisFalse()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        String result = dut.evaluateConditionParenthesis("false", caStub, null, null);
        assertEquals("false", result);
    }

    @Test
    public void testevaluateConditionParenthesisIs()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addPropertie("singleTask", "true");
        String result = dut.evaluateConditionParenthesis("is(singleTask)", caStub, null, null);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesisComplex()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addPropertie("frequency", "1000");
        caStub.addPropertie("msTimer", "available");
        String result = dut.evaluateConditionParenthesis("(frequency smallerThan 1001) and has('msTimer')", caStub, null, null);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesisParamTrue()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addParameter("on", "true");
        String result = dut.evaluateConditionParenthesis("true equals param(on)", caStub, null, null);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesisParamFalse()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addParameter("on", "false");
        String result = dut.evaluateConditionParenthesis("true equals param(on)", caStub, null, null);
        assertEquals("false", result);
    }

    @Test
    public void testevaluateConditionParenthesisSubstract()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        String result = dut.evaluateConditionParenthesis("100 - 12", caStub, null, null);
        assertEquals("88", result);
    }

    @Test
    public void testevaluateConditionParenthesisSubstractAndAlgorithmParameter()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addParameter("dutyCycle_percent", "50");
        String result = dut.evaluateConditionParenthesis("100 - dutyCycle_percent", caStub, null, null);
        assertEquals("50", result);
    }

    @Test
    public void testevaluateConditionParenthesisParamChar()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addParameter("port", "D");
        String result = dut.evaluateConditionParenthesis("A equals port", caStub, null, null);
        assertEquals("false", result);
    }

    @Test
    public void testevaluateConditionParenthesisParamCharMatch()
    {
        Configuration cfg = new Configuration();
        ContextImpl ctx = new ContextImpl(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addParameter("port", "A");
        String result = dut.evaluateConditionParenthesis("A equals port", caStub, null, null);
        assertEquals("true", result);
    }
}
