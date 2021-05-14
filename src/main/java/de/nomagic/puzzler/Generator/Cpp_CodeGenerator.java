package de.nomagic.puzzler.Generator;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.FileGroup.CFile;
import de.nomagic.puzzler.FileGroup.CppFile;
import de.nomagic.puzzler.FileGroup.SourceFile;
import de.nomagic.puzzler.configuration.Configuration;

public class Cpp_CodeGenerator extends C_CodeGenerator
{
    public static final String ALGORITHM_CPP_CODE_CHILD_NAME = "cpp_code";

    public Cpp_CodeGenerator(Context ctx)
    {
        super(ctx);
    }

    @Override
    public String getLanguageName()
    {
        return "C++";
    }

    @Override
    protected SourceFile createFile(String fileName)
    {
        CppFile aFile = new CppFile(fileName);

        // there should be a file comment explaining what this is
        aFile.addLines(CFile.C_FILE_FILE_COMMENT_SECTION_NAME,
                       new String[] {"// automatically created " + fileName,
                                     "// created at: " + Tool.curentDateTime(),
                                     "// created from " + ctx.cfg().getString(Configuration.SOLUTION_FILE_CFG) });
        return aFile;
    }

}
