package de.nomagic.puzzler.solution;

import org.junit.Test;

import de.nomagic.puzzler.ContextStub;
import de.nomagic.puzzler.Generator.C_CodeGenerator;
import de.nomagic.puzzler.configuration.Configuration;

public class C_CodeGeneratorTest
{

    @Test
    public void test_generateFor_Null()
    {
        Configuration cfg = new Configuration();
        cfg.setString(Configuration.CFG_DOC_CODE_SRC, "false");
        ContextStub ctx = new ContextStub(cfg);
        C_CodeGenerator dut = new C_CodeGenerator(ctx);
        dut.generateFor(null);
    }

}
