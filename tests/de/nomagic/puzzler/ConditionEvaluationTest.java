package de.nomagic.puzzler;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.jdom2.Element;
import org.junit.Test;

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
        ProgressReport report = new ProgressReport();
        ConditionEvaluator dut = new ConditionEvaluator(properties, report, parameters);
        Element result = dut.getBest(null);
        assertEquals(null, result);
    }

    @Test
    public void testevaluateConditionParenthesis_true()
    {
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        ProgressReport report = new ProgressReport();
        ConditionEvaluator dut = new ConditionEvaluator(properties, report, parameters);
        String result = dut.evaluateConditionParenthesis("true");
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_fasle()
    {
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        ProgressReport report = new ProgressReport();
        ConditionEvaluator dut = new ConditionEvaluator(properties, report, parameters);
        String result = dut.evaluateConditionParenthesis("false");
        assertEquals("false", result);
    }

    @Test
    public void testevaluateConditionParenthesis_is()
    {
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        ProgressReport report = new ProgressReport();
        properties.put("singleTask", "true");
        ConditionEvaluator dut = new ConditionEvaluator(properties, report, parameters);
        String result = dut.evaluateConditionParenthesis("is(singleTask)");
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_complex()
    {
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        ProgressReport report = new ProgressReport();
        properties.put("frequency", "1000");
        properties.put("msTimer", "available");
        ConditionEvaluator dut = new ConditionEvaluator(properties, report, parameters);
        String result = dut.evaluateConditionParenthesis("(frequency smallerThan 1001) and has('msTimer')");
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_param_true()
    {
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        ProgressReport report = new ProgressReport();
        parameters.put("on", "true");
        ConditionEvaluator dut = new ConditionEvaluator(properties, report, parameters);
        String result = dut.evaluateConditionParenthesis("true equals param(on)");
        assertEquals("true", result);
    }

    @Test
    public void testevaluateConditionParenthesis_param_false()
    {
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        ProgressReport report = new ProgressReport();
        parameters.put("on", "false");
        ConditionEvaluator dut = new ConditionEvaluator(properties, report, parameters);
        String result = dut.evaluateConditionParenthesis("true equals param(on)");
        assertEquals("false", result);
    }

}
