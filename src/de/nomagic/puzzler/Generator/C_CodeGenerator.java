
package de.nomagic.puzzler.Generator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.FileGroup.TextFile;
import de.nomagic.puzzler.progress.ProgressReport;
import de.nomagic.puzzler.solution.Solution;

public class C_CodeGenerator extends Generator
{
	public final static String C_FILE_FILE_COMMENT_SECTION_NAME        = "FileHeader";
	public final static String C_FILE_INCLUDE_SECTION_NAME             = "include";
	public final static String C_FILE_TYPE_SECTION_NAME                = "typedef";
	public final static String C_FILE_GLOBAL_VAR_SECTION_NAME          = "globalVar";
	public final static String C_FILE_LOCAL_VAR_SECTION_NAME           = "staticVar";
	public final static String C_FILE_FUNCTIONS_SECTION_NAME           = "publicFunctions";
	public final static String C_FILE_STATIC_FUNCTIONS_SECTION_NAME    = "privateFunctions";
	public final static String C_FILE_MAIN_FUNCTION_START_SECTION_NAME = "main_start";
	public final static String C_FILE_MAIN_FUNCTION_LOOP_SECTION_NAME  = "main_loop";
	public final static String C_FILE_MAIN_FUNCTION_END_SECTION_NAME   = "main_end";
	
	private ProgressReport report;

	public C_CodeGenerator(ProgressReport report) 
	{
		this.report = report;
	}
	
	public FileGroup generateFor(Solution s) 
	{
		if(null == s)
		{
			report.addError(this, "No Solution provided !");
			return null;
		}
		
		FileGroup codeGroup = new FileGroup();
		// we will need at least one *.c file. So create that now.
		TextFile mainC = new TextFile("main.c");
		codeGroup.add(mainC);
		mainC.createSections(new String[] {C_FILE_FILE_COMMENT_SECTION_NAME,
				                           C_FILE_INCLUDE_SECTION_NAME,
				                           C_FILE_TYPE_SECTION_NAME,
				                           C_FILE_GLOBAL_VAR_SECTION_NAME,
				                           C_FILE_LOCAL_VAR_SECTION_NAME,
				                           C_FILE_FUNCTIONS_SECTION_NAME,
				                           C_FILE_STATIC_FUNCTIONS_SECTION_NAME,
				                           C_FILE_MAIN_FUNCTION_START_SECTION_NAME,
				                           C_FILE_MAIN_FUNCTION_LOOP_SECTION_NAME,
				                           C_FILE_MAIN_FUNCTION_END_SECTION_NAME});
		
		// there should be a file comment explaining what this is
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		mainC.addLines(C_FILE_FILE_COMMENT_SECTION_NAME, new String[] {"/*",
				                                                       "  automatically created main.c", 
				                                                       "  created at: " + dateFormat.format(date),
				                                                       "  created from " + cfg.getString(Solution.SOLUTION_FILE_CFG),
				                                                       "*/"});

		// we will have a main function -> create that now
		mainC.addLines(C_FILE_MAIN_FUNCTION_START_SECTION_NAME, new String[] {"int main(void)","{"});
		
		//TODO : alternative to super loop
		// -> we have a super loop -> set that up
		mainC.addLines(C_FILE_MAIN_FUNCTION_LOOP_SECTION_NAME, new String[] {"\tfor(;;)","\t{"});
		mainC.addLines(C_FILE_MAIN_FUNCTION_END_SECTION_NAME, new String[] {"\t}","}"});
		
		return codeGroup;
	}

}
