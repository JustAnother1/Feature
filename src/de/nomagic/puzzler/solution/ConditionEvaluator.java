package de.nomagic.puzzler.solution;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;

public class ConditionEvaluator extends Base
{
    public final static String CONDITION_EVALUATOR_CONDITION_ATTRIBUTE_NAME = "cond";

    // constants
    public final static String KEY_TRUE = "true";
    public final static String KEY_FALSE = "false";
    // logic
    public final static String KEY_AND = "and";
    public final static String KEY_OR = "or";
    public final static String KEY_IS_NOT_EQUAL_TO = "isNotEqualTo";
    public final static String KEY_EQUALS = "equals";
    public final static String KEY_SMALLER_THAN = "smallerThan";
    public final static String KEY_GREATER_THAN = "greaterThan";
    // functions
    public final static String KEY_HAS = "has";
    public final static String KEY_IS = "is";
    public final static String KEY_PARAM = "param";


    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private HashMap<String, String> properties;
    private HashMap<String, String> parameters;
    private boolean valid;


    public ConditionEvaluator(HashMap<String, String> properties,
                              Context ctx,
                              HashMap<String, String> parameters)
    {
        super(ctx);
        this.parameters = parameters;
        this.properties = properties;
    }

    public Element getBest(List<Element> conditions)
    {
        if(null == conditions)
        {
            return null;
        }
        if(conditions.isEmpty())
        {
            return null;
        }
        valid = true;

        // check condition
        Vector<Element> valids = new Vector<Element>();
        Iterator<Element> it = conditions.iterator();
        while(it.hasNext() && (true == valid))
        {
            Element curE = it.next();
            String conditionText = curE.getAttributeValue(CONDITION_EVALUATOR_CONDITION_ATTRIBUTE_NAME);
            log.trace("evaluating condition {}", conditionText);
            if(true == KEY_TRUE.equals(evaluateConditionParenthesis(conditionText)))
            {
                valids.add(curE);
            }
            else
            {
                log.trace("condition not met");
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
    private String evaluate_Word(String Word)
    {
        // constants
        if(KEY_TRUE.equals(Word))
        {
            return KEY_TRUE;
        }
        if(KEY_FALSE.equals(Word))
        {
            return KEY_FALSE;
        }
        // functions
        if((true == Word.contains("(")) && (true == Word.contains(")")))
        {
            int index_opening_brace = Word.indexOf('(');
            int index_closing_brace = Word.indexOf(')');
            String functionName = Word.substring(0, index_opening_brace);
            String parameter = Word.substring(index_opening_brace + 1, index_closing_brace);
            if(1 > functionName.length())
            {
                // just additional braces -> remove those and evaluate the rest
                return  evaluate_Word(parameter);  // Caution : Recursion! So don't over do it with the unneeded braces!
            }
            switch(functionName)
            {
            case KEY_HAS:
                // Parameter may already be evaluated
                if(KEY_TRUE.equals(parameter))
                {
                    return KEY_TRUE;
                }
                if(KEY_FALSE.equals(parameter))
                {
                    return KEY_FALSE;
                }
                // If not then evaluate now
                if(true == parameter.contains("'"))
                {
                    parameter = parameter.substring(parameter.indexOf("'") + 1, parameter.lastIndexOf("'"));
                }
                String test = properties.get(parameter);
                if(null == test)
                {
                    return KEY_FALSE;
                }
                else
                {
                    return KEY_TRUE;
                }

            case KEY_IS:
                // Parameter may already be evaluated
                if(KEY_TRUE.equals(parameter))
                {
                    return KEY_TRUE;
                }
                if(KEY_FALSE.equals(parameter))
                {
                    return KEY_FALSE;
                }
                // If not then evaluate now
                String res = properties.get(parameter);
                if(null == res)
                {
                    // there is something wrong here
                    valid = false;
                    ctx.addError("ConditionEvaluation",
                            "unknown property : " + parameter);
                    return KEY_FALSE;
                }
                else
                {
                    if(KEY_TRUE.equals(res))
                    {
                        return KEY_TRUE;
                    }
                    else
                    {
                        return KEY_FALSE;
                    }
                }

            case KEY_PARAM:
                // Parameter may already be evaluated
                if(KEY_TRUE.equals(parameter))
                {
                    return KEY_TRUE;
                }
                if(KEY_FALSE.equals(parameter))
                {
                    return KEY_FALSE;
                }
                // If not then evaluate now
                String paramValue = parameters.get(parameter);
                if(null == paramValue)
                {
                    // there is something wrong here
                    valid = false;
                    ctx.addError("ConditionEvaluation",
                            "unknown parameter : " + parameter);
                    return KEY_FALSE;
                }
                else
                {
                    return paramValue;
                }

            default:
                // there is something wrong here
                valid = false;
                ctx.addError("ConditionEvaluation",
                        "unknown function : '" + functionName  + "' in '" + Word + "'");
                return KEY_FALSE;
            }
        }
        //configuration Attributes
        String val = properties.get(Word);
        if(null == val)
        {
            // This happens if we try to evaluate a parameter to a function defined heer (e.g: "has(bla)" )
            return Word;
        }
        else
        {
            return val;
        }
    }

    private boolean isFunctionWord(String Word)
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
        return false;
    }

    private String evaluateConditionText(String conditionText)
    {
        // parse condition
        String[] parts = conditionText.split("\\s"); // all whitespace splits
        if(null == parts)
        {
            return KEY_FALSE;
        }
        if(1 > parts.length)
        {
            return KEY_FALSE;
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
                    first = evaluateFunction(curPart, first, parts[i+1]);
                    i++;
                }
                else
                {
                    // last word missing
                    valid = false;
                    ctx.addError("ConditionEvaluation",
                            "last word missing in : " + conditionText);
                    return KEY_FALSE;
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
                    valid = false;
                    ctx.addError("ConditionEvaluation",
                            "two non function words in : " + conditionText);
                    return KEY_FALSE;
                }
            }
        }
        // finish up
        String result = evaluate_Word(first);
        return result;
    }

