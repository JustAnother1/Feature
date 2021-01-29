package de.nomagic.puzzler.FileGroup;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternFile extends AbstractFile
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private String pattern = null;
    private HashMap<String, String> variables = new HashMap<String, String>();


    public PatternFile(String filename)
    {
        super(filename);
    }

    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }

    public void addVariable(String name, String value)
    {
        variables.put(name, value);
    }

    @Override
    public void writeToStream(OutputStream out) throws IOException
    {
        if(null == pattern)
        {
            log.error("Pattern is null in {} !", fileName);
        }
        else
        {
            // replace all place holders with the variables values
            StringBuilder res = new StringBuilder();
            String[] parts = pattern.split("€");
            for(int i = 0; i < parts.length; i++)
            {
                if(0 == i%2)
                {
                    res.append(parts[i]);
                }
                else
                {
                    String variName = parts[i];
                    String value = variables.get(variName);
                    if(null == value)
                    {
                        log.error("Pattern {} refers to unknow variable {} !", fileName, variName);
                    }
                    else
                    {
                        res.append(value);
                    }
                }
            }
            out.write(res.toString().getBytes());
        }
    }

}
