package de.nomagic.puzzler;

import static org.junit.Assert.*;

import org.jdom2.Element;
import org.junit.Test;

import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.ConditionEvaluator;

public class ConditionEvaluationTest 
{

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
        AlgoInstanceStub instance = new AlgoInstanceStub();
        String result = dut.evaluateConditionParenthesis("true", instance);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_fasle()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        AlgoInstanceStub instance = new AlgoInstanceStub();
        String result = dut.evaluateConditionParenthesis("false", instance);
        assertEquals("false", result);
    }

    @Test
    public void testevaluateConditionParenthesis_is()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        AlgoInstanceStub instance = new AlgoInstanceStub();
        instance.addProperty("singleTask", "true");
        String result = dut.evaluateConditionParenthesis("is(singleTask)", instance);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_complex()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        AlgoInstanceStub instance = new AlgoInstanceStub();
        instance.addProperty("frequency", "1000");
        instance.addProperty("msTimer", "available");
        String result = dut.evaluateConditionParenthesis("(frequency smallerThan 1001) and has('msTimer')", instance);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_param_true()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        AlgoInstanceStub instance = new AlgoInstanceStub();
        instance.addParameter("on", "true");
        String result = dut.evaluateConditionParenthesis("true equals param(on)", instance);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_param_false()
    {
        Configuration cfg = new Configuration();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        AlgoInstanceStub instance = new AlgoInstanceStub();
        instance.addParameter("on", "false");
        String result = dut.evaluateConditionParenthesis("true equals param(on)", instance);
        assertEquals("false", result);
    }

}