    private String evaluateFunction(String function, String first, String second)
    {
        String valOne = evaluate_Word(first);
        String valTwo = evaluate_Word(second);
        switch(function)
        {
        case KEY_AND:
            if((KEY_TRUE.equals(valOne)) & (KEY_TRUE.equalsIgnoreCase(valTwo)))
            {
                return KEY_TRUE;
            }
            else
            {
                return KEY_FALSE;
            }

        case KEY_OR:
            if((KEY_TRUE.equals(valOne)) | (KEY_TRUE.equalsIgnoreCase(valTwo)))
            {
                return KEY_TRUE;
            }
            else
            {
                return KEY_FALSE;
            }

        case KEY_IS_NOT_EQUAL_TO:
            if(valOne.equals(valTwo))
            {
                return KEY_FALSE;
            }
            else
            {
                return KEY_TRUE;
            }

        case KEY_EQUALS:
            if(valOne.equals(valTwo))
            {
                return KEY_TRUE;
            }
            else
            {
                return KEY_FALSE;
            }

        case KEY_SMALLER_THAN:
            try
            {
                int one = Integer.parseInt(valOne);
                int two = Integer.parseInt(valTwo);
                if(one < two)
                {
                    return KEY_TRUE;
                }
                else
                {
                    return KEY_FALSE;
                }
            }
            catch(NumberFormatException e)
            {
                valid = false;
                ctx.addError("ConditionEvaluation",
                        "one invalid number : either '" + valOne + "' or '" + valTwo + "'");
                return KEY_FALSE;
            }

        case KEY_GREATER_THAN:
            try
            {
                int one = Integer.parseInt(valOne);
                int two = Integer.parseInt(valTwo);
                if(one > two)
                {
                    return KEY_TRUE;
                }
                else
                {
                    return KEY_FALSE;
                }
            }
            catch(NumberFormatException e)
            {
                valid = false;
                ctx.addError("ConditionEvaluation",
                        "invalid number : " + valOne + " or " + valTwo);
                return KEY_FALSE;
            }

        default:
            valid = false;
            ctx.addError("ConditionEvaluation",
                    "invalid function : " + function);
            return KEY_FALSE;
        }
    }

    public String evaluateConditionParenthesis(String condition)
    {
        if(null == condition)
        {
            return KEY_FALSE;
        }
        if(false == condition.contains("("))
        {
            return evaluateConditionText(condition);
        }
        else
        {
            int num_openP = 0;
            Vector<StringBuffer> sections = new Vector<StringBuffer>();
            StringBuffer curSection = new StringBuffer();
            sections.add(curSection);

            for(int i = 0; i < condition.length(); i++)
            {
                char c = condition.charAt(i);
                switch(c)
                {
                case '(':
                    curSection.append(c);
                    num_openP ++;

                    try
                    {
                        sections.remove(num_openP);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        // element does not exist
                        // -> no need to delete it
                        // -> we are done here
                    }
                    curSection = new StringBuffer();
                    sections.add(curSection);
                    break;

                case ')':
                    if(0 == num_openP)
                    {
                        valid = false;
                        ctx.addError("ConditionEvaluation",
                                "Parenthesis mismatch in condition : " + condition);
                        return KEY_FALSE;
                    }
                    // else:
                    String sectionResult = evaluateConditionText(curSection.toString());
                    sections.remove(num_openP);
                    num_openP--;
                    curSection = sections.get(num_openP);
                    curSection.append(sectionResult);
                    curSection.append(")");
                    break;

                default:
                    curSection.append(c);
                    break;
                }
            }
            // we parsed all the chars in the condition
            // so first some sanity checks
            if(0 != num_openP)
            {
                valid = false;
                ctx.addError("ConditionEvaluation",
                        "Parenthesis mismatch at end of condition : " + condition);
                return KEY_FALSE;
            }
            if(1 != sections.size())
            {
                valid = false;
                ctx.addError("ConditionEvaluation",
                        "Parenthesis Section mismatch at end of condition : " + condition);
                return KEY_FALSE;
            }
            StringBuffer ResultSection = sections.get(0);
            return evaluateConditionText(ResultSection.toString());
        }
    }

}
