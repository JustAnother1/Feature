package de.nomagic.puzzler.Generator;

import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.FileGroup.SourceFile;
import de.nomagic.puzzler.FileGroup.VerilogFile;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.AlgorithmInstanceInterface;

public class VerilogCodeGenerator extends Generator
{
    public final static String ALGORITHM_VERILOG_CODE_CHILD_NAME = "verilog_code";

    public final static String CFG_DOC_CODE_SRC = "document_code_source";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public VerilogCodeGenerator(Context ctx)
    {
        super(ctx);
        ROOT_FILE_NAME = "top.v";
    }

    @Override
    public FileGroup generateFor(AlgorithmInstanceInterface logic)
    {
        FileGroup codeGroup = new FileGroup();

        if(null == logic)
        {
            ctx.addError(this, "" + logic + " : Failed to build the algorithm tree !");
            return null;
        }

        log.trace("starting to generate the verilog implementation for {}", logic);
        // we will need at least one *.c file. So create that now.
        SourceFile sourceFile = createFile("top.v");

        Element res = logic.getAlgorithmElement(VerilogCodeGenerator.ALGORITHM_VERILOG_CODE_CHILD_NAME);
        if(null != res)
        {
            List<Element> l = res.getChildren("module");
            Iterator<Element> it = l.iterator();
            while(it.hasNext())
            {
                Element curE = it.next();
                sourceFile.addLine(VerilogFile.VERILOG_FILE_MODULE_SECTION_NAME,
                        curE.getText());
            }
        }

        codeGroup.add(sourceFile);
        return codeGroup;
    }

    @Override
    public String getLanguageName()
    {
        return "Verilog";
    }

    protected String fillInFunctionCall(String functionName, AlgorithmInstanceInterface logic)
    {
        // this is an error
        return null;
    }

    @Override
    protected void addAllAdditionals(AlgorithmInstanceInterface algo)
    {
        // nothing to do
    }

    @Override
    protected SourceFile createFile(String fileName)
    {
        VerilogFile aFile = new VerilogFile(fileName);

        // there should be a file comment explaining what this is
        aFile.addLines(VerilogFile.VERILOG_FILE_FILE_COMMENT_SECTION_NAME,
                       new String[] {"// automatically created " + fileName,
                                     "// created at: " + Tool.curentDateTime(),
                                     "//created from " + ctx.cfg().getString(Configuration.SOLUTION_FILE_CFG),
                                     });
        return aFile;
    }

}
