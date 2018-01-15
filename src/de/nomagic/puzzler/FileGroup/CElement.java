package de.nomagic.puzzler.FileGroup;

public abstract class CElement implements  Comparable<CElement>
{
    public abstract String getName();

    @Override
    public int compareTo(CElement n)
    {
        return this.getName().compareTo(n.getName());
    }

    public abstract String getCode(int type, String lineSperator);
}
