
package de.nomagic.puzzler;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Rule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class PuzzlerMainTest 
{
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	
	
	@Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	
	@Before
	public void setUpStreams()
	{
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}

	@After
	public void cleanUpStreams() 
	{
	    System.setOut(null);
	    System.setErr(null);
	}
	
	/**
	 * Test method for {@link de.nomagic.puzzler.PuzzlerMain#main(java.lang.String[])}.
	 */
	@Test
	public void testNoParameters() 
	{
		String[] args = new String[0];
		exit.expectSystemExitWithStatus(1);
		PuzzlerMain.main(args);
	}
	
	/**
	 * Test method for {@link de.nomagic.puzzler.PuzzlerMain#main(java.lang.String[])}.
	 */
	@Test
	public void testhelpParameter() 
	{
		String[] args = {"-h"};
		exit.expectSystemExitWithStatus(1);
		PuzzlerMain.main(args);
		String help = outContent.toString();
		System.out.println(help);
		assertTrue(5 < help.length());
		assertEquals("", errContent.toString());
	}

}
