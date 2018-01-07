package de.nomagic.puzzler.BuildSystem;

import org.jdom2.Element;

public class Target
{
    private String source;
    private String output;
    private String rule;

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

}
