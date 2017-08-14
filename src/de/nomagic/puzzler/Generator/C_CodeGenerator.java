
package de.nomagic.puzzler.Generator;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.solution.ConfiguredAlgorithm;

public class C_CodeGenerator extends Generator
{
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
