package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.jdom2.Element;
import org.junit.Test;

import de.nomagic.puzzler.FileGroup.FunctionHandler;

public class FunctionTest
{

    @Test
    public void testSameAs_null()
    {
        Function dut = new Function(null);
        assertFalse(dut.sameAs(null));
    }

    @Test
    public void testSameAs_name()
    {
        Element e1 = new Element("bla");
        Element e2 = new Element("bla");
        e1.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e2.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bill");
        Function dut = new Function(e1);
        Function other = new Function(e2);
        assertFalse(dut.sameAs(other));
    }

    @Test
    public void testSameAs_result_1()
    {
        Element e1 = new Element("bla");
        Element e2 = new Element("bla");
        e1.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e2.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e1.setAttribute(Function.FUNCTION_RESULT_ATTRIBUTE_NAME, "int");
        Function dut = new Function(e1);
        Function other = new Function(e2);
        assertFalse(dut.sameAs(other));
    }

    @Test
    public void testSameAs_result_2()
    {
        Element e1 = new Element("bla");
        Element e2 = new Element("bla");
        e1.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e2.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e2.setAttribute(Function.FUNCTION_RESULT_ATTRIBUTE_NAME, "int");
        Function dut = new Function(e1);
        Function other = new Function(e2);
        assertFalse(dut.sameAs(other));
    }

    @Test
    public void testSameAs_implementation_1()
    {
        Element e1 = new Element("bla");
        Element e2 = new Element("bla");
        e1.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e2.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        Function dut = new Function(e1);
        Function other = new Function(e2);
        dut.setImplementation("i++");
        assertFalse(dut.sameAs(other));
    }

    @Test
    public void testSameAs_implementation_2()
    {
        Element e1 = new Element("bla");
        Element e2 = new Element("bla");
        e1.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e2.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        Function dut = new Function(e1);
        Function other = new Function(e2);
        other.setImplementation("i++");
        assertFalse(dut.sameAs(other));
    }

    @Test
    public void testSameAs_parameter_1()
    {
        Element e1 = new Element("bla");
        Element e2 = new Element("bla");
        e1.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e2.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e1.setAttribute(Function.FUNCTION_RESULT_ATTRIBUTE_NAME, "int");
        e2.setAttribute(Function.FUNCTION_RESULT_ATTRIBUTE_NAME, "int");
        e1.setAttribute(Function.FUNCTION_PARAMETER_ATTRIBUTE_NAME + "0_" + Function.FUNCTION_NAME_ATTRIBUTE_NAME, "num");
        e1.setAttribute(Function.FUNCTION_PARAMETER_ATTRIBUTE_NAME + "0_" + Function.FUNCTION_TYPE_ATTRIBUTE_NAME, "int");
        Function dut = new Function(e1);
        Function other = new Function(e2);
        assertFalse(dut.sameAs(other));
    }

    @Test
    public void testSameAs_parameter_2()
    {
        Element e1 = new Element("bla");
        Element e2 = new Element("bla");
        e1.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e2.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e1.setAttribute(Function.FUNCTION_RESULT_ATTRIBUTE_NAME, "int");
        e2.setAttribute(Function.FUNCTION_RESULT_ATTRIBUTE_NAME, "int");
        e2.setAttribute(Function.FUNCTION_PARAMETER_ATTRIBUTE_NAME + "0_" + Function.FUNCTION_NAME_ATTRIBUTE_NAME, "num");
        e2.setAttribute(Function.FUNCTION_PARAMETER_ATTRIBUTE_NAME + "0_" + Function.FUNCTION_TYPE_ATTRIBUTE_NAME, "int");
        Function dut = new Function(e1);
        Function other = new Function(e2);
        dut.setImplementation("i++");
        other.setImplementation("i++");
        assertFalse(dut.sameAs(other));
    }

    @Test
    public void testSameAs_required_1()
    {
        Element e1 = new Element("bla");
        Element e2 = new Element("bla");
        e1.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e2.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e1.setAttribute(Function.FUNCTION_RESULT_ATTRIBUTE_NAME, "int");
        e2.setAttribute(Function.FUNCTION_RESULT_ATTRIBUTE_NAME, "int");
        e1.setAttribute(Function.FUNCTION_PARAMETER_ATTRIBUTE_NAME + "0_" + Function.FUNCTION_NAME_ATTRIBUTE_NAME, "num");
        e1.setAttribute(Function.FUNCTION_PARAMETER_ATTRIBUTE_NAME + "0_" + Function.FUNCTION_TYPE_ATTRIBUTE_NAME, "int");
        e2.setAttribute(Function.FUNCTION_PARAMETER_ATTRIBUTE_NAME + "0_" + Function.FUNCTION_NAME_ATTRIBUTE_NAME, "num");
        e2.setAttribute(Function.FUNCTION_PARAMETER_ATTRIBUTE_NAME + "0_" + Function.FUNCTION_TYPE_ATTRIBUTE_NAME, "int");
        e1.setAttribute(Function.FUNCTION_TYPE_ATTRIBUTE_NAME, Function.FUNCTION_REQUIRED_TYPE);
        Function dut = new Function(e1);
        Function other = new Function(e2);
        dut.setImplementation("i++");
        other.setImplementation("i++");
        assertFalse(dut.sameAs(other));
    }

