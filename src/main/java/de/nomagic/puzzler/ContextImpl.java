/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>
 *
 */
package de.nomagic.puzzler;

import org.jdom2.Element;

import org.jdom2.Attribute;
import org.jdom2.Document;

import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.configuration.Configuration;
import de.nomagic.puzzler.progress.ProgressReport;
import de.nomagic.puzzler.progress.ProgressReportFactory;
import de.nomagic.puzzler.solution.Solution;

public class ContextImpl implements Context
{
    public static final String EXTERNAL_REFFERENCE_ATTRIBUTE_NAME = "ref";

    private Configuration cfg;
    private final ProgressReport report;
    private Environment e;
    private Solution s;
    private FileGetter fg;

    public ContextImpl(Configuration cfg)
    {
        this.cfg = cfg;
        report = ProgressReportFactory.getReportFor(cfg);
    }

    public boolean wasSucessful()
    {
        return report.wasSucessful();
    }

    public String getErrors()
    {
        if(true == wasSucessful())
        {
            return "Context has no Errors!";
        }
        else
        {
            return "Context saw theses errors:\n" + report.getAllReports();
        }
    }

    public void close()
    {
        report.close();
    }

    public void addError(Object ref, String msg)
    {
        report.addError(ref, msg);
    }

    public Configuration cfg()
    {
        if(null == cfg)
        {
            cfg = new Configuration();
        }
        return cfg;
    }

    public Environment getEnvironment()
    {
        return e;
    }

    public void addEnvironment(Environment e)
    {
        this.e = e;
    }

    public void addSolution(Solution s)
    {
        this.s = s;
    }

    public Solution getSolution()
    {
        return s;
    }

    public Element getElementfrom(String in, String elementName)
    {
        if(null == elementName)
        {
            addError(this, "Must provide a stream!");
            return null;
        }

        Document doc;
        try
        {
            doc = fg.getXmlFromString(in);
        }
        catch(NullPointerException e)
        {
            return null;
        }
        if(null == doc)
        {
            addError(this, "Could not read Project File ");
            return null;
        }
        Element root  = doc.getRootElement();
        if(null == root)
        {
            addError(this, "Could not read root element from the stream");
            return null;
        }
        if(false == elementName.equals(root.getName()))
        {
            addError(this, "Invalid root tag (expected: " + elementName + ", found:  " + root.getName() + ") !");
            return null;
        }
        return loadElementFrom(root, "./", elementName);
    }

    public Element getElementfrom(String fileName, String path, String elementName)
    {
        if(null == elementName)
        {
            addError(this, "Invalid request! tag name missing!");
            return null;
        }
        Document doc;
        try
        {
            doc = fg.getXmlFile(path, fileName);
        }
        catch(NullPointerException e)
        {
            return null;
        }
        if(null == doc)
        {
            addError(this, "Could not read xml file " + path +  fileName);
            return null;
        }
        Element root  = doc.getRootElement();
        if(null == root)
        {
            addError(this, "Could not read root element from " + fileName);
            return null;
        }
        if(false == elementName.equals(root.getName()))
        {
            addError(this, "File " + fileName + " has an invalid root tag (expected: " + elementName + ", found:  " + root.getName() + ") !");
            return null;
        }
        return loadElementFrom(root, path, elementName);
    }

    private Element resolveExternalReference(String path, String externalReferenceFileName, String elementName)
    {
        // read external Reference
        Document externalReferenceDocument = fg.getXmlFile(
                path,
                externalReferenceFileName);
        if(null == externalReferenceDocument)
        {
            addError(this, "Could not read referenced File " + externalReferenceFileName);
            return null;
        }

        Element extRefEleemnt  = externalReferenceDocument.getRootElement();
        if(null == extRefEleemnt)
        {
            addError(this, "Could not read Root Element(" + elementName + ") from " + externalReferenceFileName);
            return null;
        }

        if(false == elementName.equals(extRefEleemnt.getName()))
        {
            addError(this, "Environment File " + externalReferenceFileName
                    + " has an invalid root tag (expected : " + elementName + ", found : " + extRefEleemnt.getName() + ") !");
            return null;
        }
        return extRefEleemnt;
    }

    public Element loadElementFrom(Element uncheckedElement, String path, String elementName)
    {
        if(true == uncheckedElement.hasAttributes())
        {
            Attribute attr = uncheckedElement.getAttribute(EXTERNAL_REFFERENCE_ATTRIBUTE_NAME);
            if(null != attr)
            {
                String externalReferenceFileName = attr.getValue();
                if(null == externalReferenceFileName)
                {
                    addError(this, "Invalid external reference !");
                    return null;
                }

                return resolveExternalReference(path, externalReferenceFileName, elementName);
            }
            else
            {
                // no external Reference - all data in this node
             // nothing to do
            }
        }
        else
        {
            // nothing to do
        }
        return uncheckedElement;
    }

    @Override
    public void addFileGetter(FileGetter fg)
    {
        this.fg = fg;
    }

    @Override
    public FileGetter getFileGetter()
    {
        return fg;
    }

}
