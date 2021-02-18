package de.nomagic.puzzler.FileGroup;

import de.nomagic.puzzler.solution.Function;

public class VerilogFile extends SourceFile
{
    public final static String VERILOG_FILE_FILE_COMMENT_SECTION_NAME  = "FileHeader";
    public final static String VERILOG_FILE_MODULE_SECTION_NAME = "Modules";

    public VerilogFile(String filename)
    {
        super(filename);
        createSections(new String[] {
                VERILOG_FILE_FILE_COMMENT_SECTION_NAME,
                VERILOG_FILE_MODULE_SECTION_NAME });
        separateSectionWithEmptyLine(true);
    }

    @Override
    public void addFunction(Function func)
    {
        // ignore
    }

}
