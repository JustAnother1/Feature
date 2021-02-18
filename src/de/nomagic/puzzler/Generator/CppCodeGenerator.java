package de.nomagic.puzzler.Generator;

import java.util.List;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Tool;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.CFile;
import de.nomagic.puzzler.FileGroup.CppFile;
import de.nomagic.puzzler.FileGroup.FileFactory;
import de.nomagic.puzzler.FileGroup.SourceFile;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.AlgorithmInstanceInterface;
import de.nomagic.puzzler.solution.Function;

public class CppCodeGenerator extends Generator
{

    public static final String ALGORITHM_CPP_CODE_CHILD_NAME = "cpp_code";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public CppCodeGenerator(Context ctx)
    {
        super(ctx);
    }

    @Override
    public String getLanguageName()
    {
        return "C++";
    }

    @Override
    protected void addAllAdditionals(AlgorithmInstanceInterface logic)
    {
        Element cCode = logic.getAlgorithmElement(ALGORITHM_CPP_CODE_CHILD_NAME);
        if(null == cCode)
        {
            ctx.addError(this,
                "Could not read implementation from " + logic.toString());
            return;
        }
        Element additional = cCode.getChild(Generator.ALGORITHM_ADDITIONAL_CHILD_NAME);
        if(null == additional)
        {
            log.trace("no addionals for algorithm {}", logic);
            return;
        }
        List<Element> addlist = additional.getChildren();
        if(null == addlist)
        {
            log.trace("empty addionals tag for algorithm {}", logic);
            return;
        }
        for(int i = 0; i < addlist.size(); i++)
        {
            Element curElement = addlist.get(i);
            String type = curElement.getName();
            switch(type)
            {
            case ALGORITHM_ADDITIONAL_INCLUDE_CHILD_NAME:
                String include = curElement.getText();
                log.trace("adding include {}", include);
                sourceFile.addLine(CFile.C_FILE_INCLUDE_SECTION_NAME, include);
                break;

            case ALGORITHM_FUNCTION_CHILD_NAME:
                Function func = new Function(curElement);

                CFunctionCall fc = new CFunctionCall(func.getName());
                String implementation = logic.getImplementationOf(fc);
                if(null == implementation)
                {
                    String error = "Could not get an Implementation for " + func.getName();
                    log.error(error);
                    ctx.addError(this, error);
                    return;
                }
                else
                {
                    func.setImplementation(implementation);
                }

                sourceFile.addFunction(func);
                break;

            case ALGORITHM_ADDITIONAL_FILE_CHILD_NAME:
                AbstractFile aFile = FileFactory.getFileFromXml(curElement);
                codeGroup.add(aFile);
                break;

            case ALGORITHM_ADDITIONAL_VARIABLE_CHILD_NAME:
                sourceFile.addLine(CFile.C_FILE_GLOBAL_VAR_SECTION_NAME, curElement.getText());
                break;

            default: // ignore
                log.warn("invalid type '{}' for algorithm '{}' !", type, logic);
                break;
            }
        }
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
