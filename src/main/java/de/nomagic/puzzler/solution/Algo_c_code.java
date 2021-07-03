package de.nomagic.puzzler.solution;

import de.nomagic.puzzler.Generator.C_FunctionCall;

public interface Algo_c_code
{
    String getFunctionImplementation(C_FunctionCall functionToCall);
    String getFunctionParameterValue(String ParameterName,C_FunctionCall Function);
}
