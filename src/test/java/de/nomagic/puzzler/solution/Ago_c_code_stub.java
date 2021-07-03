package de.nomagic.puzzler.solution;

import de.nomagic.puzzler.Generator.C_FunctionCall;

public class Ago_c_code_stub implements Algo_c_code 
{
	private String funcImpl = null;
	private String paramVal = null;

	public Ago_c_code_stub()
	{
		
	}
	
	public void setFunctionImplementation(String val)
	{
		funcImpl = val;
	}
	
	@Override
    public String getFunctionImplementation(C_FunctionCall functionToCall)
    {
    	return funcImpl;
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
