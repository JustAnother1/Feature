package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.junit.Test;

public class Algo_c_codeTest {

    @Test
    public void getImplementationForNull()
    {
        Algo_c_code cut = new Algo_c_code(null, null);
        String res = cut.getFunctionImplementation(null);
        assertNull(res);
    }

}
