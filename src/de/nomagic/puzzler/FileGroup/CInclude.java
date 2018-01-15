package de.nomagic.puzzler.FileGroup;

public class CInclude extends CElement
{
    private final String fileName;
    private String comment;

    public CInclude(String fileName, String comment)
    {
        this.fileName = fileName;
        this.comment = comment;
    }

    @Override
    public String getName()
    {
        return fileName;
    }

    public String getComment()
    {
        return comment;
    }

    public void addComment(String additionalInformation)
    {
        if(null != comment)
        {
            comment = comment + " " + additionalInformation;
        }
        else
        {
            comment = additionalInformation;
        }
    }

    @Override
    public String getCode(int type, String lineSperator)
    {
        String line = "#include <" + getName();
        String comment = getComment();
        if(null == comment)
        {
            line = line + ">";
        }
        else
        {
            line = line + "> // " + comment;
        }
        return line;
    }

}
