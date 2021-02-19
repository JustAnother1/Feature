package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.jdom2.Element;
import org.junit.Test;

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
        Element condition = new Element("bla");
        Element result = ConditionEvaluator.getBest(condition, null, null, null);
        assertEquals(null, result);
    }

    @Test
    public void testevaluateConditionParenthesisEmptyString()
    {
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        CondEvalResult result = ConditionEvaluator.evaluateConditionParenthesis("", caStub, null, null);
        assertEquals(true, result.isValid());
        assertEquals("", result.getResult());
    }

    @Test
    public void testevaluateConditionParenthesisNull()
    {
        CondEvalResult result = ConditionEvaluator.evaluateConditionParenthesis("true", null, null, null);
        assertEquals(true, result.isValid());
        assertEquals("false", result.getResult());
    }

    @Test
    public void testevaluateConditionParenthesisTrue()
    {
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        CondEvalResult result = ConditionEvaluator.evaluateConditionParenthesis("true", caStub, null, null);
        assertEquals(true, result.isValid());
        assertEquals("true", result.getResult());
    }

    @Test
    public void testevaluateConditionParenthesisFalse()
    {
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        CondEvalResult result = ConditionEvaluator.evaluateConditionParenthesis("false", caStub, null, null);
        assertEquals(true, result.isValid());
        assertEquals("false", result.getResult());
    }

    @Test
    public void testevaluateConditionParenthesisIs()
    {
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addPropertie("singleTask", "true");
        CondEvalResult result = ConditionEvaluator.evaluateConditionParenthesis("is(singleTask)", caStub, null, null);
        assertEquals(true, result.isValid());
        assertEquals("true", result.getResult());
    }

    @Test
    public void testevaluateConditionParenthesisComplex()
    {
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addPropertie("frequency", "1000");
        caStub.addPropertie("msTimer", "available");
        CondEvalResult result = ConditionEvaluator.evaluateConditionParenthesis("(frequency smallerThan 1001) and has('msTimer')", caStub, null, null);
        assertEquals(true, result.isValid());
        assertEquals("true", result.getResult());
    }

    @Test
    public void testevaluateConditionParenthesisParamTrue()
    {
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addParameter("on", "true");
        CondEvalResult result = ConditionEvaluator.evaluateConditionParenthesis("true equals param(on)", caStub, null, null);
        assertEquals(true, result.isValid());
        assertEquals("true", result.getResult());
    }

    @Test
    public void testevaluateConditionParenthesisParamFalse()
    {
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addParameter("on", "false");
        CondEvalResult result = ConditionEvaluator.evaluateConditionParenthesis("true equals param(on)", caStub, null, null);
        assertEquals(true, result.isValid());
        assertEquals("false", result.getResult());
    }

    @Test
    public void testevaluateConditionParenthesisSubstract()
    {
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        CondEvalResult result = ConditionEvaluator.evaluateConditionParenthesis("100 - 12", caStub, null, null);
        assertEquals(true, result.isValid());
        assertEquals("88", result.getResult());
    }

    @Test
    public void testevaluateConditionParenthesisSubstractAndAlgorithmParameter()
    {
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addParameter("dutyCycle_percent", "50");
        CondEvalResult result = ConditionEvaluator.evaluateConditionParenthesis("100 - dutyCycle_percent", caStub, null, null);
        assertEquals(true, result.isValid());
        assertEquals("50", result.getResult());
    }

    @Test
    public void testevaluateConditionParenthesisParamChar()
    {
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addParameter("port", "D");
        CondEvalResult result = ConditionEvaluator.evaluateConditionParenthesis("A equals port", caStub, null, null);
        assertEquals(true, result.isValid());
        assertEquals("false", result.getResult());
    }

    @Test
    public void testevaluateConditionParenthesisParamCharMatch()
    {
        ConfiguredAlgorithmStub caStub = new ConfiguredAlgorithmStub();
        caStub.addParameter("port", "A");
        CondEvalResult result = ConditionEvaluator.evaluateConditionParenthesis("A equals port", caStub, null, null);
        assertEquals(true, result.isValid());
        assertEquals("true", result.getResult());
    }
}
