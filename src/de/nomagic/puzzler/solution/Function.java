package de.nomagic.puzzler.solution;

import java.util.Vector;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Function
{
    public final static String FUNCTION_NAME_ATTRIBUTE_NAME = "name";
    public final static String FUNCTION_TYPE_ATTRIBUTE_NAME = "type";
    public final static String FUNCTION_REQUIRED_TYPE = "required";
    public final static String FUNCTION_RESULT_ATTRIBUTE_NAME = "result";
    public final static String FUNCTION_PARAMETER_ATTRIBUTE_NAME = "param";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private DataType result = DataType.DT_void;
    private Vector<DataType> parameters = new Vector<DataType>();
    private String name = "";
    private boolean required = false;


    /**
     *
     * @param tag
     *
     * Example Tags:
     * <function name="execute" type="required" />
     * <function name="getValue"  type="required" result="bool" />
     * <function name="setValue"  type="required" param="bool" />
     */
    public Function(Element tag)
    {
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
        String res = tag.getAttributeValue(FUNCTION_RESULT_ATTRIBUTE_NAME);
        result = DataType.decode(res);
        // Parameters
        String param = tag.getAttributeValue(FUNCTION_PARAMETER_ATTRIBUTE_NAME);
        int i = 0;
        while(null != param)
        {
            parameters.addElement(DataType.decode(param));
            i++;
            param = tag.getAttributeValue(FUNCTION_PARAMETER_ATTRIBUTE_NAME + i);
        }
    }

    public boolean isRequired()
    {
        return required;
    }

    public String getName()
    {
        return name;
    }

    public String getDeclaration()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(result.toString());
        sb.append(" ");
        sb.append(name);
        sb.append("(");
        if(0 == parameters.size())
        {
            sb.append("void");
        }
        else
        {
            sb.append(parameters.get(0).toString());
            sb.append(" ");
            sb.append("param");
            if(1 < parameters.size())
            {
                for(int i = 1; i < parameters.size(); i++)
                {
                    sb.append(", ");
                    sb.append(parameters.get(i).toString());
                    sb.append(" ");
                    sb.append("param" + i);
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }

}
