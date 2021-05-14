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

        Element res = null;
        // check for C Code
        res = algoTree.getAlgorithmElement(C_CodeGenerator.ALGORITHM_C_CODE_CHILD_NAME);
        if(null == res)
        {
            log.trace("Is not a valid C-Code tree");
        }
        else
        {
            log.trace("tree has C-Code");
            resVec.add(new C_CodeGenerator(ctx));
        }

        // check for C++ Code
        res = algoTree.getAlgorithmElement(Cpp_CodeGenerator.ALGORITHM_CPP_CODE_CHILD_NAME);
        if(null == res)
        {
            log.trace("Is not a valid C++-Code tree");
        }
        else
        {
            log.trace("tree has C++-Code");
            resVec.add(new Cpp_CodeGenerator(ctx));
        }

        // check for Verilog code
        res = algoTree.getAlgorithmElement(Verilog_CodeGenerator.ALGORITHM_VERILOG_CODE_CHILD_NAME);
        if(null == res)
        {
            log.trace("Is not a valid Verilog-Code tree");
        }
        else
        {
            log.trace("tree has Verilog-Code");
            resVec.add(new Verilog_CodeGenerator(ctx));
        }

        return resVec.toArray(new Generator[0]);
    }

}
