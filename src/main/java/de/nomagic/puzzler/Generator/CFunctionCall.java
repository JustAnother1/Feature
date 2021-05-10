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
/** represents a function call.
 *
 */
package de.nomagic.puzzler.Generator;

public class CFunctionCall implements FunctionCall
{
    private String api;
    private final String functionName;
    private String functionArguments;

    public CFunctionCall(String functionDescriptor)
    {
        // Split functionName into API Function name and parameters
        if(true == functionDescriptor.contains(":"))
        {
            api = functionDescriptor.substring(0, functionDescriptor.indexOf(":"));
            functionDescriptor = functionDescriptor.substring( functionDescriptor.indexOf(":") + 1);
        }
        else
        {
            api = null;
        }
        if(true == functionDescriptor.contains("("))
        {
            // we call a function with parameters
            functionName = functionDescriptor.substring(0, functionDescriptor.indexOf('('));
            functionArguments = functionDescriptor.substring(functionDescriptor.indexOf('(') + 1, functionDescriptor.lastIndexOf(')'));
        }
        else
        {
            // no parameters
            functionName = functionDescriptor;
            functionArguments = null;
        }
    }

    @Override
    public String toString()
    {
        return api + ":" + functionName + "(" + functionArguments + ")";
    }

    public String getApi()
    {
        return api;
    }

    public void setApi(String api)
    {
        this.api = api;
    }

    public String getArguments()
    {
        return functionArguments;
    }

    public void setFunctionArguments(String functionArguments)
    {
        this.functionArguments = functionArguments;
    }

    public String getName()
    {
        return functionName;
    }
}
