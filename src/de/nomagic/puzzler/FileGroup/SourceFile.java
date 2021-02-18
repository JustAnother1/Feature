package de.nomagic.puzzler.FileGroup;

import de.nomagic.puzzler.solution.Function;

public abstract class SourceFile extends TextFile
{

    public SourceFile(String filename)
    {
        super(filename);
    }

    public abstract void addFunction(Function func);

}
