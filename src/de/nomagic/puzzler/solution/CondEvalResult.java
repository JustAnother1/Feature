package de.nomagic.puzzler.solution;

public class CondEvalResult
{
    private boolean valid = false;
    private String result = null;
    private StringBuilder error = null;

    public CondEvalResult()
    {
        // defaults already set.
    }

    public void setResult(String value)
    {
        result = value;
    }

    public void setResultValid(String value)
    {
        result = value;
        valid = true;
    }

    public void setValid(boolean value)
    {
        valid = value;
    }

    public void addErrorLine(String msg)
    {
        if(null == error)
        {
            error = new StringBuilder();
        }
        error.append(msg);
    }

    public boolean isValid()
    {
        return valid;
    }

    public String getResult()
    {
        return result;
    }

    public String getErrors()
    {
        if(null == error)
        {
            return "";
        }
        else
        {
            return error.toString();
        }
    }

}
