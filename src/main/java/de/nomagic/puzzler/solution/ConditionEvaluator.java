package de.nomagic.puzzler.solution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionEvaluator
{
    public static final String CONDITION_EVALUATOR_CONDITION_ATTRIBUTE_NAME = "cond";

    // constants
    public static final String KEY_TRUE = "true";
    public static final String KEY_FALSE = "false";
    // logic
    public static final String KEY_AND = "and";
    public static final String KEY_OR = "or";
    public static final String KEY_IS_NOT_EQUAL_TO = "isNotEqualTo";
    public static final String KEY_EQUALS = "equals";
    public static final String KEY_SMALLER_THAN = "smallerThan";
    public static final String KEY_GREATER_THAN = "greaterThan";
    // functions
    public static final String KEY_HAS = "has";
    public static final String KEY_IS = "is";
    public static final String KEY_PARAM = "param";
    // math
    public static final String KEY_MINUS = "-";
    public static final String KEY_PLUS = "+";
    public static final String KEY_MULTIPLY = "*";
    public static final String KEY_DEVIDE = "/";

    private static final Logger log = LoggerFactory.getLogger("ConditionEvaluator");

    private ConditionEvaluator()
    {
        // not used
    }

    public static Element getBest(List<Element> conditions,
                           AlgorithmInstanceInterface algo,
                           String functionArguments,
                           Element function)
    {
        if( (null == conditions) || (null == algo) )
        {
            return null;
        }
        if(conditions.isEmpty())
        {
            return null;
        }
        boolean valid = true;

        // check condition
        ArrayList<Element> valids = new ArrayList<Element>();
        Iterator<Element> it = conditions.iterator();
        while(it.hasNext() && (true == valid))
        {
            Element curE = it.next();
            String conditionText = curE.getAttributeValue(CONDITION_EVALUATOR_CONDITION_ATTRIBUTE_NAME);
            log.trace("evaluating condition {}", conditionText);
            CondEvalResult res = evaluateConditionParenthesis(conditionText, algo, functionArguments, function);
            if(true == res.isValid())
            {
                if(true == KEY_TRUE.equals(res.getResult()))
                {
                    valids.add(curE);
                }
                else
                {
                    log.trace("condition not met");
                }
            }
            else
            {
                valid = false;
            }
        }
        if(false == valid)
        {
            return null;
        }
        // from the valids select the best one
        if(valids.isEmpty())
        {
            return null;
        }
        if(1 == valids.size())
        {
            return valids.get(0);
        }
        // score the solutions TODO
        return conditions.get(0);
    }

    public static Element getBest(
            Element conditions,
            AlgorithmInstanceInterface algo,
            String functionArguments,
            Element function)
    {
        String conditionText = conditions.getAttributeValue(CONDITION_EVALUATOR_CONDITION_ATTRIBUTE_NAME);
        log.trace("evaluating condition {}", conditionText);
        CondEvalResult res = evaluateConditionParenthesis(conditionText, algo, functionArguments, function);
        if((true == res.isValid()) && (true == KEY_TRUE.equals(res.getResult())))
        {
            return conditions;
        }
        else
        {
            log.trace("condition not met");
            return null;
        }
    }

    public static CondEvalResult evaluateConditionParenthesis(
            String condition,
            AlgorithmInstanceInterface algo,
            String functionArguments,
            Element function)
    {
        if( (null == condition) || (null == algo) )
        {
            CondEvalResult res = new CondEvalResult();
            res.setValid(false);
            res.addErrorLine("required parameter is null !");
            return res;
        }
        if(false == condition.contains("("))
        {
            return evaluateConditionText(condition, algo, functionArguments, function);
        }
        else
        {
            int numOpenP = 0;
            Vector<StringBuilder> sections = new Vector<StringBuilder>();
            StringBuilder curSection = new StringBuilder();
            sections.add(curSection);

            for(int i = 0; i < condition.length(); i++)
            {
                char c = condition.charAt(i);
                switch(c)
                {
                case '(':
                    curSection.append(c);
                    numOpenP ++;

                    try
                    {
                        sections.remove(numOpenP);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        // element does not exist
                        // -> no need to delete it
                        // -> we are done here
                    }
                    curSection = new StringBuilder();
                    sections.add(curSection);
                    break;

                case ')':
                    if(0 == numOpenP)
                    {
                        CondEvalResult res = new CondEvalResult();
                        res.setValid(false);
                        res.addErrorLine("Parenthesis mismatch in condition : " + condition);
                        return res;
                    }
                    // else:
                    CondEvalResult res = evaluateConditionText(
                            curSection.toString(),
                            algo,
                            functionArguments,
                            function);
                    if(false == res.isValid())
                    {
                        return res;
                    }
                    else
                    {
                        String sectionResult = res.getResult();
                        sections.remove(numOpenP);
                        numOpenP--;
                        curSection = sections.get(numOpenP);
                        curSection.append(sectionResult);
                        curSection.append(")");
                    }
                    break;

                default:
                    curSection.append(c);
                    break;
                }
            }
            // we parsed all the chars in the condition
            // so first some sanity checks
            if(0 != numOpenP)
            {
                CondEvalResult res = new CondEvalResult();
                res.setValid(false);
                res.addErrorLine("Parenthesis mismatch at end of condition : " + condition);
                return res;
            }
            if(1 != sections.size())
            {
                CondEvalResult res = new CondEvalResult();
                res.setValid(false);
                res.addErrorLine("Parenthesis Section mismatch at end of condition : " + condition);
                return res;
            }
            StringBuilder resultSection = sections.get(0);
            return evaluateConditionText(resultSection.toString(), algo, functionArguments, function);
        }
    }

    private static CondEvalResult evaluateFunction(
            String Word,
            AlgorithmInstanceInterface algo,
            String functionArguments,
            Element function )
    {
        int indexOpeningBrace = Word.indexOf('(');
        int indexClosingBrace = Word.indexOf(')');
        String functionName = Word.substring(0, indexOpeningBrace);
        String parameter = Word.substring(indexOpeningBrace + 1, indexClosingBrace);
        CondEvalResult res = new CondEvalResult();
        if(1 > functionName.length())
        {
            // just additional braces -> remove those and evaluate the rest
            return evaluateWord(parameter, algo, functionArguments, function);  // Caution : Recursion! So don't over do it with the unneeded braces!
        }
        switch(functionName)
        {
        case KEY_HAS:
            // Parameter may already be evaluated
            if(KEY_TRUE.equals(parameter))
            {
                res.setResultValid(KEY_TRUE);
                break;
            }
            if(KEY_FALSE.equals(parameter))
            {
                res.setResultValid(KEY_FALSE);
                break;
            }
            // If not then evaluate now
            if(true == parameter.contains("'"))
            {
                parameter = parameter.substring(parameter.indexOf('\'') + 1, parameter.lastIndexOf('\''));
            }
            String test = algo.getProperty(parameter);
            if(null == test)
            {
                test = algo.getBuildIn(parameter);
            }
            if(null == test)
            {
                res.setResultValid(KEY_FALSE);
            }
            else
            {
                res.setResultValid(KEY_TRUE);
            }
            break;

        case KEY_IS:
            // Parameter may already be evaluated
            if(KEY_TRUE.equals(parameter))
            {
                res.setResultValid(KEY_TRUE);
                break;
            }
            if(KEY_FALSE.equals(parameter))
            {
                res.setResultValid(KEY_FALSE);
                break;
            }
            // If not then evaluate now
            String resStr = algo.getProperty(parameter);
            if(null == resStr)
            {
                resStr = algo.getBuildIn(parameter);
            }
            if(null == resStr)
            {
                // there is something wrong here
                res.setValid(false);
                res.addErrorLine("unknown property : " + parameter);
            }
            else
            {
                if(KEY_TRUE.equals(resStr))
                {
                    res.setResultValid(KEY_TRUE);
                }
                else
                {
                    res.setResultValid(KEY_FALSE);
                }
            }
            break;

        case KEY_PARAM:
            // Parameter may already be evaluated
            if(KEY_TRUE.equals(parameter))
            {
                res.setResultValid(KEY_TRUE);
                break;
            }
            if(KEY_FALSE.equals(parameter))
            {
                res.setResultValid(KEY_FALSE);
            }
            // If not then evaluate now
            log.trace("parameter is {}", parameter);
            CondEvalResult resRes = getParameter(parameter, functionArguments, function);
            if(false == resRes.isValid())
            {
                // there is something wrong here
                resRes.addErrorLine("unknown parameter : " + parameter);
                resRes.addErrorLine(algo.dumpParameter());
                resRes.addErrorLine(algo.toString());
                return resRes;
            }
            else
            {
                String value = resRes.getResult();
                log.trace("parameter value is {}", value);
                if(KEY_TRUE.equals(value))
                {
                    res.setResultValid(KEY_TRUE);
                    break;
                }
                if(KEY_FALSE.equals(value))
                {
                    res.setResultValid(KEY_FALSE);
                    break;
                }
                log.trace("unparseable parameter value: {}", value);
                res.setResultValid(value);
            }
            break;

        default:
            // there is something wrong here
            res.setValid(false);
            res.addErrorLine("unknown function : '" + functionName  + "' in '" + Word + "'");
        }
        return res;
    }

    private static CondEvalResult getParameter(String name, String functionArguments, Element function)
    {
        CondEvalResult res = new CondEvalResult();
        if(null == name)
        {
            res.setValid(false);
            res.addErrorLine("could not get parameter value: name is null!");
            return res;
        }
        if(null == function)
        {
            res.setValid(false);
            res.addErrorLine("could not get parameter value: function element is null!");
            return res;
        }
        if(null == functionArguments)
        {
            res.setValid(false);
            res.addErrorLine("could not get parameter value: function arguments are null!");
            return res;
        }

        int i = 0;
        String paramName = null;
        do
        {
            Attribute att = function.getAttribute(KEY_PARAM + i + "_name");
            if(null != att)
            {
                paramName = att.getValue();
                if((null != paramName) && (true == name.equals(paramName)))
                {
                    // i is the parameter index (0, 1, 2,.. )
                    String[] arguments = functionArguments.split(",");
                    if(i < arguments.length)
                    {
                        res.setResultValid(arguments[i]);
                        return res;
                    }
                }
            }
        }while(paramName != null);

        // Parameter with that name was not found
        res.setValid(false);
        res.addErrorLine("Parameter with the name '" + name + "' was not found");
        return res;
    }

    /**
     *
     * Conditions can have these keywords:
     * true                 boolean value true
     * false                boolean value false
     * and                  logical and (must be between two boolean expressions)
     * or                   logical or (must be between two boolean expressions)
     * isNotEqualTo         logical not equal (must be between two boolean expressions)
     * equals               logical equal (must be between two boolean expressions)
     * smallerThan          logical smaller than (must be between two boolean expressions)
     * greaterThan          logical greater than (must be between two boolean expressions)
     * has('property')      is true if the solution has the property set to a value. otherwise it is false.
     *                      the ' ' are necessary to differentiate between the name of the property and the value
     * is(property)         is the value of the boolean property or false if the property is not set.
     * param(parameterName) is the value of the parameter.
     *
     * configuration Attributes can be referred to by name
     *
     * @param conditionText
     * @return
     */
    private static CondEvalResult evaluateWord(
            String Word,
            AlgorithmInstanceInterface algo,
            String functionArguments,
            Element function)
    {
        CondEvalResult res = new CondEvalResult();
        // constants
        if(KEY_TRUE.equals(Word))
        {
            res.setResultValid(KEY_TRUE);
            return res;
        }
        if(KEY_FALSE.equals(Word))
        {
            res.setResultValid(KEY_FALSE);
            return res;
        }

        // functions
        if((true == Word.contains("(")) && (true == Word.contains(")")))
        {
            return evaluateFunction(Word, algo, functionArguments, function);
        }

        //configuration Attributes
        String val = algo.getProperty(Word);
        if(null == val)
        {
            // might be build-in
            val = algo.getBuildIn(Word);
        }
        if(null != val)
        {
            res.setResultValid(val);
            return res;
        }

        // This happens if we try to evaluate a parameter to a function defined here (e.g: "has(bla)" )
        val = algo.getParameter(Word);
        if(null != val)
        {
            res.setResultValid(val);
            return res;
        }
        else
        {
            // we give up. Returning the word for better error messages.
            res.setResultValid(Word);
            return res;
        }
    }

    private static boolean isFunctionWord(String Word)
    {
        if(KEY_AND.equals(Word))
        {
            return true;
        }
        if(KEY_OR.equals(Word))
        {
            return true;
        }
        if(KEY_IS_NOT_EQUAL_TO.equals(Word))
        {
            return true;
        }
        if(KEY_EQUALS.equals(Word))
        {
            return true;
        }
        if(KEY_SMALLER_THAN.equals(Word))
        {
            return true;
        }
        if(KEY_GREATER_THAN.equals(Word))
        {
            return true;
        }
        if(KEY_MINUS.equals(Word))
        {
            return true;
        }
        if(KEY_PLUS.equals(Word))
        {
            return true;
        }
        if(KEY_MULTIPLY.equals(Word))
        {
            return true;
        }
        if(KEY_DEVIDE.equals(Word))
        {
            return true;
        }
        return false;
    }

    private static CondEvalResult evaluateConditionText(
            String conditionText,
            AlgorithmInstanceInterface algo,
            String functionArguments,
            Element function )
    {
        // parse condition
        String[] parts = conditionText.split("\\s"); // all whitespace splits
        if(null == parts)
        {
            CondEvalResult res = new CondEvalResult();
            res.setValid(false);
            res.addErrorLine("required parameter 'parts' is null !");
            return res;
        }
        if(1 > parts.length)
        {
            CondEvalResult res = new CondEvalResult();
            res.setValid(false);
            res.addErrorLine("required parameter 'parts' length is 0 !");
            return res;
        }
        // evaluate condition
        String first = null;
        for(int i = 0; i < parts.length; i++)
        {
            String curPart = parts[i];
            if(true == isFunctionWord(curPart))
            {
                if(i+1 < parts.length)
                {
                    CondEvalResult res =  evaluateFunction(curPart, first, parts[i+1], algo, functionArguments, function);
                    if(true == res.isValid())
                    {
                        first = res.getResult();
                        i++;
                    }
                    else
                    {
                        // res is already invalid and has the error.
                        return res;
                    }
                }
                else
                {
                    // last word missing
                    CondEvalResult res = new CondEvalResult();
                    res.setValid(false);
                    res.addErrorLine("last word missing in : " + conditionText);
                    return res;
                }
            }
            else
            {
                if(null == first)
                {
                    first = curPart;
                }
                else
                {
                    // there is something wrong here
                    CondEvalResult res = new CondEvalResult();
                    res.setValid(false);
                    res.addErrorLine("two non function words(" + first + ", " + curPart + ") in : " + conditionText);
                    res.addErrorLine("Algorithm: " + algo.toString());
                    res.addErrorLine(algo.dumpParameter());
                    res.addErrorLine(algo.dumpProperty());
                    return res;
                }
            }
        }
        // finish up
        CondEvalResult res = evaluateWord(first, algo, functionArguments, function);
        return res;
    }

    private static CondEvalResult evaluateFunction(
            String func,
            String first,
            String second,
            AlgorithmInstanceInterface algo,
            String functionArguments,
            Element function )
    {
        int one = 0;
        int two = 0;
        CondEvalResult res = evaluateWord(first, algo, functionArguments, function);
        if(false == res.isValid())
        {
            return res;
        }
        String valOne = res.getResult();
        res = evaluateWord(second, algo, functionArguments, function);
        if(false == res.isValid())
        {
            return res;
        }
        String valTwo = res.getResult();
        res = new CondEvalResult();
        switch(func)
        {
        case KEY_AND:
            if((KEY_TRUE.equals(valOne)) && (KEY_TRUE.equalsIgnoreCase(valTwo)))
            {
                res.setResultValid(KEY_TRUE);
            }
            else
            {
                res.setResultValid(KEY_FALSE);
            }
            break;

        case KEY_OR:
            if((KEY_TRUE.equals(valOne)) || (KEY_TRUE.equalsIgnoreCase(valTwo)))
            {
                res.setResultValid(KEY_TRUE);
            }
            else
            {
                res.setResultValid(KEY_FALSE);
            }
            break;

        case KEY_IS_NOT_EQUAL_TO:
            if(valOne.equals(valTwo))
            {
                res.setResultValid(KEY_FALSE);
            }
            else
            {
                res.setResultValid(KEY_TRUE);
            }
            break;

        case KEY_EQUALS:
            if(valOne.equals(valTwo))
            {
                res.setResultValid(KEY_TRUE);
            }
            else
            {
                log.trace("equals failed: {} != {}", valOne, valTwo);
                res.setResultValid(KEY_FALSE);
            }
            break;

        case KEY_SMALLER_THAN:
            try
            {
                one = Integer.parseInt(valOne);
            }
            catch(NumberFormatException e)
            {
                res.setValid(false);
                res.addErrorLine("one invalid number : '" + valOne + "'");
                res.addErrorLine(algo.toString());
                res.addErrorLine(algo.dumpParameter());
                res.addErrorLine(algo.dumpProperty());
                break;
            }
            try
            {
                two = Integer.parseInt(valTwo);
            }
            catch(NumberFormatException e)
            {
                res.setValid(false);
                res.addErrorLine("invalid number : '" + valTwo + "'");
            }
            if(one < two)
            {
                res.setResultValid(KEY_TRUE);
            }
            else
            {
                res.setResultValid(KEY_FALSE);
            }
            break;

        case KEY_GREATER_THAN:
            try
            {
                one = Integer.parseInt(valOne);
            }
            catch(NumberFormatException e)
            {
                res.setValid(false);
                res.addErrorLine("invalid number : '" + valOne + "'");
                break;
            }
            try
            {
                two = Integer.parseInt(valTwo);
            }
            catch(NumberFormatException e)
            {
                res.setValid(false);
                res.addErrorLine("invalid number : '" + valTwo + "'");
                break;
            }
            if(one > two)
            {
                res.setResultValid(KEY_TRUE);
            }
            else
            {
                res.setResultValid(KEY_FALSE);
            }
            break;


        case KEY_MINUS:
            try
            {
                one = Integer.parseInt(valOne);
            }
            catch(NumberFormatException e)
            {
                res.setValid(false);
                res.addErrorLine("one invalid number : '" + valOne + "'");
                res.addErrorLine(algo.toString());
                res.addErrorLine(algo.dumpParameter());
                res.addErrorLine(algo.dumpProperty());
                break;
            }
            try
            {
                two = Integer.parseInt(valTwo);
            }
            catch(NumberFormatException e)
            {
                res.setValid(false);
                res.addErrorLine("invalid number : '" + valTwo + "'");
                break;
            }
            res.setResultValid("" + (one - two));
            break;

        case KEY_PLUS:
            try
            {
                one = Integer.parseInt(valOne);
            }
            catch(NumberFormatException e)
            {
                res.setValid(false);
                res.addErrorLine("one invalid number : '" + valOne + "'");
                res.addErrorLine(algo.toString());
                res.addErrorLine(algo.dumpParameter());
                res.addErrorLine(algo.dumpProperty());
                break;
            }
            try
            {
                two = Integer.parseInt(valTwo);
            }
            catch(NumberFormatException e)
            {
                res.setValid(false);
                res.addErrorLine("invalid number : '" + valTwo + "'");
                break;
            }
            res.setResultValid("" + (one + two));
            break;

        case KEY_MULTIPLY:
            try
            {
                one = Integer.parseInt(valOne);
            }
            catch(NumberFormatException e)
            {
                res.setValid(false);
                res.addErrorLine("one invalid number : '" + valOne + "'");
                res.addErrorLine(algo.toString());
                res.addErrorLine(algo.dumpParameter());
                res.addErrorLine(algo.dumpProperty());
                break;
            }
            try
            {
                two = Integer.parseInt(valTwo);
            }
            catch(NumberFormatException e)
            {
                res.setValid(false);
                res.addErrorLine("invalid number : '" + valTwo + "'");
                break;
            }
            res.setResultValid("" + (one * two));
            break;

        case KEY_DEVIDE:
            try
            {
                one = Integer.parseInt(valOne);
            }
            catch(NumberFormatException e)
            {
                res.setValid(false);
                res.addErrorLine("one invalid number : '" + valOne + "'");
                res.addErrorLine(algo.toString());
                res.addErrorLine(algo.dumpParameter());
                res.addErrorLine(algo.dumpProperty());
                break;
            }
            try
            {
                two = Integer.parseInt(valTwo);
            }
            catch(NumberFormatException e)
            {
                res.setValid(false);
                res.addErrorLine("invalid number : '" + valTwo + "'");
                break;
            }
            res.setResultValid("" + (one / two));
            break;

        default:
            res.setValid(false);
            res.addErrorLine("invalid function : " + func);
        }
        return res;
    }

}
