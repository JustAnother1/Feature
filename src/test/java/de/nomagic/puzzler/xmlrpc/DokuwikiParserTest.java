package de.nomagic.puzzler.xmlrpc;

import static org.junit.Assert.*;

import org.jdom2.Document;
import org.jdom2.Element;
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
        String input = "====== gpio algorithm ======";
        Document res = dut.convertToXml(input);
        assertNotNull(res);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<algorithm name=\"gpio\" />\r\n", getXml(res));
    }

    @Test
    public void testConvertToXml_specialCharacters()
    {
        String input = "====== vendor/gpio-usb.h algorithm ======";
        Document res = dut.convertToXml(input);
        assertNotNull(res);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<algorithm name=\"vendor/gpio-usb.h\" />\r\n", getXml(res));
    }

    @Test
    public void testConvertToXml_attributeWithManyValues()
    {
        String input = "====== gpio algorithm ======\r\n"
                + "**api**s implemented by this algorithm:\r\n"
                + "  * //bitOut// to set this pin to high or low\r\n"
                + "  * //bitIn// to read the level of the pin\r\n"
                + "  * //initialize// to configure the pin\r\n";
        Document res = dut.convertToXml(input);
        assertNotNull(res);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<algorithm name=\"gpio\" api=\"bitOut, bitIn, initialize\" />\r\n", getXml(res));
    }

    @Test
    public void testConvertToXml_attributeWithDot()
    {
        String input = "====== SOME_VARIABLE ======\r\n"
                + "This variable shall be inserted into the **file** //stm32.ld//.\r\n";
        Document res = dut.convertToXml(input);
        assertNotNull(res);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<SOME_VARIABLE file=\"stm32.ld\" />\r\n", getXml(res));
    }

    @Test
    public void testConvertToXml_twoattributesinOneLine()
    {
        String input = "====== SOME_VARIABLE ======\r\n"
                + "This variable shall be inserted into the **file** named //stm32.ld//."
                + " It therefore must be of **type** //pattern//.\r\n";
        Document res = dut.convertToXml(input);
        assertNotNull(res);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<SOME_VARIABLE file=\"stm32.ld\" type=\"pattern\" />\r\n", getXml(res));
    }

    @Test
    public void testConvertToXml_childAttribute()
    {
        String input = "====== gpio algorithm ======\r\n"
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
        String input = "====== gpio algorithm ======\r\n"
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
        String input = "====== gpio algorithm ======\r\n"
                     + "===== include =====\r\n"
                     + "This include <code c>avr/io.h</code> is needed to make this work.\r\n";
        Document res = dut.convertToXml(input);
        assertNotNull(res);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<algorithm name=\"gpio\">\r\n"
                + "  <include><![CDATA[avr/io.h]]></include>\r\n"
                + "</algorithm>\r\n", getXml(res));
    }

    @Test
    public void testConvertToXml_childWithMultiLineContent()
    {
        String input = "====== gpio algorithm ======\r\n"
                     + "===== include =====\r\n"
                     + "This include <code c>avr/io.h\r\n"
                     + "util/delay.h</code> is needed to make this work.\r\n";
        Document res = dut.convertToXml(input);
        assertNotNull(res);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<algorithm name=\"gpio\">\r\n"
                + "  <include><![CDATA[avr/io.h\r\nutil/delay.h]]></include>\r\n"
                + "</algorithm>\r\n", getXml(res));
    }

    @Test
    public void testConvert_multipleChilds()
    {
        String input = "====== environment ======\r\n"
                + "===== ARM_Cortex-M4F/STM32F407 tool =====\r\n"
                + "===== program_entry_point root_api =====\r\n"
                + "===== resources =====\r\n"
                + "==== user_led ====\r\n"
                + "the **algorithm** used is //gpio//.\r\n"
                + "The led is connected to **port** //D// on **pin** //12//.\r\n";
        Document res = dut.convertToXml(input);
        assertNotNull(res);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<environment>\r\n"
                + "  <tool name=\"ARM_Cortex-M4F/STM32F407\" />\r\n"
                + "  <root_api name=\"program_entry_point\" />\r\n"
                + "  <resources>\r\n"
                + "    <user_led algorithm=\"gpio\" port=\"D\" pin=\"12\" />\r\n"
                + "  </resources>\r\n"
                + "</environment>\r\n", getXml(res));
    }

    @Test
    public void test_convertXmlToWiki_null()
    {
        String res = dut.convertXmlToWiki(null);
        assertEquals("No Document!", res);
    }

    @Test
    public void test_convertXmlToWiki_no_rootl()
    {
        Document doc = new Document();
        String res = dut.convertXmlToWiki(doc);
        assertEquals("No root element!", res);
    }

    @Test
    public void test_convertXmlToWikil()
    {
        Document doc = new Document();
        Element root = new Element("environment");
        doc.setRootElement(root);
        String res = dut.convertXmlToWiki(doc);
        assertEquals("====== environment ======" +  System.getProperty("line.separator"), res);
    }

}
