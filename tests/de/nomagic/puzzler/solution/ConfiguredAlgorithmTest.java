package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConfiguredAlgorithmTest {

	@Test
	public void testConfiguredAlgorithm() 
	{
		ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
		assertEquals("dut", dut.getName());
	}
}
