package de.nomagic.puzzler.FileGroup;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileGroupTest {

	@Test
	public void testFileGroup() 
	{
		FileGroup fg = new FileGroup();
		assertNotNull(fg);
	}

	@Test
	public void testNumEntries() 
	{
		FileGroup fg = new FileGroup();
		assertNotNull(fg);
		assertEquals(0, fg.numEntries());
		fg.add(null);
		assertEquals(0, fg.numEntries());
		EmptyFolder dir = new EmptyFolder("bla/");
		fg.add(dir);
		assertEquals(1, fg.numEntries());
	}

}
