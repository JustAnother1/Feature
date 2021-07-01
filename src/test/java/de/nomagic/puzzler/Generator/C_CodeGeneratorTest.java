package de.nomagic.puzzler.Generator;

import static org.junit.Assert.*;

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
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.ConfiguredAlgorithmStub;

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
        assertEquals(0, logsList.size());
        assertEquals("C_CodeGenerator : Could not read implementation from ConfiguredAlgorithmStub", ctx.getErrors());
        /*
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertEquals("",
                logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        */
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
        assertEquals(1, logsList.size());
        assertEquals("Could not get an Implementation for execute", logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertEquals("C_CodeGenerator : Could not get an Implementation for execute", ctx.getErrors());
        /*
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertEquals("Could not get an Implementation for execute",
                logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        */
    }
}
