/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>
 *
 */
package de.nomagic.puzzler.BuildSystem;

import org.jdom2.Element;

public class Target
{
    public static final String PHONY_ATTRIBUTE_NAME = "phony";
    public static final String IS_DEFAULT_ATTRIBUTE_NAME = "default";

    private String source;
    private String output;
    private String rule;
    private boolean phony = false;
    private boolean isDefault = false;

    public Target(String source)
    {
        this.source = source;
    }

    public Target(Element xml)
    {
        if(null != xml)
        {
            source = xml.getChildText("source");
            output = xml.getChildText("output");
            setRule(xml.getChildText("rule"));
            String att = xml.getAttributeValue(PHONY_ATTRIBUTE_NAME);
            phony = getBooleanFromAttribute(att);
            att = xml.getAttributeValue(IS_DEFAULT_ATTRIBUTE_NAME);
            isDefault = getBooleanFromAttribute(att);
        }
    }

    private boolean getBooleanFromAttribute(String att)
    {
        if(null == att)
        {
            return false;
        }
        else
        {
            att = att.toUpperCase();
            if(true == "TRUE".equals(att))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public void setOutput(String output)
    {
        this.output = output;
    }

    public void setRule(String rule)
    {
        if(null == rule)
        {
            this.rule = "";
        }
        else
        {
            rule = rule.trim();
            if(true == rule.contains("\n"))
            {
                String[] lines = rule.split("\n");
                StringBuilder sb = new StringBuilder();
                sb.append(lines[0].trim());
                for(int i = 1; i < lines.length; i++)
                {
                    sb.append("\n");
                    sb.append(lines[i].trim());
                }
                this.rule = sb.toString();
            }
            else
            {
                this.rule = rule;
            }
        }
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

    public void setDefault()
    {
        isDefault = true;
    }

    public boolean isDefault()
    {
        return isDefault;
    }

}
