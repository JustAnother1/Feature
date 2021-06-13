package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.jdom2.CDATA;
import org.jdom2.Element;
import org.jdom2.Comment;
import org.junit.Test;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.ContextStub;
import de.nomagic.puzzler.Generator.C_CodeGenerator;
import de.nomagic.puzzler.Generator.C_FunctionCall;
import de.nomagic.puzzler.Generator.Generator;

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
    public void getImplementationForFunction_noCode()
    {
        Context ctx = new ContextStub();
        AlgorithmInstanceInterface algo = new ConfiguredAlgorithmStub();
        Algo_c_code cut = new Algo_c_code(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);
        assertNull(res);
    }

    @Test
    public void getImplementationForFunction_noFunction()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Element code = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);
        Algo_c_code cut = new Algo_c_code(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);
        assertNull(res);
    }

    @Test
    public void getImplementationForFunction_wrongFunction()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Element code = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        Element funcBla = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcBla = funcBla.setAttribute(Algo_c_code.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "bla");
        code = code.addContent(funcBla);
        Element funcNoName = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        code = code.addContent(funcNoName);
        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);
        Algo_c_code cut = new Algo_c_code(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);
        assertNull(res);
    }

    @Test
    public void getImplementationForFunction_moImplementation()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Element code = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        Element funcBla = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcBla = funcBla.setAttribute(Algo_c_code.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "bla");
        code = code.addContent(funcBla);
        Element funcNoName = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        code = code.addContent(funcNoName);
        Element funcMain = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcMain = funcMain.setAttribute(Algo_c_code.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "main");
        code = code.addContent(funcMain);
        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);
        Algo_c_code cut = new Algo_c_code(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);
        assertNotNull(res);
        assertEquals("", res);
    }

    @Test
    public void getImplementationForFunction_Comment()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Element code = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        Element funcBla = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcBla = funcBla.setAttribute(Algo_c_code.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "bla");
        code = code.addContent(funcBla);
        Element funcNoName = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        code = code.addContent(funcNoName);
        Element funcMain = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcMain = funcMain.setAttribute(Algo_c_code.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "main");
        Comment remove = new Comment("increment i by one");
        funcMain = funcMain.addContent(remove);
        CDATA impl = new CDATA("i=i++;");
        funcMain = funcMain.addContent(impl);
        code = code.addContent(funcMain);
        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);
        Algo_c_code cut = new Algo_c_code(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);
        assertNotNull(res);
        assertEquals("i=i++;" + System.getProperty("line.separator"), res);
    }

}
