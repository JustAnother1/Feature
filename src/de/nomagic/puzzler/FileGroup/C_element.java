package de.nomagic.puzzler.FileGroup;

public abstract class C_element implements  Comparable<C_element>
{
    public abstract String getName();

    @Override
    public int compareTo(C_element n)
    {
        return this.getName().compareTo(n.getName());
    }

    public abstract String getCode(int type, String lineSperator);
}
