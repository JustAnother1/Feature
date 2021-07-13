package de.nomagic.puzzler.Generator;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom2.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.nomagic.puzzler.ContextStub;
import de.nomagic.puzzler.FileGetterStub;
import de.nomagic.puzzler.Environment.EnvironmentStub;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.CFile;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.Ago_c_code_stub;
import de.nomagic.puzzler.solution.ConfiguredAlgorithmStub;
import de.nomagic.puzzler.solution.SolutionStub;

public class C_CodeGeneratorTest
{
    // private final Logger testedLog = (Logger)LoggerFactory.getLogger(C_CodeGenerator.class);
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
    public void testCCodeGenerator()
    {
        C_CodeGenerator gen = new C_CodeGenerator(null);
        assertNotNull(gen);
    }
    
    @Test
    public void testCCodeGenerator_noCfg()
    {
        ContextStub ctx = new ContextStub(null);
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
    }

    @Test
    public void testLanguageName()
    {
        C_CodeGenerator gen = new C_CodeGenerator(null);
        assertEquals("C", gen.getLanguageName());
    }

    @Test
    public void testGenerateForNull()
    {
        ContextStub ctx = new ContextStub(new Configuration());
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(null);
        assertNull(fg);
    }
    
    @Test
    public void testGenerateForNull_cfg()
    {
        Configuration cfg = new Configuration();
        cfg.setString(Configuration.CFG_DOC_CODE_SRC, "false");
        ContextStub ctx = new ContextStub(cfg);
        C_CodeGenerator dut = new C_CodeGenerator(ctx);
        dut.generateFor(null);
    }
    

