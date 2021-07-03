package de.nomagic.puzzler.solution;

import java.util.Vector;

import de.nomagic.puzzler.Generator.C_FunctionCall;

public class Ago_c_code_stub implements Algo_c_code 
{
	private Vector<String> funcImpl = new Vector<String>();
	private String paramVal = null;

	public Ago_c_code_stub()
	{
		
	}
	
	public void setFunctionImplementation(String val)
	{
		funcImpl.add(val);
	}
	
	@Override
    public String getFunctionImplementation(C_FunctionCall functionToCall)
    {
		if(0 < funcImpl.size())
		{
			return funcImpl.remove(0);
		}
		else
		{
			// no more implementations available.
			return null;
		}
    }
	
	public void setFunctionParameter(String val)
	{
		paramVal = val;
	}
	
	@Override
    public String getFunctionParameterValue(String ParameterName,C_FunctionCall Function)
    {
    	return paramVal;
    }

	
}
