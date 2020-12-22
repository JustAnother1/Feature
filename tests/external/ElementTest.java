package external;

import static org.junit.Assert.*;

import org.jdom2.CDATA;
import org.jdom2.Element;
import org.junit.Test;

public class ElementTest {

    @Test
    public void testGetText_empty()
    {
        Element dut = new Element("someTag");
        String text = dut.getText();
        assertNotNull(text);
        assertEquals("", text);
    }

    @Test
    public void testGetText_CDATA()
    {
        Element dut = new Element("someTag");
        CDATA cdat = new CDATA("some Text");
        dut.addContent(cdat);
        String text = dut.getText();
        assertNotNull(text);
        assertEquals("some Text", text);
    }

}
