
package de.nomagic.puzzler.Generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.progress.ProgressReport;
import de.nomagic.puzzler.solution.ConfiguredAlgorithm;
import de.nomagic.puzzler.solution.Solution;

public class C_CodeGenerator extends Generator
{

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public C_CodeGenerator(Context ctx)
    {
        super(ctx);
    }

    public FileGroup generateFor()
    {
        FileGroup codeGroup = new FileGroup();

        // create configured Algorithm Tree
        ConfiguredAlgorithm logic = ConfiguredAlgorithm.getTreeFrom(ctx);

        if(null == logic)
        {
            ctx.addError(this, "Failed to build the algorithm tree !");
            return null;
        }

        codeGroup = logic.getCImplementationInto(codeGroup);

        return codeGroup;
    }

}
;
