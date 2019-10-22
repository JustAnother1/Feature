package de.nomagic.puzzler.Generator;

import java.util.ArrayList;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.solution.AlgorithmInstanceInterface;

public class CodeGeneratorFactory
{

    private static final Logger log = LoggerFactory.getLogger("CodeGeneratorFactory");


    private CodeGeneratorFactory()
    {
        // not used
    }


    public static Generator[] getGeneratorFor(AlgorithmInstanceInterface algoTree,
            Context ctx)
    {
        if(null == algoTree)
        {
            ctx.addError("CodeGeneratorFactory", "algorithm tree is null !");
            return null;
        }

        ArrayList<Generator> resVec = new ArrayList<Generator>();

        // check for C Code
        if(false == algoTree.hasApi(CCodeGenerator.REQUIRED_ROOT_API))
        {
            log.trace("Is not a valid C-Code tree");
        }
        else
        {
            log.trace("tree has C-Code");
            resVec.add(new CCodeGenerator(ctx));
        }

        // check for Verilog code
        Element res = algoTree.getAlgorithmElement(VerilogCodeGenerator.ALGORITHM_VERILOG_CODE_CHILD_NAME);
        if(null != res)
        {
            log.trace("tree has Verilog-Code");
            resVec.add(new VerilogCodeGenerator(ctx));
        }
        else
        {
            log.trace("Is not a valid Verilog-Code tree");
        }

        return resVec.toArray(new Generator[0]);
    }

}
