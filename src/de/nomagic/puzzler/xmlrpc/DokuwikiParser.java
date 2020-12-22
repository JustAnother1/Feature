package de.nomagic.puzzler.xmlrpc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.CDATA;
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

    private Pattern[] levelPatterns = {
            Pattern.compile("={6}\\s*\\w+\\s*(\\w)*\\s*={6}"),
            Pattern.compile("={5}\\s*\\w+\\s*(\\w)*\\s*={5}"),
            Pattern.compile("={4}\\s*\\w+\\s*(\\w)*\\s*={4}"),
            Pattern.compile("={3}\\s*\\w+\\s*(\\w)*\\s*={3}"),
            Pattern.compile("={2}\\s*\\w+\\s*(\\w)*\\s*={2}") };

    // to test regex : https://regexr.com/

    // (\*\*\w+\*\*)|(\/\/(\w+|\s)+\/\/)
    private String AttributeOrValueExpr = "(\\*\\*\\w+\\*\\*)|(\\/\\/(\\w+|\\s)+\\/\\/)";
    private Pattern AtOrValPattern = Pattern.compile(AttributeOrValueExpr);

    // <\s*code((\s)|(\w))*>
    private String CodeContentStartExpr = "<\\s*code((\\s)|(\\w))*>";
    private Pattern CodeContentStartPattern = Pattern.compile(CodeContentStartExpr);
    // <\s*\/\s*code(\s)*>
    private String CodeContentEndExpr = "<\\s*\\/\\s*code(\\s)*>";
    private Pattern CodeContentEndPattern = Pattern.compile(CodeContentEndExpr);



    public DokuwikiParser()
    {

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
                curAttribute = match.toLowerCase();
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
            Element res = new Element(parts[2].toLowerCase());
            res.setAttribute("name", parts[1].toLowerCase());
            return res;
        }
        else if(3 == parts.length)
        {
            Element res = new Element(parts[1].toLowerCase());
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

        // might be a child
        Matcher m = levelPatterns[curLevel + 1].matcher(line);
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

    public Document convertToXml(String result)
    {
        if(null == result)
        {
            return null;
        }
        // log.trace("read from XML-RPC({}): {}",result.length(), result);
        if(0 == result.length())
        {
            return null;
        }
        String[] lines = result.split("\\R");
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
                checkForAttributes(lines[i]);
                checkForChildElement(lines[i]);
                Matcher codeStart = CodeContentStartPattern.matcher(lines[i]);
                if(codeStart.matches())
                {
                    int startIdx = codeStart.end();
                    Matcher codeEnd = CodeContentEndPattern.matcher(lines[i]);
                    if(codeEnd.matches())
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
                        i++;
                        while(i < lines.length)
                        {
                            codeEnd = CodeContentEndPattern.matcher(lines[i]);
                            if(codeEnd.matches())
                            {
                                // found end
                                int EndIdx = codeEnd.start();
                                sb.append(lines[i].substring(0, EndIdx));
                                break;
                            }
                            else
                            {
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

}
