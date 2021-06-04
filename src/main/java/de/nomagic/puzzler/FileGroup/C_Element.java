package de.nomagic.puzzler.FileGroup;

public abstract class C_Element implements  Comparable<C_Element>
{
    public abstract String getName();

    @Override
    public int compareTo(C_Element n)
    {
        return this.getName().compareTo(n.getName());
    }

    public abstract String getCode(int type, String lineSperator);
}
