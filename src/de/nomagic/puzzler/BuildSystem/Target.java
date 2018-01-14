package de.nomagic.puzzler.BuildSystem;

import org.jdom2.Element;

public class Target
{
    public final static String PHONY_ATTRIBUTE_NAME = "phony";

    private String source;
    private String output;
    private String rule;
    private boolean phony = false;

    public Target(String source)
    {
        this.source = source;
    }

    public Target(Element Xml)
    {
        if(null != Xml)
        {
            source = Xml.getChildText("source");
            output = Xml.getChildText("output");
            rule = Xml.getChildText("rule");
            String att = Xml.getAttributeValue(PHONY_ATTRIBUTE_NAME);
            if(null == att)
            {
                phony = false;
            }
            else
            {
                att = att.toUpperCase();
                if(true == "TRUE".equals(att))
                {
                    phony = true;
                }
                else
                {
                    phony = false;
                }
            }
        }
    }

    public void setOutput(String output)
    {
        this.output = output;
    }

    public void setRule(String rule)
    {
        this.rule = rule;
    }

    public String getAsMakeFileTarget()
    {
        rule = rule.replace("\n", "\n\t");
        return output + ": " + source + "\n\t" + rule;
    }

    public String getSource()
    {
        return source;
    }

    public String getOutput()
    {
        return output;
    }

    public void setPhony(boolean val)
    {
        phony = val;
    }

    public boolean isPhony()
    {
        return phony;
    }

}
