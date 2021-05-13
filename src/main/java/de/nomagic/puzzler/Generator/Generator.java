
package de.nomagic.puzzler.Generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.solution.AlgorithmInstanceInterface;
import de.nomagic.puzzler.solution.Api;

public abstract class Generator extends Base
{
    public static final String ALGORITHM_FUNCTION_CHILD_NAME = "function";
    public static final String ALGORITHM_ADDITIONAL_CHILD_NAME = "additional";
    public static final String ALGORITHM_ADDITIONAL_INCLUDE_CHILD_NAME = "include";
    public static final String ALGORITHM_ADDITIONAL_FILE_CHILD_NAME = "file";
    public static final String ALGORITHM_ADDITIONAL_VARIABLE_CHILD_NAME = "variable";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    protected String ROOT_FILE_NAME = "not_set.txt";  // should be initialized by implementing classes to something that makes sense.

    // generated files are collected in this
    protected FileGroup codeGroup;

    protected Generator(Context ctx)
    {
        super(ctx);
    }

    public abstract String getLanguageName();

    private Api checkRootApi(AlgorithmInstanceInterface logic)
    {
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
        return api;
    }

    protected abstract boolean generateSourceCodeFor(Api api, AlgorithmInstanceInterface logic);

    public FileGroup generateFor(AlgorithmInstanceInterface logic)
    {
        if(null == logic)
        {
            ctx.addError(this, "root algorithm instance is null! -> Failed to build the algorithm tree !");
            return null;
        }

        codeGroup = new FileGroup();

        Api api = checkRootApi(logic);
        if(null == api)
        {
            return null;
        }
        // else OK

        log.trace("starting to generate the {} implementation for {}", getLanguageName(), logic);

        if(false == generateSourceCodeFor(api, logic))
        {
            return null;
        }

        if(false == ctx.wasSucessful())
        {
            return null;
        }

        return codeGroup;
    }

}