    @Test
    public void testGenerateForNotRoot()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);
        assertNull(fg);
        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }
    
    @Test
    public void testGenerateForNotRoot_butEnvironment()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        ctx.addEnvironment(new EnvironmentStub());
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);
        assertNull(fg);
        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }

    @Test
    public void testGenerateForNotRoot_butEnvironment_rootApi()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");
        ctx.addEnvironment(e);
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);
        assertNull(fg);
        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }
    
    @Test
    public void testGenerateForNotRoot_butEnvironment_rootApi_fg()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        ctx.addFileGetter(fgs);  
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);
        assertNull(fg);
        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }
    
    @Test
    public void testGenerateForNotRoot_butEnvironment_rootApi_fgRes()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);  
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);
        assertNull(fg);
        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }
    
    @Test
    public void testGenerateForNotRoot_butEnvironment_rootApi_fgRes_algoAi()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);  
        cas.setApi("run");
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);
        
        assertNull(fg);
        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertEquals("Could not get required function for the API null", logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertEquals("C_CodeGenerator : Could not get required function for the API null\n", ctx.getErrors());
    }
    
    @Test
    public void testGenerateForNotRoot_api_func()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);  
        cas.setApi("run");
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);
        
        assertNull(fg);
        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());
        assertEquals("The algorithm ConfiguredAlgorithmStub does not have C-code!", logsList.get(0).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertEquals("Could not get an Implementation for execute", logsList.get(1).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(1).getLevel());
        assertEquals("C_CodeGenerator : Could not get an Implementation for execute\n", ctx.getErrors());
        /*
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertEquals("Could not get an Implementation for execute",
                logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        */
    }
    
    @Test
    public void testGenerateFor_hasAlgoCodeClass()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);  
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        cas.setAlgo_c_code(code);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);
        
        assertNull(fg);
        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertEquals("Could not get an Implementation for execute", logsList.get(0).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertEquals("C_CodeGenerator : Could not get an Implementation for execute\n", ctx.getErrors());
    }
    
    @Test
    public void testGenerateFor_hasImplementation()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);  
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("printf(\"Hello World!\");" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);
        
        assertNull(fg);
        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
        assertEquals("C_CodeGenerator : Could not read implementation from ConfiguredAlgorithmStub\n", ctx.getErrors());
    }
    
    @Test
    public void testGenerateFor_has_code_eleemnt()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);  
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("printf(\"Hello World!\");" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertTrue(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
        assertEquals("", ctx.getErrors());
        assertNotNull(fg);
        assertEquals(1, fg.numEntries());
        AbstractFile main = fg.getFileWithName("main.c");
        assertNotNull(main);
        assertTrue(main instanceof CFile);
        // CFile c_main = (CFile)main;
        ByteArrayOutputStream codeFile = new ByteArrayOutputStream();
        try 
        {
			main.writeToStream(codeFile);
			String sourceCode =  codeFile.toString("UTF8");
			assertTrue(sourceCode.contains("printf(\"Hello World!\");"));
			assertTrue(sourceCode.contains("execute"));
		}
        catch (IOException e1) 
        {
			e1.printStackTrace();
			fail("Could not read generated main.c");
		}
    }
    
    @Test
    public void testGenerateFor_needs_two_functions()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
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
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("printf(\"Hello World!\");" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertEquals("Could not get an Implementation for initialize", logsList.get(0).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertEquals("C_CodeGenerator : Could not get an Implementation for initialize\n", ctx.getErrors());
        assertNull(fg);
    }
    
    @Test
    public void testGenerateFor_has_two_functions()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
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
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("printf(\"Hello World!\");" + System.getProperty("line.separator"));
        code.setFunctionImplementation("i++;" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertTrue(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
        assertEquals("", ctx.getErrors());
        assertNotNull(fg);
        assertEquals(1, fg.numEntries());
        AbstractFile main = fg.getFileWithName("main.c");
        assertNotNull(main);
        assertTrue(main instanceof CFile);
        // CFile c_main = (CFile)main;
        ByteArrayOutputStream codeFile = new ByteArrayOutputStream();
        try 
        {
			main.writeToStream(codeFile);
			String sourceCode =  codeFile.toString("UTF8");
			assertTrue(sourceCode.contains("printf(\"Hello World!\");"));
			assertTrue(sourceCode.contains("i++;"));
			assertTrue(sourceCode.contains("initialize"));
			assertTrue(sourceCode.contains("execute"));
		}
        catch (IOException e1) 
        {
			e1.printStackTrace();
			fail("Could not read generated main.c");
		}
    }
    
    @Test
    public void testGenerateFor_replacement_missing()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);  
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("printf(€message€);" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertEquals("Could not get an Implementation for execute", logsList.get(0).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertEquals("C_CodeGenerator : Invalid parameter requested : message\n"
        		+ "C_CodeGenerator : available parameters: null\n"
        		+ "C_CodeGenerator : available properties: null\n"
        		+ "C_CodeGenerator : request was: €message€\n"
        		+ "C_CodeGenerator : Could not get an Implementation for execute\n", ctx.getErrors());
        assertNull(fg);
    }

    @Test
    public void testGenerateFor_replacement_buildIn()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);  
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("printf(€message€);" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        cas.addBuildIn("message", "\"Hello World!\"");
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertTrue(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
        assertEquals("", ctx.getErrors());
        assertNotNull(fg);
        assertEquals(1, fg.numEntries());
        AbstractFile main = fg.getFileWithName("main.c");
        assertNotNull(main);
        assertTrue(main instanceof CFile);
        // CFile c_main = (CFile)main;
        ByteArrayOutputStream codeFile = new ByteArrayOutputStream();
        try 
        {
			main.writeToStream(codeFile);
			String sourceCode =  codeFile.toString("UTF8");
			assertTrue(sourceCode.contains("printf(\"Hello World!\");"));
			assertTrue(sourceCode.contains("execute"));
		}
        catch (IOException e1) 
        {
			e1.printStackTrace();
			fail("Could not read generated main.c");
		}
    }
    
    @Test
    public void testGenerateFor_replacement_parameter()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);  
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("printf(€message€);" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        cas.addParameter("message", "\"Hello World!\"");
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertTrue(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
        assertEquals("", ctx.getErrors());
        assertNotNull(fg);
        assertEquals(1, fg.numEntries());
        AbstractFile main = fg.getFileWithName("main.c");
        assertNotNull(main);
        assertTrue(main instanceof CFile);
        // CFile c_main = (CFile)main;
        ByteArrayOutputStream codeFile = new ByteArrayOutputStream();
        try 
        {
			main.writeToStream(codeFile);
			String sourceCode =  codeFile.toString("UTF8");
			assertTrue(sourceCode.contains("printf(\"Hello World!\");"));
			assertTrue(sourceCode.contains("execute"));
		}
        catch (IOException e1) 
        {
			e1.printStackTrace();
			fail("Could not read generated main.c");
		}
    }
    
    @Test
    public void testGenerateFor_func()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);  
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("€initialize:initialize()€" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());
        assertEquals("No solution available in this context !", logsList.get(0).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertEquals("Could not get an Implementation for execute", logsList.get(1).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(1).getLevel());
        assertEquals("C_CodeGenerator : Could not get an Implementation for execute\n", ctx.getErrors());
        assertNull(fg);
    }
    

    @Test
    public void testGenerateFor_Solution()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);  
        SolutionStub s = new SolutionStub();
        ctx.addSolution(s);
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("€initialize:initialize()€" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());
        assertEquals("Could not get an Implementation for initialize:initialize()", logsList.get(0).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertEquals("Could not get an Implementation for execute", logsList.get(1).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(1).getLevel());
        assertEquals("C_CodeGenerator : Function call to null Algorithm !\n"
        		+ "C_CodeGenerator : Could not get an Implementation for initialize:initialize()\n"       		
        		+ "C_CodeGenerator : Could not get an Implementation for execute\n", ctx.getErrors());
        assertNull(fg);
    }

    @Test
    public void testGenerateFor_Algo_wrong_API()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);
        SolutionStub s = new SolutionStub();
        ctx.addSolution(s);
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("€initialize:initialize()€" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        cas.addChildWithApi("initialize", algo);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(5, logsList.size());
        assertEquals("C_CodeGenerator : Function call to wrong API!(API: initialize)", logsList.get(0).getFormattedMessage());
        assertEquals(Level.WARN, logsList.get(0).getLevel());
        assertEquals("valid APIs : null", logsList.get(1).getFormattedMessage());
        assertEquals(Level.WARN, logsList.get(1).getLevel());
        assertEquals("Function called: initialize", logsList.get(2).getFormattedMessage());
        assertEquals(Level.WARN, logsList.get(2).getLevel());
        assertEquals("Could not get an Implementation for initialize:initialize() from ConfiguredAlgorithmStub", logsList.get(3).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(3).getLevel());
        assertEquals("Could not get an Implementation for execute", logsList.get(4).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(4).getLevel());
        assertEquals("C_CodeGenerator : Could not get an Implementation for initialize:initialize() from ConfiguredAlgorithmStub\n"
        		+ "C_CodeGenerator : Could not get an Implementation for execute\n", ctx.getErrors());
        assertNull(fg);
    }
    
    @Test
    public void testGenerateFor_Algo_wrong_root_API()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "initialize");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);
        SolutionStub s = new SolutionStub();
        ctx.addSolution(s);
        cas.setApi("initialize");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("€initialize:initialize()€" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        cas.addChildWithApi("initialize", algo);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
        assertEquals("C_CodeGenerator : ConfiguredAlgorithmStub : Root element of the solution is not an run !\n", ctx.getErrors());
        assertNull(fg);
    }
    
    @Test
    public void testGenerateFor_no_matching_algo()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);
        SolutionStub s = new SolutionStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        s.addAlgorithm("execute", algo);
        ctx.addSolution(s);
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("€run:execute()€" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());
        assertEquals("Could not get an Implementation for run:execute()", logsList.get(0).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertEquals("Could not get an Implementation for execute", logsList.get(1).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(1).getLevel());        
        assertEquals("C_CodeGenerator : Function call to null Algorithm !\n"
        		+ "C_CodeGenerator : Could not get an Implementation for run:execute()\n"
        		+ "C_CodeGenerator : Could not get an Implementation for execute\n", ctx.getErrors());
        assertNull(fg);
    }
    
    @Test
    public void testGenerateFor_Algo_no_impl()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);
        SolutionStub s = new SolutionStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        s.addAlgorithm("execute", algo);
        ctx.addSolution(s);
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("€run:execute()€" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        ConfiguredAlgorithmStub runStub = new ConfiguredAlgorithmStub();
        cas.addChildWithApi("run", runStub);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(5, logsList.size());
        assertEquals("C_CodeGenerator : Function call to wrong API!(API: run)", logsList.get(0).getFormattedMessage());
        assertEquals(Level.WARN, logsList.get(0).getLevel());
        assertEquals("valid APIs : null", logsList.get(1).getFormattedMessage());
        assertEquals(Level.WARN, logsList.get(1).getLevel());
        assertEquals("Function called: execute", logsList.get(2).getFormattedMessage());
        assertEquals(Level.WARN, logsList.get(2).getLevel());
        assertEquals("Could not get an Implementation for run:execute() from ConfiguredAlgorithmStub", logsList.get(3).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(3).getLevel());
        assertEquals("Could not get an Implementation for execute", logsList.get(4).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(4).getLevel());
        assertEquals("C_CodeGenerator : Could not get an Implementation for run:execute() from ConfiguredAlgorithmStub\n"
        		+ "C_CodeGenerator : Could not get an Implementation for execute\n", ctx.getErrors());
        assertNull(fg);
    }
    
    
    @Test
    public void testGenerateFor_Algo_no_C_Code()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);
        SolutionStub s = new SolutionStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        s.addAlgorithm("execute", algo);
        ctx.addSolution(s);
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("€run:execute()€" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        ConfiguredAlgorithmStub runStub = new ConfiguredAlgorithmStub();
        runStub.setApi("run");
        cas.addChildWithApi("run", runStub);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertFalse(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(3, logsList.size());
        assertEquals("The algorithm ConfiguredAlgorithmStub does not have C-code!", logsList.get(0).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertEquals("Could not get an Implementation for run:execute() from ConfiguredAlgorithmStub", logsList.get(1).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(1).getLevel());
        assertEquals("Could not get an Implementation for execute", logsList.get(2).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(2).getLevel());        
        assertEquals("C_CodeGenerator : Could not get an Implementation for run:execute() from ConfiguredAlgorithmStub\n"
        		+ "C_CodeGenerator : Could not get an Implementation for execute\n", ctx.getErrors());
        assertNull(fg);
    }
    
    @Test
    public void testGenerateFor_Algo_OK()
    {
        ConfiguredAlgorithmStub cas = new ConfiguredAlgorithmStub();
        ContextStub ctx = new ContextStub(new Configuration());
        EnvironmentStub e = new EnvironmentStub();
        e.setRootApi("run");      		
        ctx.addEnvironment(e);
        FileGetterStub fgs = new FileGetterStub();
        Element res = new Element("api");
        res.setAttribute("name", "run");
        Element func = new Element("function");
        func.setAttribute("name", "execute");
        func.setAttribute("type", "required");
        res.addContent(func);
        fgs.setGetFtromFileResult(res);
        ctx.addFileGetter(fgs);
        SolutionStub s = new SolutionStub();
        ConfiguredAlgorithmStub algo = new ConfiguredAlgorithmStub();
        s.addAlgorithm("execute", algo);
        ctx.addSolution(s);
        cas.setApi("run");
        Ago_c_code_stub code = new Ago_c_code_stub();
        code.setFunctionImplementation("€run:execute()€" + System.getProperty("line.separator"));
        cas.setAlgo_c_code(code);
        Element add = new Element(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
        cas.addAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME, add);
        ConfiguredAlgorithmStub runStub = new ConfiguredAlgorithmStub();
        runStub.setApi("run");
        Ago_c_code_stub runCode = new Ago_c_code_stub();
        runCode.setFunctionImplementation("printf(\"Hello World!\");" + System.getProperty("line.separator"));
        runStub.setAlgo_c_code(runCode);
        cas.addChildWithApi("run", runStub);
        
        C_CodeGenerator gen = new C_CodeGenerator(ctx);
        assertNotNull(gen);
        FileGroup fg = gen.generateFor(cas);

        assertTrue(ctx.wasSucessful());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
        assertEquals("", ctx.getErrors());
        assertNotNull(fg);
        assertEquals(1, fg.numEntries());
        AbstractFile main = fg.getFileWithName("main.c");
        assertNotNull(main);
        assertTrue(main instanceof CFile);
        // CFile c_main = (CFile)main;
        ByteArrayOutputStream codeFile = new ByteArrayOutputStream();
        try 
        {
			main.writeToStream(codeFile);
			String sourceCode =  codeFile.toString("UTF8");
			assertTrue(sourceCode.contains("printf(\"Hello World!\");"));
			assertTrue(sourceCode.contains("execute"));
		}
        catch (IOException e1) 
        {
			e1.printStackTrace();
			fail("Could not read generated main.c");
		}
    }
    
}
