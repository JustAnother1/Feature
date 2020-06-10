package de.nomagic.puzzler.Generator;

import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.FileGroup.FileGroup;
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
        VerilogFile sourceFile = createFile("top.v");

        Element res = logic.getAlgorithmElement(VerilogCodeGenerator.ALGORITHM_VERILOG_CODE_CHILD_NAME);
        if(null != res)
        {
            if(true == documentCodeSource)
            {
                sourceFile.addLine(VerilogFile.VERILOG_FILE_MODULE_SECTION_NAME,
                        "// start modules from " + logic);
            }
            List<Element> l = res.getChildren("module");
            Iterator<Element> it = l.iterator();
            while(it.hasNext())
            {
                Element curE = it.next();
                sourceFile.addLine(VerilogFile.VERILOG_FILE_MODULE_SECTION_NAME,
                        curE.getText());
            }
            if(true == documentCodeSource)
            {
                sourceFile.addLine(VerilogFile.VERILOG_FILE_MODULE_SECTION_NAME,
                        "// end modules from " + logic);
            }
        }

        codeGroup.add(sourceFile);
        return codeGroup;
    }

    @Override
    public void configure(Configuration cfg)
    {
        if(null == cfg)
        {
            return;
        }
        if("true".equals(cfg.getString(CFG_DOC_CODE_SRC)))
        {
            log.trace("Switching on documentation of source code");
            documentCodeSource = true;
        }
    }

    private VerilogFile createFile(String fileName)
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


}
