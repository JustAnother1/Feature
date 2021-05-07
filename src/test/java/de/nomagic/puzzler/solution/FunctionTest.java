package de.nomagic.puzzler.solution;

import static org.junit.Assert.*;

import org.jdom2.Element;
import org.junit.Test;

import de.nomagic.puzzler.FileGroup.FunctionHandler;

public class FunctionTest
{

    @Test
    public void testSameAsNull()
    {
        Function dut = new Function(null);
        assertFalse(dut.sameAs(null));
    }

    @Test
    public void testSameAsName()
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
    public void testSameAsResult1()
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
    public void testSameAsResult2()
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
    public void testSameAsImplementation1()
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
    public void testSameAsImplementation2()
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
    public void testSameAsParameter1()
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
    public void testSameAsParameter2()
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
    public void testSameAsRequired1()
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
    public void testSameAsRequired2()
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
    public void testSameAsMatch()
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
    public void testIsRequiredNull()
    {
        Function dut = new Function(null);
        assertFalse("", dut.isRequired());
    }

    @Test
    public void testGetNameNull()
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
    public void testGetCodeNull()
    {
        Function dut = new Function(null);
        assertNull(dut.getCode(FunctionHandler.TYPE_DECLARATION, "bla"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION, "bla"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION+1, "bla"));
    }

    @Test
    public void testGetCodeEmptyTag()
    {
        Element tag = new Element("a");
        Function dut = new Function(tag);
        assertEquals("void null(void);", dut.getCode(FunctionHandler.TYPE_DECLARATION, "bla"));
        assertEquals("void null(void)blabla", dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION, "bla"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION+1, "bla"));
    }

    @Test
    public void testGetCodeName()
    {
        Element tag = new Element("a");
        tag.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        Function dut = new Function(tag);
        assertEquals("void Bob(void);", dut.getCode(FunctionHandler.TYPE_DECLARATION, "bla"));
        assertEquals("void Bob(void)blabla", dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION, "bla"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION+1, "bla"));
    }

    @Test
    public void testGetCodeComment()
    {
        Element tag = new Element("a");
        tag.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        Function dut = new Function(tag);
        dut.addComment("a comment");
        assertEquals("void Bob(void); // from a comment\n", dut.getCode(FunctionHandler.TYPE_DECLARATION, "\n"));
        assertEquals("void Bob(void) // from a comment\n\n", dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION, "\n"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION+1, "bla"));
    }

    @Test
    public void testGetCodeImplementation()
    {
        Element tag = new Element("a");
        tag.setAttribute(Function.FUNCTION_NAME_ATTRIBUTE_NAME, "Bob");
        Function dut = new Function(tag);
        dut.addComment("a comment");
        dut.setImplementation("{\n\ti++\n}");
        assertEquals("void Bob(void); // from a comment\n", dut.getCode(FunctionHandler.TYPE_DECLARATION, "\n"));
        assertEquals("void Bob(void) // from a comment\n{\n{\n\ti++\n}\n}\n", dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION, "\n"));
        assertNull(dut.getCode(FunctionHandler.TYPE_IMPLEMENTATION+1, "bla"));
    }
}