    @Test
    public void testSameAs_required_2()
    {
        Element e1 = new Element("bla");
        Element e2 = new Element("bla");
        e1.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e2.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e1.setAttribute(Function.FUNCTION_RESULT_ATTRIBUTE_NAME, "int");
        e2.setAttribute(Function.FUNCTION_RESULT_ATTRIBUTE_NAME, "int");
        e2.setAttribute(Function.FUNCTION_TYPE_ATTRIBUTE_NAME, Function.FUNCTION_REQUIRED_TYPE);
        Function dut = new Function(e1);
        Function other = new Function(e2);
        dut.setImplementation("i++");
        other.setImplementation("i++");
        assertFalse(dut.sameAs(other));
    }

    @Test
    public void testSameAs_match()
    {
        Element e1 = new Element("bla");
        Element e2 = new Element("bla");
        e1.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e2.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        e1.setAttribute(Function.FUNCTION_RESULT_ATTRIBUTE_NAME, "int");
        e2.setAttribute(Function.FUNCTION_RESULT_ATTRIBUTE_NAME, "int");
        e1.setAttribute(Function.FUNCTION_PARAMETER_ATTRIBUTE_NAME + "0_" + Function.FUNCTION_NAME_ATTRIBUTE_NAME, "num");
        e1.setAttribute(Function.FUNCTION_PARAMETER_ATTRIBUTE_NAME + "0_" + Function.FUNCTION_TYPE_ATTRIBUTE_NAME, "int");
        e2.setAttribute(Function.FUNCTION_PARAMETER_ATTRIBUTE_NAME + "0_" + Function.FUNCTION_NAME_ATTRIBUTE_NAME, "num");
        e2.setAttribute(Function.FUNCTION_PARAMETER_ATTRIBUTE_NAME + "0_" + Function.FUNCTION_TYPE_ATTRIBUTE_NAME, "int");
        e1.setAttribute(Function.FUNCTION_TYPE_ATTRIBUTE_NAME, Function.FUNCTION_REQUIRED_TYPE);
        e2.setAttribute(Function.FUNCTION_TYPE_ATTRIBUTE_NAME, Function.FUNCTION_REQUIRED_TYPE);
        Function dut = new Function(e1);
        Function other = new Function(e2);
        dut.setImplementation("i++");
        other.setImplementation("i++");
        assertTrue(dut.sameAs(other));
    }

    @Test
    public void testSameAs()
    {
        Function dut = new Function(null);
        assertTrue(dut.sameAs(dut));
    }

    @Test
    public void testIsRequired_null()
    {
        Function dut = new Function(null);
        assertFalse("", dut.isRequired());
    }

    @Test
    public void testGetName_null()
    {
        Function dut = new Function(null);
        assertEquals("", dut.getName());
    }

    @Test
    public void testGetComment()
    {
        Function dut = new Function(null);
        assertEquals(null, dut.getComment());
        dut.addComment("foo");
        assertEquals("foo", dut.getComment());
        dut.addComment("bar");
        assertEquals("foo bar", dut.getComment());
    }

    @Test
    public void testGetCode_null()
    {
        Function dut = new Function(null);
        assertNull(dut.getCode(FunctionHandler.TYPE_DECLARATION, "bla"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION, "bla"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION+1, "bla"));
    }

    @Test
    public void testGetCode_emptyTag()
    {
        Element tag = new Element("a");
        Function dut = new Function(tag);
        assertEquals("void null(void);", dut.getCode(FunctionHandler.TYPE_DECLARATION, "bla"));
        assertEquals("void null(void)blabla", dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION, "bla"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION+1, "bla"));
    }

    @Test
    public void testGetCode_name()
    {
        Element tag = new Element("a");
        tag.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        Function dut = new Function(tag);
        assertEquals("void Bob(void);", dut.getCode(FunctionHandler.TYPE_DECLARATION, "bla"));
        assertEquals("void Bob(void)blabla", dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION, "bla"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION+1, "bla"));
    }

    @Test
    public void testGetCode_comment()
    {
        Element tag = new Element("a");
        tag.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        Function dut = new Function(tag);
        dut.addComment("a comment");
        assertEquals("void Bob(void); // from a comment", dut.getCode(FunctionHandler.TYPE_DECLARATION, "\n"));
        assertEquals("void Bob(void) // from a comment\n\n", dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION, "\n"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION+1, "bla"));
    }

    @Test
    public void testGetCode_implementation()
    {
        Element tag = new Element("a");
        tag.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        Function dut = new Function(tag);
        dut.addComment("a comment");
        dut.setImplementation("{\n\ti++\n}");
        assertEquals("void Bob(void); // from a comment", dut.getCode(FunctionHandler.TYPE_DECLARATION, "\n"));
        assertEquals("void Bob(void) // from a comment\n{\n\ti++\n}\n", dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION, "\n"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION+1, "bla"));
    }
}
