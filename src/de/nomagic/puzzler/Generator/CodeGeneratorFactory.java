package de.nomagic.puzzler.Generator;

import java.util.Vector;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.solution.AlgorithmInstanceInterface;

public class CodeGeneratorFactory
{

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());


    public CodeGeneratorFactory()
    {

    }


    public Generator[] getGeneratorFor(AlgorithmInstanceInterface algoTree,
            Context ctx)
    {
        if(null == algoTree)
        {
            ctx.addError(this, "algorithm tree is null !");
            return null;
        }

        Vector<Generator> resVec = new Vector<Generator>();

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
        // TODO: search whole three for Verilog code elements in the algorithms
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
