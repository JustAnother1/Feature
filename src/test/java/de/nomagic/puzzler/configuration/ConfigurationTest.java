package de.nomagic.puzzler.configuration;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConfigurationTest
{

    @Test
    public void testConfiguration()
    {
        Configuration cfg = new Configuration();
        assertNotNull(cfg);
    }

    @Test
    public void testGetStringNotSet()
    {
        Configuration cfg = new Configuration();
        assertNotNull(cfg);
        String res = cfg.getString("bla");
        assertEquals("", res);
        String[] arr = cfg.getStringsOf("bla");
        assertNotNull(arr);
        assertEquals(0, arr.length);
    }

    @Test
    public void testGetString()
    {
        Configuration cfg = new Configuration();
        assertNotNull(cfg);
        cfg.setString("bla", "123");
        String res = cfg.getString("bla");
        assertEquals("123", res);
        String[] arr = cfg.getStringsOf("bla");
        assertNotNull(arr);
        assertEquals(1, arr.length);
        assertEquals("123", arr[0]);
    }

    @Test
    public void testGetStringOverwrite()
    {
        Configuration cfg = new Configuration();
        assertNotNull(cfg);
        cfg.setString("bla", "123");
        cfg.setString("bla", "456");
        String res = cfg.getString("bla");
        assertEquals("123", res);
        String[] arr = cfg.getStringsOf("bla");
        assertNotNull(arr);
        assertEquals(2, arr.length);
        assertEquals("123", arr[0]);
        assertEquals("456", arr[1]);
    }

    @Test
    public void testGetBool()
    {
        Configuration cfg = new Configuration();
        assertNotNull(cfg);
        cfg.setBool("bla", true);
        boolean res = cfg.getBool("bla");
        assertTrue(res);
    }

    @Test
    public void testGetMissingBool()
    {
        Configuration cfg = new Configuration();
        assertNotNull(cfg);
        cfg.setBool("bla", true);
        boolean res = cfg.getBool("blubb");
        assertFalse(res);
    }

    @Test
    public void testListAllConfigurationVariables()
    {
        String res = Configuration.listAllConfigurationVariables();
        assertEquals("document_code_source=true      : define in comments which algorithm cretaed the source code lines\n"
                   + "embeetle_project=true          : create a Embetle IDE(https://embeetle.com/) project\n"
                   , res);
    }
}
