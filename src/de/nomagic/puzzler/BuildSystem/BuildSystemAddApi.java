package de.nomagic.puzzler.BuildSystem;

import de.nomagic.puzzler.FileGroup.AbstractFile;

public interface BuildSystemAddApi
{
    boolean hasTargetFor(String source);
    void addTarget(Target aTarget);
    void extendListVariable(String list, String newElement);
    void addRequiredVariable(String string);
    void addVariable(String variName, String variValue);
    void addFile(AbstractFile newFile);
}
