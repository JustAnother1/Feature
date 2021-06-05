package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.junit.Test;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.ContextStub;
import de.nomagic.puzzler.Generator.C_FunctionCall;

public class Algo_c_codeTest {

    @Test
    public void getImplementationForNull()
    {
        Algo_c_code cut = new Algo_c_code(null, null);
        String res = cut.getFunctionImplementation(null);
        assertNull(res);
    }

    @Test
    public void getImplementationForEmptyString()
    {
        Context ctx = new ContextStub();
        AlgorithmInstanceInterface algo = new ConfiguredAlgorithmStub();
        Algo_c_code cut = new Algo_c_code(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("");
        String res = cut.getFunctionImplementation(fc);
        assertNull(res);
    }

    @Test
    public void getImplementationForFunction()
    {
        Context ctx = new ContextStub();
        AlgorithmInstanceInterface algo = new ConfiguredAlgorithmStub();
        Algo_c_code cut = new Algo_c_code(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);
        assertNull(res);
    }

}
