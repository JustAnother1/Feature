package de.nomagic.puzzler;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.jdom2.Element;
import org.junit.Test;

import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.progress.ProgressReport;
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
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        Configuration cfg = new Configuration();
        ProgressReport report = new ProgressReport();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        Element result = dut.getBest(null, null);
        assertEquals(null, result);
    }

    @Test
    public void testevaluateConditionParenthesis_true()
    {
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        Configuration cfg = new Configuration();
        ProgressReport report = new ProgressReport();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        String result = dut.evaluateConditionParenthesis("true", null);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_fasle()
    {
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        Configuration cfg = new Configuration();
        ProgressReport report = new ProgressReport();
        Context ctx = new Context(cfg);
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        String result = dut.evaluateConditionParenthesis("false", null);
        assertEquals("false", result);
    }

    @Test
    public void testevaluateConditionParenthesis_is()
    {
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        Configuration cfg = new Configuration();
        ProgressReport report = new ProgressReport();
        Context ctx = new Context(cfg);
        properties.put("singleTask", "true");
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        String result = dut.evaluateConditionParenthesis("is(singleTask)", null);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_complex()
    {
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        Configuration cfg = new Configuration();
        ProgressReport report = new ProgressReport();
        Context ctx = new Context(cfg);
        properties.put("frequency", "1000");
        properties.put("msTimer", "available");
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        String result = dut.evaluateConditionParenthesis("(frequency smallerThan 1001) and has('msTimer')", null);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_param_true()
    {
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        Configuration cfg = new Configuration();
        ProgressReport report = new ProgressReport();
        Context ctx = new Context(cfg);
        parameters.put("on", "true");
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        String result = dut.evaluateConditionParenthesis("true equals param(on)", null);
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_param_false()
    {
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        Configuration cfg = new Configuration();
        ProgressReport report = new ProgressReport();
        Context ctx = new Context(cfg);
        parameters.put("on", "false");
        ConditionEvaluator dut = new ConditionEvaluator(ctx);
        String result = dut.evaluateConditionParenthesis("true equals param(on)", null);
        assertEquals("false", result);
    }

}
