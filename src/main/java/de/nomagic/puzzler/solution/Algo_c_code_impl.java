package de.nomagic.puzzler.solution;

import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Content.CType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Base;
import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Generator.C_CodeGenerator;
import de.nomagic.puzzler.Generator.C_FunctionCall;
import de.nomagic.puzzler.Generator.Generator;

/** This class reads the c_code element in an Algorithm XML.
*
* This provides convenient access functionality for Generators
* to extract C Code information from a single Algorithm.
*
*/
public class Algo_c_code_impl extends Base implements Algo_c_code
{
    public static final String ALGORITHM_CODE_CHILD_NAME = "c_code";
    public static final String ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME = "name";
    public static final String ALGORITHM_IF_CHILD_NAME = "if";
    public static final String ALGORITHM_FOR_CHILDS_CHILD_NAME = "forChilds";
    public static final String ALGORITHM_FOR_CHILDS_CHILD_API_ATTRIBUTE = "api";
    public static final String ALGORITHM_ADDITIONAL_CHILD_NAME = "additional";
    public static final String IMPLEMENTATION_PLACEHOLDER_REGEX = "€";
    public static final char   IMPLEMENTATION_PLACEHOLDER_CHAR = '€';

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final AlgorithmInstanceInterface algo;
    
    public Algo_c_code_impl(Context ctx, AlgorithmInstanceInterface configuredAlgorithm)
    {
        super(ctx);
        this.algo = configuredAlgorithm;
    }
    
    @Override
    public String toString()
    {
    	return "Algo_c_code_impl for " + algo.toString();
    }
    
    /** extract the implementation of the function from the XML.
    *
    * @param functionToCall requested function
    * @return implementation
    */
   public String getFunctionImplementation(C_FunctionCall functionToCall)
   {
       if(null == functionToCall)
       {
           return null;
       }
       Element func = getFunctionElement(functionToCall.getName());
       if(null == func)
       {
           return null;
       }
       log.trace("function element: {} !", func);
       String implementation = getImplementationFromFunctionElement(func, functionToCall.getArguments());
       return implementation;
   }

   public String getFunctionParameterValue(String ParameterName,
           C_FunctionCall Function)
   {
       if(null == ParameterName)
       {
           log.error("Function parameter name is null !");
           return null;
       }
       if(null == Function)
       {
           log.error("Function parameters are null !");
           return null;
       }
       Element functionElement = getFunctionElement(Function.getName());
       if(null == functionElement)
       {
           log.error("Function name is null or invalid ({}) !", Function.getName());
           return null;
       }

       // Reference to Algorithm parameter
       // get which parameter this is (1st 2nd 3rd,..)
       int paramIndex = 0;
       do {
           Attribute attr = functionElement.getAttribute("param" + paramIndex + "_name");
           if(null == attr)
           {
               log.trace("Function parameter '{}' not found !(Algorithm configuration?)", ParameterName);
               return null;
           }
           if(true == ParameterName.equals(attr.getValue()))
           {
               break;
           }
           else
           {
               paramIndex++;
           }
       }while(true);

       // get value for that parameter from FunctionParameters
       String[] parameters = Function.getArguments().split(",");
       if(paramIndex < parameters.length)
       {
           return parameters[paramIndex];
       }
       else
       {
           ctx.addError(this, "Could not get the " + paramIndex
               + ". parameter to this function from the parameters given as "
               + Function.getArguments() );
           return null;
       }
   }

   private Element getFunctionElementFromElement(Element src, String FunctionName)
   {
       List<Element> funcs = src.getChildren(Generator.ALGORITHM_FUNCTION_CHILD_NAME);
       log.trace("func.size() : {}", funcs.size());
       for(int i = 0; i < funcs.size(); i++)
       {
           // check all functions
           Element curElement = funcs.get(i);
           String curName = curElement.getAttributeValue(ALGORITHM_FUNCTION_NAME_ATTRIBUTE_NAME);
           log.trace("func.name : {}", curName);
           if(true == FunctionName.equals(curName))
           {
               // found the correct function
               return curElement;
           }
       }
       return null;
   }

   private Element getFunctionElement(String searchedFunctionName)
   {
       // get all code from Algorithm.
       Element cCode = algo.getAlgorithmElement(C_CodeGenerator.ALGORITHM_CODE_CHILD_NAME);
       if(null == cCode)
       {
           ctx.addError(this,
               "Could not read implementation for " + searchedFunctionName +
               " from " + toString());
           return null;
       }

       Element res;

       // pick requested function from all code of algorithm.
       res = getFunctionElementFromElement(cCode, searchedFunctionName);

       if(null == res)
       {
           // check additional functions
           Element additional = cCode.getChild(ALGORITHM_ADDITIONAL_CHILD_NAME);
           if(null != additional)
           {
               res = getFunctionElementFromElement(additional, searchedFunctionName);
           }
           // else no additional functions in this algorithm.
       }
       if(null == res)
       {
           // function not found
           ctx.addError(this, "Function call to missing function! (" + algo
                           + ", function name : " + searchedFunctionName + ")");
       }
       return res;
   }

