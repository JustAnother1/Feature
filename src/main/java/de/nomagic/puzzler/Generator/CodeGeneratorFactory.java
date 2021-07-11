package de.nomagic.puzzler.Generator;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.solution.Solution;

public class CodeGeneratorFactory
{
    private static final Logger log = LoggerFactory.getLogger("CodeGeneratorFactory");


    private CodeGeneratorFactory()
    {
        // not used
    }

    public static Generator[] getGeneratorFor(Context ctx)
    {
        if(null == ctx)
        {
        	log.error("No context!");
            return new Generator[0];
        }    	
        
        Solution s = ctx.getSolution();
        if(null == s)
        {
        	log.error("No solution!");
            return new Generator[0];
        }  
        
        ArrayList<Generator> resVec = new ArrayList<Generator>();
        
        
        // check for C Code
        if(false == s.treeContainsElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME))
        {
            log.trace("Is not a valid C-Code tree");
        }
        else
        {
            log.trace("tree has C-Code");
            resVec.add(new C_CodeGenerator(ctx));
        }

        // check for C++ Code
        if(false == s.treeContainsElement(Cpp_CodeGenerator.ALGORITHM_CODE_CHILD_NAME))
        {
            log.trace("Is not a valid C++-Code tree");
        }
        else
        {
            log.trace("tree has C++-Code");
            resVec.add(new Cpp_CodeGenerator(ctx));
        }

        // check for Verilog code
        if(false == s.treeContainsElement(Verilog_CodeGenerator.ALGORITHM_CODE_CHILD_NAME))
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
