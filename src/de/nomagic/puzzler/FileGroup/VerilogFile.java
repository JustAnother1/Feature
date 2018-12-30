package de.nomagic.puzzler.FileGroup;

public class VerilogFile extends TextFile
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

}