   /** extracts the implementation from the XML element.
    *
    * this only gets the implementation parts that are active. (evaluates IF).
    * It does not replace place holders!
    *
    * @param function
    * @param FunctionArguments
    * @return
    */
   private String getImplementationFromFunctionElement(Element function,
           String FunctionArguments)
   {
       // some part of the Implementation might be conditional, So only select the valid parts
       StringBuilder sb = new StringBuilder();
       List<Content> parts = function.getContent();
       for (int i = 0; i < parts.size() ; i++)
       {
           Content curC = parts.get(i);
           if(CType.Element == curC.getCType())
           {
        	   log.trace("found elment {} !", curC);
               String impl = getImplementationFromElement( (Element)curC, FunctionArguments,function);
               if(null != impl)
               {
                   sb.append(impl);
               }
           }
           else if(CType.Comment == curC.getCType())
           {
               // ignore comments
           }
           else
           {
               // Not an element, therefore can not have if conditions,
               // therefore we can just extract all the text.
               String impl = beautifyImplementation(curC.getValue());
               sb.append(impl);
           }
       }
       return sb.toString();
   }

   private String getImplementationFromElement(Element curE,
           String FunctionArguments,
           Element function)
   {
       String impl = null;
       if(ALGORITHM_IF_CHILD_NAME.equals(curE.getName()))
       {
           impl = get_conditional_part(curE, FunctionArguments,function);
       }
       else  if(ALGORITHM_FOR_CHILDS_CHILD_NAME.equals(curE.getName()))
       {
           impl = get_forChilds_part(curE);
       }
       else
       {
           impl = beautifyImplementation(curE.getText());
           log.warn("Ignoring not recognised Element in implementation ! text:  {} element: {}",
                   impl, curE);
           return null;
       }
       return impl;
   }

   private String get_conditional_part(Element element,
           String FunctionArguments,
           Element function)
   {
       // this part is conditional -> check if we need it.
       Element active = ConditionEvaluator.getBest(element, algo, FunctionArguments, function);
       if(null != active)
       {
           String impl = beautifyImplementation(active.getText());
           log.trace("adding the conditioned parts to the implementation : {}", impl);
           return impl;
       }
       else
       {
           log.trace("The condition {} is not true",
                   element.getAttribute(ConditionEvaluator.CONDITION_EVALUATOR_CONDITION_ATTRIBUTE_NAME));
       }
       // else this part is not used this time.
       return null;
   }

   private String get_forChilds_part(Element element)
   {
       String apiName = element.getAttributeValue(ALGORITHM_FOR_CHILDS_CHILD_API_ATTRIBUTE);
       if(null == apiName)
       {
           ctx.addError(this, "" + algo + " : for childs element with missing api attribute !)");
           return null;
       }
       log.trace("api : {} !", apiName);

       StringBuilder sb = new StringBuilder();
       String commonCode = element.getText();
       log.trace("generic code : {} !", commonCode);
       
       Iterator<String> it = algo.getAllChildren();
       if(null == it)
       {
    	   return null;
       }
       while(it.hasNext())
       {
           String childName = it.next();
           log.trace("child : {} !", childName);
           AlgorithmInstanceInterface curChild = algo.getChild(childName);
           if(true == curChild.hasApi(apiName))
           {
               String implementation = replaceGenericWithSpecific(commonCode, apiName, curChild.getName());
               implementation = beautifyImplementation(implementation);
               log.trace("implementation : {} !", implementation);
               sb.append(implementation);
           }
           // else don't care for that child
       }

       if(sb.length() > 0)
       {
           return sb.toString();
       }
       else
       {
           return null;
       }
   }

   private String beautifyImplementation(String impl)
   {
       impl = impl.trim();
       if(0 < impl.length())
       {
           if(false == impl.endsWith(System.getProperty("line.separator")))
           {
               impl = impl + System.getProperty("line.separator");
           }
           return impl;
       }
       else
       {
           return "";
       }
   }

   private String replaceGenericWithSpecific(String line, String generic, String specific)
   {
       if(null == line)
       {
           return "";
       }
       if(true == line.contains(IMPLEMENTATION_PLACEHOLDER_REGEX))
       {    	   
           StringBuilder res = new StringBuilder();
           String[] parts = line.split(IMPLEMENTATION_PLACEHOLDER_REGEX);
           for(int i = 0; i < parts.length; i++)
           {
               if(0 == i%2)
               {
                   // outside of IMPLEMENTATION_PLACEHOLDER_REGEX
                   res.append(parts[i]);
               }
               else
               {
                   // this is where we want to replace
            	   res.append(IMPLEMENTATION_PLACEHOLDER_REGEX);
                   res.append(parts[i].replaceFirst(generic, specific));
                   res.append(IMPLEMENTATION_PLACEHOLDER_REGEX);
               }               
           }
           String res_str = res.toString();
           log.trace("replacing {} to {} !", line, res_str);
           return res_str;
       }
       else
       {
           // nothing to replace
           return line;
       }
   }

}
