package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import java.util.List;

import org.jdom2.CDATA;
import org.jdom2.Element;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.ContextStub;
import de.nomagic.puzzler.FileGetterStub;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.Generator.C_CodeGenerator;
import de.nomagic.puzzler.Generator.C_FunctionCall;
import de.nomagic.puzzler.Generator.Generator;

public class Algo_c_code_implTest {

    // private final Logger testedLog = (Logger)LoggerFactory.getLogger(Algo_c_code.class);
    private final Logger testedLog = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private ListAppender<ILoggingEvent> listAppender;

    @Before
    public void setupTestedLogger()
    {
        listAppender = new ListAppender<>();
        listAppender.start();
        testedLog.setLevel(Level.WARN);
        testedLog.addAppender(listAppender);
    }

    @After
    public void teardownTestedLogger()
    {
    	testedLog.detachAppender(listAppender);
    }

    @Test
    public void getImplementationForNull()
    {
        Algo_c_code cut = new Algo_c_code_impl(null, null);
        String res = cut.getFunctionImplementation(null);

        assertNull(res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }

    @Test
    public void getImplementationForEmptyString()
    {
        Context ctx = new ContextStub();
        AlgorithmInstanceInterface algo = new ConfiguredAlgorithmStub();

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("");
        String res = cut.getFunctionImplementation(fc);

        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNull(res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertEquals("Could not read implementation for  from Algo_c_code_impl for ConfiguredAlgorithmStub !",
                logsList.get(0).getFormattedMessage());
        assertEquals(Level.WARN, logsList.get(0).getLevel());
    }

    @Test
    public void getImplementationForFunction_noCode()
    {
        Context ctx = new ContextStub();
        AlgorithmInstanceInterface algo = new ConfiguredAlgorithmStub();

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);

        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNull(res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertEquals("Could not read implementation for main from Algo_c_code_impl for ConfiguredAlgorithmStub !",
                logsList.get(0).getFormattedMessage());
        assertEquals(Level.WARN, logsList.get(0).getLevel());
    }

    @Test
    public void getImplementationForFunction_noFunction()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Element code = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);

        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNull(res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertEquals("Function call to missing function! (ConfiguredAlgorithmStub, function name : main)",
                logsList.get(0).getFormattedMessage());
        assertEquals(Level.WARN, logsList.get(0).getLevel());
    }

    @Test
    public void getImplementationForFunction_wrongFunction()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Element code = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        Element funcBla = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcBla = funcBla.setAttribute(Algo_c_code_impl.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "bla");
        code = code.addContent(funcBla);
        Element funcNoName = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        code = code.addContent(funcNoName);
        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);

        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNull(res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertEquals("Function call to missing function! (ConfiguredAlgorithmStub, function name : main)",
                logsList.get(0).getFormattedMessage());
        assertEquals(Level.WARN, logsList.get(0).getLevel());
    }

