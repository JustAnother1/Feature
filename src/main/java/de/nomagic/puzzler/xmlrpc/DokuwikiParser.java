package de.nomagic.puzzler.xmlrpc;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Document;
import org.jdom2.Element;

public class DokuwikiParser
{
    // documenatation about docuwiki API:
    // https://www.dokuwiki.org/devel:xmlrpc
    public static final String GET_INFO = "wiki.getPage";
    private Element curElement = null;
    private String curAttribute = null;
    private int curLevel = 0;
    private Element[] levelElements = {null, null, null, null, null};

    // ={3}\s*(\w|-|\/|\.)+\s*(\w)*\s*={3}
    private Pattern[] levelPatterns = {
            Pattern.compile("={6}\\s*(\\w|-|\\/|\\.)+\\s*(\\w)*\\s*={6}"),
            Pattern.compile("={5}\\s*(\\w|-|\\/|\\.)+\\s*(\\w)*\\s*={5}"),
            Pattern.compile("={4}\\s*(\\w|-|\\/|\\.)+\\s*(\\w)*\\s*={4}"),
            Pattern.compile("={3}\\s*(\\w|-|\\/|\\.)+\\s*(\\w)*\\s*={3}"),
            Pattern.compile("={2}\\s*(\\w|-|\\/|\\.)+\\s*(\\w)*\\s*={2}") };

    private static final int MAX_LEVEL = 4;

    // to test regex : https://regexr.com/

    // (\*\*\w+\*\*)|(\/\/(\w+|\s|\(|\)|\.)+\/\/)
    private String AttributeOrValueExpr = "(\\*\\*\\w+\\*\\*)|(\\/\\/(\\w+|\\s|\\(|\\)|\\.)+\\/\\/)";
    private Pattern AtOrValPattern = Pattern.compile(AttributeOrValueExpr);

    // <\s*code\s*((\s)|(\w))*>
    private String CodeContentStartExpr = "<\\s*code\\s*((\\s)|(\\w))*>";
    private Pattern CodeContentStartPattern = Pattern.compile(CodeContentStartExpr);
    // <\s*\/\s*code(\s)*>
    private String CodeContentEndExpr = "<\\s*\\/\\s*code(\\s)*>";
    private Pattern CodeContentEndPattern = Pattern.compile(CodeContentEndExpr);

    // ''end''
    private String EndOfElementExpr = "''end''";
    private Pattern EndOfElementPattern = Pattern.compile(EndOfElementExpr);


    public DokuwikiParser()
    {

    }

    public String convertXmlToWiki(Document xml)
    {
        if(null == xml)
        {
            return "No Document!";
        }
        if(false == xml.hasRootElement())
        {
            return "No root element!";
        }
        Element e = xml.getRootElement();
        return getWikiSyntaxFor(e, 0);
    }

