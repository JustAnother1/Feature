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
	public void testToString() 
	{
		Configuration cfg = new Configuration();
		Context ctx = new Context(cfg);
		Algorithm cut = new Algorithm(null, ctx);
		assertSame("ERROR: unconfigured Algorithm", cut.toString());
	}

	@Test
	public void testGetFromFile() 
	{
		assertNull(Algorithm.getFromFile(null, null));
		fail("Not yet implemented");
	}

	@Test
	public void testHasApi() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetChild() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetChildren() {
		fail("Not yet implemented");
	}

}
