
package de.nomagic.puzzler.Generator;


public class CInitCodeBlock
{
    private final String identifier;
    private final String implementation;

    public CInitCodeBlock(String id, String implementation)
    {
        this.identifier = id;
        this.implementation = implementation;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getImplemenation()
    {
        return implementation;
    }

}
