package de.nomagic.puzzler.solution;

import java.util.ArrayList;

import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.FileGroup.CElement;
import de.nomagic.puzzler.FileGroup.FunctionHandler;

public class Function extends CElement
{
    public static final String FUNCTION_NAME_ATTRIBUTE_NAME = "name";
    public static final String FUNCTION_TYPE_ATTRIBUTE_NAME = "type";
    public static final String FUNCTION_REQUIRED_TYPE = "required";
    public static final String FUNCTION_RESULT_ATTRIBUTE_NAME = "result";
    public static final String FUNCTION_PARAMETER_ATTRIBUTE_NAME = "param";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String result;
    private ArrayList<String> parameterDefinitions = new ArrayList<String>();
    private String name = "";
    private String comment = null;
    private boolean required = false;
    private final Element tag;
    private String implementation = null;

    public Function(Element tag)
    {
        this.tag = tag;
        if(null == tag)
        {
            log.error("Tried to create function without XML tag!");
            return;
        }
        // name
        name = tag.getAttributeValue(FUNCTION_NAME_ATTRIBUTE_NAME);
        // required
        String type = tag.getAttributeValue(FUNCTION_TYPE_ATTRIBUTE_NAME);
        if(true == FUNCTION_REQUIRED_TYPE.equals(type))
        {
            required = true;
        }
        // result
        result = tag.getAttributeValue(FUNCTION_RESULT_ATTRIBUTE_NAME);
        // Parameters
        int i = 0;
        String paramName;
        String paramType;
        do
        {
            paramName = tag.getAttributeValue(FUNCTION_PARAMETER_ATTRIBUTE_NAME + i + "_" + FUNCTION_NAME_ATTRIBUTE_NAME);
            paramType = tag.getAttributeValue(FUNCTION_PARAMETER_ATTRIBUTE_NAME + i + "_" + FUNCTION_TYPE_ATTRIBUTE_NAME);
            if((null != paramName) && (null != paramType))
            {
                parameterDefinitions.add(paramType + " " + paramName);
            }
            i++;
        }
        while((null != paramName) && (null != paramType));
    }

    public boolean sameAs(Function next)
    {
        if(null == next)
        {
            return false;
        }

        if(false == this.name.equals(next.name))
        {
            log.trace("name differs");
            return false;
        }

        if(null == result)
        {
            if(null != next.result)
            {
                log.trace("result differs");
                return false;
            }
        }
        else
        {
            if(false == this.result.equals(next.result))
            {
                log.trace("result differs");
                return false;
            }
        }

        if(null == implementation)
        {
            if(null != next.implementation)
            {
                log.trace("implementation differs");
                return false;
            }
        }
        else
        {
            if(false == this.implementation.equals(next.implementation))
            {
                log.trace("implementation differs");
                return false;
            }
        }

        if(false == this.parameterDefinitions.equals(next.parameterDefinitions))
        {
            log.trace("parameter_definitions differs");
            return false;
        }

        if(this.required != next.required)
        {
            log.trace("required differs");
            return false;
        }

        if(null == tag)
        {
            if(null != next.tag)
            {
                log.trace("tag differs null - {}", next.tag);
                return false;
            }
        }
        else
        {
            if(false == this.tag.equals(next.tag))
            {
                XMLOutputter xout = new XMLOutputter();
                String my = xout.outputString(this.tag);
                String other = xout.outputString(next.tag);
                if(false == my.equals(other))
                {
                    log.trace("tag differs {} - {}", my, other);
                    return false;
                }
            }
        }
        // comment is allowed to be different
        return true;
    }

    /**
     *
     * @return true = this function must be present to fulfill the API; false= this function may be missing
     */
    public boolean isRequired()
    {
        return required;
    }

    public String getName()
    {
        return name;
    }

    private String getDeclaration()
    {
        StringBuilder sb = new StringBuilder();
        if(null == result)
        {
            sb.append("void");
        }
        else
        {
            sb.append(result);
        }
        sb.append(" ");
        sb.append(name);
        sb.append("(");
        if(true == parameterDefinitions.isEmpty())
        {
            sb.append("void");
        }
        else
        {
            sb.append(parameterDefinitions.get(0));
            for(int i = 1; i < parameterDefinitions.size(); i++)
            {
                sb.append(", ");
                sb.append(parameterDefinitions.get(i));
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public void addComment(String additionalInformation)
    {
        if(null == comment)
        {
            comment = additionalInformation;
        }
        else
        {
            comment = comment  + " "+ additionalInformation;
        }
    }

    public String getComment()
    {
        return comment;
    }

    @Override
    public String getCode(int type, String lineSperator)
    {
        if(null == tag)
        {
            return null;
        }

        if(FunctionHandler.TYPE_DECLARATION == type)
        {
            if(null == comment)
            {
                return  getDeclaration() + ";";
            }
            else
            {
                return  getDeclaration() + "; // from " + comment + lineSperator;
            }
        }
        else if(FunctionHandler.TYPE_IMPLEMENTATION == type)
        {
            if(null == implementation)
            {
                if(null == comment)
                {
                    return  getDeclaration() + lineSperator
                            + tag.getText() + lineSperator;
                }
                else
                {
                    return  getDeclaration() + " // from " + comment + lineSperator
                            + tag.getText() + lineSperator;
                }
            }
            else
            {
                if(null == comment)
                {
                    return  getDeclaration() + lineSperator
                            + "{"            + lineSperator
                            + implementation + lineSperator
                            + "}"            + lineSperator;
                }
                else
                {
                    return  getDeclaration() + " // from " + comment + lineSperator
                            + "{"            + lineSperator
                            + implementation + lineSperator
                            + "}"            + lineSperator;
                }
            }
        }
        else
        {
            log.error("Invalid Type requested!");
            return null;
        }
    }

    public void setImplementation(String implementation)
    {
        this.implementation = implementation;
    }

}
