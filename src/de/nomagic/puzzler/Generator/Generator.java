
package de.nomagic.puzzler.Generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.FileGroup.SourceFile;
import de.nomagic.puzzler.solution.AlgorithmInstanceInterface;
import de.nomagic.puzzler.solution.Api;
import de.nomagic.puzzler.solution.Function;

public abstract class Generator extends Base
{
    public static final String ALGORITHM_FUNCTION_CHILD_NAME = "function";
    public static final String ALGORITHM_ADDITIONAL_CHILD_NAME = "additional";
    public static final String ALGORITHM_ADDITIONAL_INCLUDE_CHILD_NAME = "include";
    public static final String ALGORITHM_ADDITIONAL_FILE_CHILD_NAME = "file";
    public static final String ALGORITHM_ADDITIONAL_VARIABLE_CHILD_NAME = "variable";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    protected String ROOT_FILE_NAME = "not_set.txt";
    protected SourceFile sourceFile;

    // generated files are collected in this
    protected FileGroup codeGroup;

    protected Generator(Context ctx)
    {
        super(ctx);
    }

    public abstract String getLanguageName();
    protected abstract void addAllAdditionals(AlgorithmInstanceInterface algo);
    protected abstract SourceFile createFile(String fileName);

    public FileGroup generateFor(AlgorithmInstanceInterface logic)
    {
        if(null == logic)
        {
            ctx.addError(this, "root algorithm instance is null! -> Failed to build the algorithm tree !");
            return null;
        }

        codeGroup = new FileGroup();
        Environment e = ctx.getEnvironment();
        if(null == e)
        {
            ctx.addError(this, "environment is null! -> Failed to build the algorithm tree !");
            return null;
        }
        String rootApi = e.getRootApi();

        Api api = Api.getFromFile(rootApi, ctx);
        if(null == api)
        {
            ctx.addError(this, "" + logic + " : Failed to load the api " + rootApi + " !");
            return null;
        }

        if(false == logic.hasApi(rootApi))
        {
            log.trace("root: {}", logic);
            ctx.addError(this, "" + logic + " : Root element of the solution is not an " + rootApi + " !");
            return null;
        }

        log.trace("starting to generate the C implementation for {}", logic);

        sourceFile = createFile(ROOT_FILE_NAME);

        // ... now we can add the code to sourceFile

        log.trace("getting implementation of the {} from {}", api, logic);
        Function[] funcs = api.getRequiredFunctions();
        for(int i = 0; i < funcs.length; i++)
        {
            CFunctionCall fc = new CFunctionCall(funcs[i].getName());
            fc.setApi(api.toString());
            String implementation = logic.getImplementationOf(fc);
            if(null == implementation)
            {
                String error = "Could not get an Implementation for " + funcs[i].getName();
                log.error(error);
                ctx.addError(this, error);
                return null;
            }
            else
            {
                funcs[i].setImplementation(implementation);
            }

            sourceFile.addFunction(funcs[i]);
        }

        addAllAdditionals(logic);

        codeGroup.add(sourceFile);

        if(false == ctx.wasSucessful())
        {
            return null;
        }

        return codeGroup;
    }

}
