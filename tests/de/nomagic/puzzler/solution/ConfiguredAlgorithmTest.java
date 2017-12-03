package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.junit.Test;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.configuration.Configuration;

public class ConfiguredAlgorithmTest 
{
	@Test
	public void testConfiguredAlgorithm() 
	{
		ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
		assertEquals("dut", dut.getName());
	}
	
	@Test
	public void testGetTreeFrom_null_null() 
	{
		assertNull(ConfiguredAlgorithm.getTreeFrom(null, null));
	}
	
	@Test
	public void testGetTreeFrom_noSolution() 
	{
		Configuration cfg = new Configuration();
		Context ctx = new Context(cfg);
		assertNull(ConfiguredAlgorithm.getTreeFrom(ctx, null));
	}
	
	@Test
	public void testGetTreeFrom_Solution() 
	{
		Configuration cfg = new Configuration();
		Context ctx = new Context(cfg);
		Solution s = new Solution(ctx);
		ctx.addSolution(s);
		assertNull(ConfiguredAlgorithm.getTreeFrom(ctx, null));
	}
	
	@Test
	public void testToString() 
	{
		ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
		assertEquals("dut", dut.getName());
		assertEquals("ConfiguredAlgorithm dut(null)", dut.toString());
	}
	
	@Test
	public void testGetBuildIn_bla() 
	{
		ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
		assertEquals("dut", dut.getName());
		assertNull(dut.getBuildIn("bla"));
	}
	
	@Test
	public void testGetBuildIn_numChilds() 
	{
		ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
		assertEquals("dut", dut.getName());
		assertEquals("0", dut.getBuildIn(ConfiguredAlgorithm.BUILD_IN_NUM_OF_CHILDS));
	}
	
	@Test
	public void testDumpParameter() 
	{
		ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
		assertEquals("dut", dut.getName());
		assertEquals("Parameter:\n", dut.dumpParameter());
	}
	
	@Test
	public void testGetParameter() 
	{
		ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
		assertEquals("dut", dut.getName());
		assertNull(dut.getParameter("bla"));
	}
	
	@Test
	public void testDumpProperty() 
	{
		ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
		assertEquals("dut", dut.getName());
		assertEquals("Properties:\n", dut.dumpProperty());
	}
	
	@Test
	public void testGetProperty() 
	{
		ConfiguredAlgorithm dut = new ConfiguredAlgorithm("dut", null, null, null);
		assertEquals("dut", dut.getName());
		assertNull(dut.getProperty("bla"));
	}
}
