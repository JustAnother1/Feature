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
package de.nomagic.puzzler.Environment;

import org.jdom2.Element;

public interface Environment
{
    public static final String ROOT_ELEMENT_NAME = "environment";
    public static final String TOOL_ELEMENT_NAME = "tool";
    public static final String BUILD_SYSTEM_ELEMENT_NAME = "build";
    public static final String ROOT_API_ELEMENT_NAME = "root_api";
    public static final String BUILD_SYSTEM_TYPE_ATTRIBUTE_NAME = "type";
    public static final String TOOL_NAME_ATTRIBUTE_NAME = "name";
    public static final String ROOT_API_NAME_ATTRIBUTE_NAME = "name";
    public static final String PIN_MAPPING_ELEMENT_NAME = "resources";
    public static final String ADDITIONAL_FILES_ROOT_ELEMENT_NAME = "additional";


    boolean loadFromElement(Element environmentRoot);
    String[] getPlatformParts();
    String getBuldSystemType();
    String getRootApi();
    boolean provides(String name);
    Element getAlgorithmCfg(String algoName);
    Element[] getConfigFile(String postfix, String RootElementName);

}
