package de.nomagic.puzzler;

import static org.junit.Assert.*;

import org.junit.Test;

import de.nomagic.puzzler.configuration.Configuration;

public class CommandLineParserTest {

    @Test
    public void testGetConfig()
    {
        CommandLineParser cut = new CommandLineParser();
        assertNotNull(cut.getConfiguration());
    }

    @Test
    public void testParse_null()
    {
        CommandLineParser cut = new CommandLineParser();
        assertFalse(cut.parse(null));
    }

    @Test
    public void testParse_no_parameter()
    {
        CommandLineParser cut = new CommandLineParser();
        String[] parameters = {};
        assertFalse(cut.parse(parameters));
    }

    @Test
    public void testParse_invalid()
    {
        CommandLineParser cut = new CommandLineParser();
        String[] parameters = {"invalid"};
        assertFalse(cut.parse(parameters));
    }

    @Test
    public void testParse_output()
    {
        CommandLineParser cut = new CommandLineParser();
        String[] parameters = {"invalid.xml"};
        assertFalse(cut.parse(parameters));
        Configuration cfg = cut.getConfiguration();
        String prjName = cfg.getString(Configuration.PROJECT_FILE_CFG);
        assertEquals("invalid", prjName);
    }

    @Test
    public void testParse_help()
    {
        CommandLineParser cut = new CommandLineParser();
        String[] parameters = {"-h"};
        assertFalse(cut.parse(parameters));
    }

    @Test
    public void testParse_ziptoout()
    {
        CommandLineParser cut = new CommandLineParser();
        String[] parameters = {"important.xml", "--zip_to_stdout"};
        assertTrue(cut.parse(parameters));
        Configuration cfg = cut.getConfiguration();
        String prjName = cfg.getString(Configuration.PROJECT_FILE_CFG);
        assertEquals("important", prjName);
        assertTrue(cfg.getBool(Configuration.ZIP_OUTPUT_TO_STDOUT));
        assertEquals("lib/",cfg.getString(Configuration.LIB_PATH_CFG));

    }
}