    public Document convertToXml(String wikiPage)
    {
        if(null == wikiPage)
        {
            return null;
        }
        // log.trace("read from XML-RPC({}): {}",result.length(), result);
        if(0 == wikiPage.length())
        {
            return null;
        }
        String[] lines = wikiPage.split("\\R");
        boolean foundSomething = false;
        curLevel = 0;
        Document doc = null;
        for(int i = 0; i < lines.length; i++)
        {
            // log.trace("Line {}. is : {}", i, lines[i]);
            // at first we need a H1 Headline (====== \w+ \w+ ======)
            Matcher m = levelPatterns[0].matcher(lines[i]);
            if(m.matches())
            {
                if(false == foundSomething)
                {
                    // found start
                    foundSomething = true;
                    Element root = getElementFromHeading(lines[i]);
                    doc = new Document(root);
                    curElement = root;
                    levelElements[curLevel] = root;
                }
                else
                {
                    // found end
                    break;
                }
            }
            else if(true == foundSomething)
            {
                // TODO if one check found something then I probably do not have to do the other ones, right?
                checkForAttributes(lines[i]);
                checkForChildElement(lines[i]);
                checkForExplicitEndOfElement(lines[i]);
                // check for Code sections:
                Matcher codeStart = CodeContentStartPattern.matcher(lines[i]);
                if(codeStart.find())
                {
                    int startIdx = codeStart.end();
                    Matcher codeEnd = CodeContentEndPattern.matcher(lines[i]);
                    if(codeEnd.find())
                    {
                        // start and end in the same line
                        int EndIdx = codeEnd.start();
                        String code = lines[i].substring(startIdx, EndIdx);
                        CDATA codeElement = new CDATA(code);
                        curElement.addContent(codeElement);
                    }
                    else
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append(lines[i].substring(startIdx));
                        sb.append("\r\n");
                        i++;
                        while(i < lines.length)
                        {
                            codeEnd = CodeContentEndPattern.matcher(lines[i]);
                            if(codeEnd.find())
                            {
                                // found end
                                int EndIdx = codeEnd.start();
                                sb.append(lines[i].substring(0, EndIdx));
                                break;
                            }
                            else
                            {
                                sb.append(lines[i]);
                                sb.append("\r\n");
                                i++;
                            }
                        }
                        CDATA codeElement = new CDATA(sb.toString());
                        curElement.addContent(codeElement);
                    }
                }
                // else no code in this line
            }
            // else line before start -> ignore
        }
        return doc;
    }

    // Recursion!
    private String getWikiSyntaxFor(Element e, int level)
    {
        boolean lastPartWasChild = false;
        StringBuilder sb = new StringBuilder();
        // Headline with Element name
        String name =e.getAttributeValue("name");
        if(null == name)
        {
            // element does not have an attribute name
            sb.append(getHeaderFor(e.getName(), level));
        }
        else
        {
            sb.append(getHeaderFor(name + " " + e.getName(), level));
        }
        // all the other attributes
        List<Attribute> l = e.getAttributes();
        for(int i = 0; i < l.size(); i++)
        {
            Attribute a = l.get(i);
            if(true == "name".equals(a.getName()))
            {
                // we already handled the name Attribute
            }
            else
            {
                // The **api** is //delay//.
                sb.append("The **" + a.getName() + "** is //" + a.getValue() + "//." + System.getProperty("line.separator"));
            }
        }
        List<Content> parts = e.getContent();

        for(int i = 0; i < parts.size(); i++)
        {
            Content p = parts.get(i);
            CType t = p.getCType();
            switch(t)
            {
            case CDATA:
                // code segments
                if(true == lastPartWasChild)
                {
                    sb.append("''end''" + System.getProperty("line.separator"));
                }
                sb.append("<code>" + ((CDATA)p).getText() + "</code>" + System.getProperty("line.separator"));
                lastPartWasChild = false;
                break;

            case Comment:
                if(true == lastPartWasChild)
                {
                    sb.append("''end''" + System.getProperty("line.separator"));
                }
                sb.append( ((Comment)p).getText() + System.getProperty("line.separator"));
                lastPartWasChild = false;
                break;

            case Element:
                // children
                // Recursion!
                sb.append(getWikiSyntaxFor((Element)p, level + 1));
                lastPartWasChild = true;
                break;

             default:
                 // not needed to be parsed.
                 break;
            }
        }
        return sb.toString();
    }

    private Object getHeaderFor(String name, int level)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 6 - level; i++)
        {
            sb.append("=");
        }
        String header = sb.toString();
        return header + " " + name + " " + header + System.getProperty("line.separator");
    }

    private void checkForAttributes(String line)
    {
        Matcher ma = AtOrValPattern.matcher(line);
        while (ma.find())
        {
            String match = ma.group();
            if(match.startsWith("**"))
            {
                // next Attribute
                match = match.substring(2, match.length()-2);
                curAttribute = match;
            }
            else
            {
                // starts with //
                // next value
                match = match.substring(2, match.length()-2);
                String oldValue = curElement.getAttributeValue(curAttribute);
                if(null == oldValue)
                {
                    oldValue = "";
                }
                else if(1 < oldValue.length())
                {
                    oldValue = oldValue + ", ";
                }
                oldValue = oldValue + match;
                curElement.setAttribute(curAttribute, oldValue);
            }
        }
    }

    private Element getElementFromHeading(String line)
    {
        String[] parts = line.split("\\s");
        if(4 == parts.length)
        {
            Element res = new Element(parts[2]);
            res.setAttribute("name", parts[1]);
            return res;
        }
        else if(3 == parts.length)
        {
            Element res = new Element(parts[1]);
            return res;
        }
        return null;
    }

    private void checkForChildElement(String line)
    {
        if(false == line.contains("=="))
        {
            // nothing in this line
            return;
        }
        // else something in this line -> lets see

        Matcher m = null;

        // might be a child
        if(MAX_LEVEL > curLevel)
        {
            m = levelPatterns[curLevel + 1].matcher(line);
            if(m.matches())
            {
                // found child element
                Element child = getElementFromHeading(line);
                curElement.addContent(child);
                curLevel = curLevel + 1;
                levelElements[curLevel] = child;
                curElement = child;
                curAttribute = "";
                return;
            }
            // else no child in this line
        }
        // else the max level can not have children.

        // a sibling
        m = levelPatterns[curLevel].matcher(line);
        if(m.matches())
        {
            // found sibling element
            Element sibling = getElementFromHeading(line);
            levelElements[curLevel -1 ].addContent(sibling);
            levelElements[curLevel] = sibling;
            curElement = sibling;
            curAttribute = "";
            return;
        }

        // or a parent, grand parent,...
        for(int i = curLevel -1; i > 0; i--)
        {
            m = levelPatterns[i].matcher(line);
            if(m.matches())
            {
                // found parent element
                Element parent = getElementFromHeading(line);
                levelElements[i -1 ].addContent(parent);
                levelElements[i] = parent;
                curElement = parent;
                curAttribute = "";
                curLevel = i;
                return;
            }
        }
    }

    private void checkForExplicitEndOfElement(String line)
    {
        Matcher m = EndOfElementPattern.matcher(line);
        if(m.matches())
        {
            // end this element and go back to its parent.
            curElement = levelElements[curLevel - 1];
            curAttribute = "";
            curLevel = curLevel -1;
        }
    }

}