    @Test
    public void getImplementationForFunction_moImplementation()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Element code = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        Element funcBla = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcBla = funcBla.setAttribute(Algo_c_code_impl.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "bla");
        code = code.addContent(funcBla);
        Element funcNoName = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        code = code.addContent(funcNoName);
        Element funcMain = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcMain = funcMain.setAttribute(Algo_c_code_impl.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "main");
        code = code.addContent(funcMain);
        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);

        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNotNull(res);
        assertEquals("", res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }

    @Test
    public void getImplementationForFunction_emptyImplementation()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Element code = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        Element funcBla = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcBla = funcBla.setAttribute(Algo_c_code_impl.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "bla");
        code = code.addContent(funcBla);
        Element funcNoName = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        code = code.addContent(funcNoName);
        Element funcMain = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcMain = funcMain.setAttribute(Algo_c_code_impl.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "main");
        Comment remove = new Comment("increment i by one");
        funcMain = funcMain.addContent(remove);
        CDATA impl = new CDATA("  ");
        funcMain = funcMain.addContent(impl);
        code = code.addContent(funcMain);
        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);

        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNotNull(res);
        assertEquals("", res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }

    @Test
    public void getImplementationForFunction_Comment()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Element code = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        Element funcBla = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcBla = funcBla.setAttribute(Algo_c_code_impl.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "bla");
        code = code.addContent(funcBla);
        Element funcNoName = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        code = code.addContent(funcNoName);
        Element funcMain = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcMain = funcMain.setAttribute(Algo_c_code_impl.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "main");
        Comment remove = new Comment("increment i by one");
        funcMain = funcMain.addContent(remove);
        CDATA impl = new CDATA("i=i++;");
        funcMain = funcMain.addContent(impl);
        code = code.addContent(funcMain);
        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);

        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNotNull(res);
        assertEquals("i=i++;" + System.getProperty("line.separator"), res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }

    @Test
    public void getImplementationForFunction_alternativeFunction()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Element code = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        Element funcBla = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcBla = funcBla.setAttribute(Algo_c_code_impl.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "bla");
        code = code.addContent(funcBla);
        Element funcNoName = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        code = code.addContent(funcNoName);
        Element additional = new Element(Algo_c_code_impl.ALGORITHM_ADDITIONAL_CHILD_NAME);
        Element funcMain = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcMain = funcMain.setAttribute(Algo_c_code_impl.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "main");
        Comment remove = new Comment("increment i by one");
        funcMain = funcMain.addContent(remove);
        CDATA impl = new CDATA("i=i++;");
        funcMain = funcMain.addContent(impl);
        additional = additional.addContent(funcMain);
        code = code.addContent(additional);
        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);

        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNotNull(res);
        assertEquals("i=i++;" + System.getProperty("line.separator"), res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }

    @Test
    public void getImplementationForFunction_invalidChild()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Element code = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        Element funcBla = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcBla = funcBla.setAttribute(Algo_c_code_impl.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "bla");
        code = code.addContent(funcBla);
        Element funcNoName = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        code = code.addContent(funcNoName);
        Element funcMain = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcMain = funcMain.setAttribute(Algo_c_code_impl.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "main");
        Comment remove = new Comment("increment i by one");
        funcMain = funcMain.addContent(remove);
        Element invalid = new Element("somethingCrazy");
        invalid.setText("this is no implementation");
        funcMain = funcMain.addContent(invalid);
        code = code.addContent(funcMain);
        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);

        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNotNull(res);
        assertEquals("", res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertEquals("Ignoring not recognised Element in implementation ! text:  this is no implementation\n"
        		+ " element: [Element: <somethingCrazy/>]",
                logsList.get(0).getFormattedMessage());
        assertEquals(Level.WARN, logsList.get(0).getLevel());
    }

    @Test
    public void getImplementationForFunction_inv_if()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Element code = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        Element funcBla = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcBla = funcBla.setAttribute(Algo_c_code_impl.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "bla");
        code = code.addContent(funcBla);
        Element funcNoName = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        code = code.addContent(funcNoName);
        Element funcMain = new Element(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
        funcMain = funcMain.setAttribute(Algo_c_code_impl.ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME, "main");
        Comment remove = new Comment("increment i by one");
        funcMain = funcMain.addContent(remove);
        Element invalid = new Element(Algo_c_code_impl.ALGORITHM_IF_CHILD_NAME);
        invalid.setText("this is no implementation");
        funcMain = funcMain.addContent(invalid);
        code = code.addContent(funcMain);
        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, code);

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);

        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNotNull(res);
        assertEquals("", res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }


    @Test
    public void getImplementationForFunction_function_call_xml()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Document doc = Tool.getXmlDocumentFrom(
                "<c_code>"
                + "<additional>"
                + "<function name=\"main\">"
                + "<![CDATA[€initialize:initialize()€]]>"
                + "</function>"
                + "</additional>"
                + "</c_code>");

        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, doc.getRootElement());

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);

        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNotNull(res);
        assertEquals("€initialize:initialize()€" + System.getProperty("line.separator"), res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }
    
    @Test
    public void getImplementationForFunction_function_call_forChilds_no_FileGetter()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Document doc = Tool.getXmlDocumentFrom(
                "<c_code>"
                + "<additional>"
                + "<function name=\"main\">"
                + "<forChilds api=\"run\"><![CDATA[€run:execute()€]]></forChilds>"
                + "</function>"
                + "</additional>"
                + "</c_code>");

        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, doc.getRootElement());

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String res = cut.getFunctionImplementation(fc);

        // file getter not needed
        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNotNull(res);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
        assertEquals("", res);
    }
    
    @Test
    public void getImplementationForFunction_function_call_forChilds_no_children()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Document doc = Tool.getXmlDocumentFrom(
                "<c_code>"
                + "<additional>"
                + "<function name=\"main\">"
                + "<forChilds api=\"run\"><![CDATA[€run:execute()€]]></forChilds>"
                + "</function>"
                + "</additional>"
                + "</c_code>");

        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, doc.getRootElement());
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        Element funcB = new Element("function");
        funcB.setAttribute("name", "initialize");
        funcB.setAttribute("type", "required");
        res.addContent(funcB);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs); 

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String impl = cut.getFunctionImplementation(fc);

        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNotNull(impl);
        assertEquals("", impl);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());        
    }
    
    @Test
    public void getImplementationForFunction_function_call_forChilds_has_Childs()
    {
        Context ctx = new ContextStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        Document doc = Tool.getXmlDocumentFrom(
                "<c_code>"
                + "<additional>"
                + "<function name=\"main\">"
                + "<forChilds api=\"run\"><![CDATA[€run:execute()€]]></forChilds>"
                + "</function>"
                + "</additional>"
                + "</c_code>");

        algo.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, doc.getRootElement());
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        Element funcB = new Element("function");
        funcB.setAttribute("name", "initialize");
        funcB.setAttribute("type", "required");
        res.addContent(funcB);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs); 
        ConfiguredAlgorithmStub child = new ConfiguredAlgorithmStub();
        child.setName("someAlgo");
        algo.addChild(child);
        ConfiguredAlgorithmStub child2 = new ConfiguredAlgorithmStub();
        child2.setName("AlgoWithApi");
        child2.setApi("run");
        algo.addChild(child2);

        Algo_c_code cut = new Algo_c_code_impl(ctx, algo);
        C_FunctionCall fc = new C_FunctionCall("main()");
        String impl = cut.getFunctionImplementation(fc);

        assertTrue(ctx.wasSucessful());
        assertEquals("", ctx.getErrors());
        assertNotNull(impl);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
        assertEquals("€AlgoWithApi:execute()€" + System.getProperty("line.separator"), impl);
    }

}
