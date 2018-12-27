package de.nomagic.puzzler.Generator;

import java.util.Vector;

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
            log.trace("Is not a C-Code Tree");
        }
        else
        {
            resVec.add(new CCodeGenerator(ctx));
        }

        // TODO check used programming languages in Tree

        return resVec.toArray(new Generator[0]);
    }

}
