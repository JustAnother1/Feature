package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.jdom2.Element;
import org.junit.Test;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.configuration.Configuration;

public class AlgorithmTest {

	@Test
	public void testAlgorithm() 
	{
		Configuration cfg = new Configuration();
		Context ctx = new Context(cfg);
		Algorithm cut = new Algorithm(null, ctx);
		assertFalse(cut.hasApi("bla"));
	}

	@Test
	public void testToString_null() 
	{
		Configuration cfg = new Configuration();
		Context ctx = new Context(cfg);
		Algorithm cut = new Algorithm(null, ctx);
		assertSame("ERROR: unconfigured Algorithm", cut.toString());
	}
	
	@Test
	public void testToString() 
	{
		Configuration cfg = new Configuration();
		Context ctx = new Context(cfg);
		Element root = new Element("testElement");
		root.setAttribute(Algorithm.ALGORITHM_NAME_ATTRIBUTE_NAME, "blablabla");
		Algorithm cut = new Algorithm(root, ctx);
		assertEquals("Algorithm blablabla", cut.toString());
	}

	@Test
	public void testGetFromFile_null() 
	{
		assertNull(Algorithm.getFromFile(null, null, null));
	}
	
	@Test
	public void testGetFromFile_badElement() 
	{
		Element root = new Element("bad");
		assertNull(Algorithm.getFromFile(root, null, null));
	}
	
	@Test
	public void testGetFromFile_noLib() 
	{
		Element root = new Element("good");
		root.setAttribute(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME, "bla");
		assertNull(Algorithm.getFromFile(root, null, null));
	}
	
	@Test
	public void testGetFromFile_noImplementation() 
	{
		Element root = new Element("good");
		root.setAttribute(Algorithm.ALGORITHM_REFFERENCE_ATTRIBUTE_NAME, "bla");
		LibraryStub libstub = new LibraryStub();
		libstub.setResult(null);
		Algorithm res = Algorithm.getFromFile(root, libstub, null);
		assertNotNull(res);
		assertSame("ERROR: unconfigured Algorithm", res.toString());
	}

}
