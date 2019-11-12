
package de.nomagic.puzzler.Generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.solution.AlgorithmInstanceInterface;

public abstract class Generator extends Base
{
    public static final String CFG_DOC_CODE_SRC = "document_code_source";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    // if this is true then all code snippets will be wrapped into comment lines explaining where they came from.
    protected boolean documentCodeSource = false;

    public Generator(Context ctx)
    {
        super(ctx);
    }

    public void configure(Configuration cfg)
    {
        if(null == cfg)
        {
            return;
        }
        if("true".equals(cfg.getString(CFG_DOC_CODE_SRC)))
        {
            log.trace("Switching on documentation of source code");
            documentCodeSource = true;
        }
    }

    public abstract FileGroup generateFor(AlgorithmInstanceInterface logic);

    public abstract String getLanguageName();

}
