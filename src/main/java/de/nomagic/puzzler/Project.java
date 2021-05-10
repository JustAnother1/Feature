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

public interface Project 
{
    public final static String PROJECT_ROOT_ELEMENT_NAME = "project";
    public final static String ENVIRONMENT_ELEMENT_NAME = "environment";
    public final static String SOLUTION_ELEMENT_NAME = "solution";
    public boolean loadFromElement(Element root);
    public Element getEnvironmentElement();
    public Element getSolutionElement();
}
