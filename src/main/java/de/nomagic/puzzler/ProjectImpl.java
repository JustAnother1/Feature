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

public class ProjectImpl extends Base implements Project
{
    private Element projectRoot = null;

    public ProjectImpl(Context ctx)
    {
        super(ctx);
    }

    public boolean loadFromElement(Element root)
    {
        if(null != root)
        {
            projectRoot = root;
            return true;
        }
        return false;
    }

    public Element getEnvironmentElement()
    {
        if(null == projectRoot)
        {
            return null;
        }
        return projectRoot.getChild(ENVIRONMENT_ELEMENT_NAME);
    }

    public Element getSolutionElement()
    {
        if(null == projectRoot)
        {
            return null;
        }
        return projectRoot.getChild(SOLUTION_ELEMENT_NAME);
    }

}
