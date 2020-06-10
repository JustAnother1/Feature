package de.nomagic.puzzler.BuildSystem;

import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.FileGroup;

public interface BuildSystemApi
{
    boolean hasTargetFor(String source);
    void addTarget(Target aTarget);
    void extendListVariable(String list, String newElement);
    void addRequiredVariable(String string);
    void addVariable(String variName, String variValue);
    void addFile(AbstractFile newFile);
    FileGroup createBuildFor(FileGroup files);
}
