package de.nomagic.puzzler.xmlrpc;

import static org.junit.Assert.*;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;

public class DokuwikiParserTest {

    private DokuwikiParser dut = null;

    @Before
    public void setUp() throws Exception
    {
        dut = new DokuwikiParser();
    }

    private String getXml(Document doc)
    {
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        //xmlOutput.output(doc, System.err);
        return xmlOutput.outputString(doc);
    }

    @Test
    public void testConvertToXml_null()
    {
        Document res = dut.convertToXml(null);
        assertNull(res);
    }

    @Test
    public void testConvertToXml_emptyString()
    {
        Document res = dut.convertToXml("");
        assertNull(res);
    }

    @Test
    public void testConvertToXml_invaldData()
    {
        Document res = dut.convertToXml("bla\nbla\nblubb\n\nnothing else");
        assertNull(res);
    }

    @Test
    public void testConvertToXml_singleValidLine()
    {
        String input = "====== GPIO Algorithm ======";
        Document res = dut.convertToXml(input);
        assertNotNull(res);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<algorithm name=\"gpio\" />\r\n", getXml(res));
    }

    @Test
    public void testConvertToXml_attributeWithManyValues()
    {
        String input = "====== GPIO Algorithm ======\r\n"
                + "**API**s implemented by this algorithm:\r\n"
                + "  * //bitOut// to set this pin to high or low\r\n"
                + "  * //bitIn// to read the level of the pin\r\n"
                + "  * //initialize// to configure the pin\r\n";
        Document res = dut.convertToXml(input);
        assertNotNull(res);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<algorithm name=\"gpio\" api=\"bitOut, bitIn, initialize\" />\r\n", getXml(res));
    }

    @Test
    public void testConvertToXml_childAttribute()
    {
        String input = "====== GPIO Algorithm ======\r\n"
                + "===== port parameter =====\r\n"
                + "This parameter specifies which GPIO Port of the Chip is used.\r\n"
                + "The **type** of the parameter is //character//.\r\n";
        Document res = dut.convertToXml(input);
        assertNotNull(res);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<algorithm name=\"gpio\">\r\n"
                + "  <parameter name=\"port\" type=\"character\" />\r\n"
                + "</algorithm>\r\n", getXml(res));
    }

    @Test
    public void testConvertToXml_twoChildren()
    {
        String input = "====== GPIO Algorithm ======\r\n"
                     + "===== port parameter =====\r\n"
                     + "===== pin parameter =====\r\n";
        Document res = dut.convertToXml(input);
        assertNotNull(res);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<algorithm name=\"gpio\">\r\n"
                + "  <parameter name=\"port\" />\r\n"
                + "  <parameter name=\"pin\" />\r\n"
                + "</algorithm>\r\n", getXml(res));
    }

    @Test
    public void testConvertToXml_childWithContent()
    {
        String input = "====== GPIO Algorithm ======\r\n"
                     + "===== include =====\r\n"
                     + "This include <code c>avr/io.h</code> is needed to make this work.\r\n";
        Document res = dut.convertToXml(input);
        assertNotNull(res);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<algorithm name=\"gpio\">\r\n"
                + "  <include><![CDATA[avr/io.h]]></include>\r\n"
                + "</algorithm>\r\n", getXml(res));
    }

}
