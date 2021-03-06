package de.nomagic.puzzler.BuildSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.Environment.Environment;
import de.nomagic.puzzler.FileGroup.AbstractFile;
import de.nomagic.puzzler.FileGroup.EmptyFolder;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.FileGroup.TextFile;
import de.nomagic.puzzler.configuration.Configuration;

public class EmbeetleMakeBuildSystem extends BuildSystem
{
    public static final String MAKEFILE_FILE_COMMENT_SECTION_NAME = "FileHeader";
    public static final String MAKEFILE_FILE_VARIABLES_SECTION_NAME = "Variables";
    public static final String MAKEFILE_FILE_TARGET_SECTION_NAME = "targets";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private HashMap<String, String> listVariables = new HashMap<String, String>();
    private ArrayList<Target> targets = new ArrayList<Target>();

    public EmbeetleMakeBuildSystem(Context ctx)
    {
        super(ctx);
    }

    @Override
    public boolean hasTargetFor(String source)
    {
        if(null == source)
        {
            return false;
        }
        for(int i = 0; i < targets.size(); i++)
        {
            Target t = targets.get(i);
            if(source.equals(t.getSource()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addTarget(Target aTarget)
    {
        if(true == aTarget.isDefault())
        {
            listVariables.put(".DEFAULT_GOAL", aTarget.getOutput());
        }
        targets.add(aTarget);
    }

    @Override
    public void extendListVariable(String list, String newElement)
    {
        newElement = newElement.trim();
        String oldList = listVariables.get(list);
        if(null == oldList)
        {
            // This list did not exist -> create the list
            listVariables.put(list, newElement);
        }
        else
        {
            String newList = oldList + " " + newElement;
            listVariables.put(list, newList);
        }
    }

    @Override
    public void addRequiredVariable(String name)
    {
        requiredEnvironmentVariables.put(name, "");
    }

    @Override
    public void addVariable(String variName, String variValue)
    {
        listVariables.put(variName, variValue);
    }

    private AbstractFile createMakeFile()
    {
        TextFile makeFile = new TextFile("config/Makefile");
        makeFile.separateSectionWithEmptyLine(true);
        makeFile.createSections(new String[]
                { MAKEFILE_FILE_COMMENT_SECTION_NAME });

        makeFile.addLines(MAKEFILE_FILE_COMMENT_SECTION_NAME,
                       new String[] {
            "################################################################################",
            "#                                                                              #",
            "#                      MAKEFILE FOR EMBEETLE PROJECTS                          #",
            "#                                                                              #",
            "################################################################################",
            "# COPYRIGHT (c) 2020 Johan Cockx",
            "# This software component is licensed by Embeetle under the MIT license. Please",
            "# consult the license text at the bottom of this file.",
            "#",
            "# Compatible with Embeetle makefile interface version 2",
            "# To avoid a warning when opening this project in Embeetle, always keep the",
            "# above line somewhere in this makefile as long as it includes Embeetle's",
            "# 'dashboard.mk' or 'filetree.mk'.",
            "#",
            "# ------------------------------------------------------------------------------",
            "# |                           HOW TO RUN THIS MAKEFILE                         |",
            "# ------------------------------------------------------------------------------",
            "# The CWD (Current Working Directory) should be the build folder!",
            "# So first navigate to the build folder, then run make. For example:",
            "#",
            "#   $ cd C:/beetle_projects/my_project/build",
            "#",
            "#   $ make build",
            "#          -f ../config/makefile",
            "#          \"TOOLPREFIX=C:/my_tools/gnu_arm_toolchain_9.2.1/bin/arm-none-eabi-\"",
            "#          \"OCD=C:/my_tools/openocd_0.10.0_dev01138_32b/bin/openocd.exe\"",
            "#",
            "# The arguments TOOLPREFIX and OCD are absolute paths to the compiler toolchain",
            "# (ARM or RISCV) and the flash/debug server (OpenOCD or PyOCD) respectively. You",
            "# should provide them if they're not available on your PATH environment",
            "# variable.",
            "#",
            "# Note: Aside from running this makefile PATH-independent, we've got another",
            "#       valid reason to provide absolute paths on the commandline for tools.",
            "#       Read more in ADDENDUM 2 at the bottom.",
            "################################################################################",
            "",
            "# 1. MAKEFILE INCLUDES",
            "# ====================",
            "# Embeetle generates two include files for make in the same directory as the",
            "# makefile itself. You don't need to include these, but if you don't, some",
            "# features of the Embeetle user interface will not work as described below.",
            "#",
            "# First determine the directory containing the makefile:",
            "MAKEFILE := $(lastword $(MAKEFILE_LIST))",
            "MAKEFILE_DIR := $(dir $(MAKEFILE))",
            "",
            "# 1.1 dashboard.mk",
            "# ----------------",
            "# Include the generated file 'dashboard.mk'. It defines the variables that are",
            "# dependent on the user's choices in the dashboard. It must be included for the",
            "# Embeetle dashboard mechanism to work.",
            "#",
            "# 'dashboard.mk' defines the following variables:",
            "#",
            "#     - TOOLPREFIX         : A fallback value like 'arm-none-eabi-' in case this",
            "#                            makefile is invoked without giving an absolute path",
            "#                            to TOOLPREFIX in the commandline.",
            "#",
            "#     - ELF_FILE           : Relative(*) path to .elf file.",
            "#",
            "#     - SOURCE_DIR         : Relative(*) path to the source directory.",
            "#",
            "#     - TARGET_COMMONFLAGS : Flags for C++, C and assembly compilation and",
            "#                            linking.",
            "#",
            "#     - TARGET_CFLAGS      : C compilation flags.",
            "#",
            "#     - TARGET_CPPFLAGS    : C++ compilation flags.",
            "#",
            "#     - TARGET_SFLAGS      : Assembler flags.",
            "#",
            "#     - TARGET_LDFLAGS     : Linker flags.",
            "#",
            "#     - EEPROM_FLAGS       : Flags used by objcopy to generate an EEPROM .eep",
            "#                            file from the .elf file. This variable is only",
            "#                            defined if the microcontroller actually has",
            "#                            EEPROM memory.",
            "#",
            "#     - HEX_FLAGS          : Extra flags used by objcopy to generate the .hex",
            "#                            file from the .elf file. This variable is only",
            "#                            defined if needed (eg. for ATmega328 microcon-",
            "#                            trollers, because they need special flags to deal",
            "#                            with EEPROM memory).",
            "#",
            "#     - FLASH              : The front-end software used to flash the firmware",
            "#                            to the microcontroller (usually GDB).",
            "#",
            "#     - FLASHFLAGS         : Flags given to the FLASH software (usually these",
            "#                            flags contain a reference to the .gdbinit file",
            "#                            where instructions are found to launch OpenOCD).",
            "#",
            "#     - PREPARE_FLASH_BOOTLOADER_FLAGS: Flags given to the FLASH software to",
            "#                                       initialize the flashing of a bootloader.",
            "#",
            "#     - FLASH_BOOTLOADER_FLAGS: Flags given to the FLASH software to flash a",
            "#                               bootloader",
            "#",
            "# 'dashboard.mk' also defines the following variables, but they're only used",
            "# inside 'dashboard.mk' itself:",
            "#",
            "#     - OCD                : A fallback value like 'openocd' in case this make-",
            "#                            file is invoked without giving an absolute path to",
            "#                            OCD in the commandline.",
            "#",
            "#     - LINKERSCRIPT       : Relative(*) path to the linkerscript.",
            "#",
            "#     - BOOTLOADER_FILE    : Relative(*) path to a bootloader binary file.",
            "#",
            "#     - GDB_FLASHFILE      : Relative(*) path to the .gdbinit file that contains",
            "#                            the flash commands.",
            "#",
            "#     - OPENOCD_PROBEFILE  : Relative(*) path to the OpenOCD config file that",
            "#                            defines the flash/debug probe.",
            "#",
            "#     - OPENOCD_CHIPFILE   : Relative(*) path to the OpenOCD config file that",
            "#                            defines the microcontroller.",
            "#",
            "#     - PYOCD_FILE         : Relative(*) path to the PyOCD config file.",
            "#",
            "# (*) Note: all relative paths are relative with",
            "#           respect to the build folder.",
            "#",
            "include $(MAKEFILE_DIR)dashboard.mk",
            "",
            "# 1.2 filetree.mk",
            "# ---------------",
            "# Include the generated file 'filetree.mk'. It defines lists of source files and",
            "# header directories selected in the Embeetle filetree (presented as green",
            "# and red checkboxes).",
            "#",
            "# 'filetree.mk' defines the following variables:",
            "#",
            "#     - CFILES   : List of c-files that should be compiled.",
            "#     - CPPFILES : List of cpp-files that should be compiled.",
            "#     - SFILES   : List of s-files (assembly files) that should be compiled.",
            "#     - HDIRS    : List of h-directories (directories containing h-files) that",
            "#                  should be added to the compiler's searchpath.",
            "#",
            "# Embeetle automatically constructs these lists in 'filetree.mk'. For more info,",
            "# check this webpage:",
            "# https://embeetle.com/#embeetle-ide/manual/build/source-file-selection",
            "#",
            "include $(MAKEFILE_DIR)filetree.mk",
            "",
            "# 2. HOST OS SPECIFIC COMMANDS",
            "# ============================",
            "# The host OS is the operating system on which make is running (Windows, Linux,",
            "# ..). This differs from the target OS running on the target microcontroller",
            "# (FreeRTOS, Mbed-OS, Chibi-OS, .. or none at all).",
            "#",
            "# The commands to be used on Windows depend on the command shell being used.",
            "# The default CMD shell is sh.exe.",
            "ifeq ($(SHELL),sh.exe)",
            "  RM = del /F /Q",
            "  REMOVE_ALL = \\",
            "    cmd /Q /C \"for /D %%p IN (*) do rmdir /S /Q %%p && for %%p in (*) do del %%p\"",
            "  MKDIR = mkdir 2>NUL",
            "  PSEP=\\\\",
            "  ECHO = @echo \\#",
            "  TOUCH = type NUL >>",
            "  PATHSEP = ;",
            "else",
            "  RM = rm -f",
            "  REMOVE_ALL = rm -rf * .[!.]* ..?*",
            "  MKDIR = mkdir -p",
            "  PSEP=/",
            "  ECHO = \\#",
            "  TOUCH = touch",
            "  PATHSEP = :",
            "endif",
            "",
            "# 3. SHADOW BUILDING",
            "# ==================",
            "# This makefile supports 'shadow building', i.e. building the project in another",
            "# directory than the source directory. Shadow building has several advantages:",
            "#",
            "#  - build artifacts are cleanly separated from source files",
            "#",
            "#  - the 'clean' target can simply delete all files",
            "#",
            "#  - it is possible to simultanuously build different configurations from the",
            "#    same set of source files",
            "#",
            "# Potential disadvantages of shadow building are:",
            "#",
            "#  - shadow building needs a way to find the source files, as they are not",
            "#    located in the build directory",
            "#",
            "#  - when starting 'make' from the command line, the makefile to be used must",
            "#    be explicitly specified",
            "#",
            "# $(SOURCE_DIR) can be an absolute path or it can be relative to the build",
            "# directory. This makefile does not assume to be located in the source",
            "# directory, although it can be.",
            "#",
            "# If you include 'dashboard.mk', $(SOURCE_DIR) is already defined. If it isn't,",
            "# for example because you decided not to include 'dashboard.mk', we look for the",
            "# source directory in a list of directories $(SOURCE_PATH). The first existing",
            "# directory is used. The value below works well with the default structure of an",
            "# Embeetle project.  If you have a different structure, you may need to change",
            "# $(SOURCE_PATH) or define $(SOURCE_DIR) in some other way.",
            "#",
            "SOURCE_PATH = ../source/ $(MAKEFILE_DIR)../source/ $(MAKEFILE_DIR)",
            "ifeq ($(SOURCE_DIR),)",
            "  SOURCE_DIR = $(firstword $(wildcard $(SOURCE_PATH)))",
            "else",
            "  # Make sure the $(SOURCE_DIR) variable has exactly one trailing slash.",
            "  ifneq ($(patsubst %//,%/,$(SOURCE_DIR)/),$(SOURCE_DIR))",
            "    $(error $(SOURCE_DIR) should end in a trailing slash '/')",
            "  endif",
            "endif",
            "ifeq ($(wildcard $(SOURCE_DIR)),)",
            "  $(error Source directory $(SOURCE_DIR) not found)",
            "endif",
            "",
            "# VPATH is make's search path for source files not found in the build directory.",
            "VPATH = $(SOURCE_DIR)",
            "        ",
            "# Check that we are using shadow building. If you want to allow building in the",
            "# source directory, comment or remove the check below. Take care: shadow",
            "# building cannot be combined with building in the source directory. If the",
            "# source directory contains build artefacts like object files, these will be",
            "# reused and potentially overwritten by a shadow build.",
            "#",
            "ifeq ($(abspath $(SOURCE_DIR)),$(abspath .))",
            "  $(warning Please use shadow building:)",
            "  $(warning create a new directory and build there using)",
            "  $(warning $   $(MAKE) -f $(abspath $(MAKEFILE)))",
            "  $(error Attempt to build in the source directory)",
            "else",
            "  # Protect against shadow building in non-build directories. Shadow building",
            "  # is only allowed in an empty directory and in a directory containing a .build",
            "  # file.  In an empty directory, a .build file is automatically created.",
            "  # If you don't want this protection, comment or remove the check below.",
            "  ifeq ($(wildcard * .[^.]* ..?*),)",
            "    $(shell $(TOUCH) .build)",
            "  endif",
            "  ifeq ($(wildcard .build),)",
            "    $(warning Build directory not empty and .build file not found)",
            "    $(warning Create an empty .build file if you really want to build here)",
            "    $(error Attempt to build in a non-empty non-build directory)",
            "  endif",
            "  ",
            "  # If the source directory has subdirectories, create the same subdirectories",
            "  # in the build directory, so that the object file for a source file in a",
            "  # subdirectory can be created in the corresponding subdirectory of the build",
            "  # directory.",
            "  SUBDIRS = $(sort $(dir $(CPPFILES) $(CFILES) $(SFILES)))",
            "  $(shell $(MKDIR) . $(subst /,$(PSEP),$(SUBDIRS)))",
            "endif",
            "",
            "# 4. COMPILERS",
            "# ============",
            "# Define the compilers to be used for compilation. We'll use cross compilers",
            "# for the target microcontroller. These typically have the same command names as",
            "# a native gcc compiler, with a prefix (e.g. arm-none-eabi- for ARM",
            "# cross-compilers).",
            "#",
            "# Note: A cross compiler is a compiler that runs on one machine (e.g. a Windows",
            "#       desktop) to compile code for another type of machine (e.g. your",
            "#       microcontroller).",
            "#",
            "# The TOOLPREFIX variable can be given a value on the command line. If not, the",
            "# default value given in 'dashboard.mk' is used. In Embeetle, we use an absolute",
            "# path for TOOLPREFIX. See ADDENDUM 2 on the bottom why we do that.",
            "#",
            "# We use the C and C++ compilers(*), as well as the 'objcopy' and 'size'",
            "# commands. The C compiler is also used for assembly code. For linking,  we use",
            "# the C++ compiler if there are any C++ files in the project,  and the C compiler",
            "# otherwise. The C++ compiler will link in the C++ standard libraries.",
            "#",
            "# (*) Strictly speaking,  'gcc' and 'g++' are not compilers but \"driver",
            "#     programs\",  programs that call the compiler and other tools as appropriate",
            "#     for the arguments you give them.",
            "#",
            "CC      = \"$(TOOLPREFIX)gcc\"",
            "CXX     = \"$(TOOLPREFIX)g++\"",
            "LD      = $(if $(CXXFILES),$(CXX),$(CC))",
            "OBJCOPY = \"$(TOOLPREFIX)objcopy\"",
            "OBJSIZE = \"$(TOOLPREFIX)size\"",
            "GDB     = \"$(TOOLPREFIX)gdb\"",
            "",
            "# 5. COMPILATION FLAGS",
            "# ====================",
            "# 5.1 COMMONFLAGS",
            "# ---------------",
            "#",
            "# Common flags for C/C++ and assembly compilations and linking.",
            "#",
            "#     $(TARGET_COMMONFLAGS) CPU specific compilation flags, defined in",
            "#                           dashboard.mk",
            "#",
            "#     -Og                   The optimization level of choice for the standard",
            "#                           edit-compile-debug cycle, offering a reasonable",
            "#                           level of optimization while maintaining fast",
            "#                           compilation and a good debugging experience.  It is",
            "#                           a better choice than -O0 for producing debuggable",
            "#                           code because some compiler passes that collect debug",
            "#                           information are disabled at -O0.",
            "#",
            "#     -g3                   Maximize debug information.",
            "#",
            "#     -MMD                  Tell preprocessor to generate dependency (.d) files",
            "#                           that will help make to determine what files to re-",
            "#                           compile after a change in a header file.",
            "#",
            "#     -fmessage-length=0    Try to format error messages so that they fit on li-",
            "#                           nes of about n characters. If n=0, they'll be prin-",
            "#                           ted on a single line.",
            "#",
            "#     -ffunction-sections   Generate a separate ELF section for each function in",
            "#                           the source file. The unused section elimination fea-",
            "#                           ture of the linker can then remove unused functions",
            "#                           at link time.",
            "#",
            "#     -fdata-sections       Enable the generation of one ELF section for each",
            "#                           variable in the source file.",
            "#",
            "#     -Wno-comment          Do not warn when /* appears in the middle of a /* */",
            "#                           comment.",
            "#",
            "#     -Wno-unused-function  Do not warn for a static function that is declared",
            "#                           but not defined, or a non\\-inline static function",
            "#                           that is unused.",
            "#",
            "#     -Werror-implicit-function-declaration    Give a warning (or error) when-",
            "#                                              ever a function is used before",
            "#                                              being declared.",
            "#",
            "# ABOUT WARNINGS:",
            "#     The flags -Wall and -Wextra make sure that most potential problems are",
            "#     reported as warnings. We did not add these flags by default because they",
            "#     trigger a lot of warnings in third party code in many sample projects, and",
            "#     we don't have the resources to fix all of them. However, we do recommend",
            "#     to add these flags for your own projects.",
            "#",
            "#     The flag -Werror turns all these warnings into errors. To avoid acciden-",
            "#     tally missing an important warning, add this flag and edit your source",
            "#     code until no warnings are reported. For even more warnings, see",
            "#     https://kristerw.blogspot.com/2017/09/useful-gcc-warning-options-not-enabled.html",
            "#",
            "COMMONFLAGS = \\",
            "  $(TARGET_COMMONFLAGS) \\",
            "  -Og \\",
            "  -g3 \\",
            "  -MMD \\",
            "  -fmessage-length=0 \\",
            "  -ffunction-sections \\",
            "  -fdata-sections \\",
            "  -Wno-comment \\",
            "  -Wno-unused-function \\",
            "  -Werror-implicit-function-declaration \\",
            "",
            "# 5.2 HDIRS_INCLUDES",
            "# -------------------",
            "# List all directories with relevant h-files, each directory preceded by '-I'.",
            "HDIRS_INCLUDES = $(patsubst %, -I$(SOURCE_DIR)%, $(HDIRS)) \\",
            "",
            "# 5.3 CFLAGS",
            "# -----------",
            "# C compilation flags.",
            "# Note: The variable TARGET_CFLAGS gets defined in 'dashboard.mk'.",
            "CFLAGS = $(COMMONFLAGS) $(TARGET_CFLAGS) $(HDIRS_INCLUDES)",
            "",
            "# 5.4 CPPFLAGS",
            "# -------------",
            "# C++ compilation flags.",
            "# Note: The variable TARGET_CPPFLAGS gets defined in 'dashboard.mk'.",
            "CPPFLAGS = $(COMMONFLAGS) $(TARGET_CPPFLAGS) $(HDIRS_INCLUDES)",
            "",
            "# 5.5 SFLAGS",
            "# -----------",
            "# Assembly specific compilation flags.",
            "# Note: The variable TARGET_SFLAGS gets defined in 'dashboard.mk'.",
            "SFLAGS = $(COMMONFLAGS) $(TARGET_SFLAGS) $(HDIRS_INCLUDES)",
            "",
            "# 5.6 LDFLAGS",
            "# ------------",
            "# Linker flags.",
            "# Note: The variable TARGET_LDFLAGS gets defined in 'dashboard.mk'.",
            "#",
            "#     -Wl,-Map=output.map    Pass \"-Map output.map\" flag to linker,",
            "#                            request output.map generation.",
            "#",
            "#     -Wl,--gc-sections      Pass \"--gc-sections\" flag to linker,",
            "#                            garbage collect unused sections.",
            "#",
            "LDFLAGS  = \\",
            "  -Wl,-Map=output.map \\",
            "  -Wl,--gc-sections \\",
            "  $(COMMONFLAGS) \\",
            "  $(TARGET_LDFLAGS) \\",
            "",
            "# 6. DEFAULT RULE",
            "# ===============",
            "# If no target is specified on the command line,  make builds the first target",
            "# of the makefile. By convention,  this is usually called 'default'.",
            "default: build",
            "",
            "# Note: The variable ELF_FILE gets defined in 'dashboard.mk'. If you did",
            "# not include 'dashboard.mk', it gets a default value here.",
            "ifeq ($(ELF_FILE),)",
            "ELF_FILE = application.elf",
            "endif",
            "",
            "# The eventual goal of the build target is to create all the binaries: .bin,",
            "# .elf and .hex. Potentially also the EEPROM file .eep if the variable",
            "# EEPROM_FLAGS is defined in 'dashboard.mk'. To create all these files, we just",
            "# need to add them as dependencies/prerequisites to the phony build target.",
            "BINARIES = \\",
            "  $(ELF_FILE) \\",
            "  $(ELF_FILE:.elf=.bin) \\",
            "  $(ELF_FILE:.elf=.hex) \\",
            "  $(if $(EEPROM_FLAGS),$(ELF_FILE:.elf=.eep))",
            "",
            ".PHONY: build",
            "build: print_build $(BINARIES) show_size",
            "",
            ".PHONY: show_size",
            "show_size: $(ELF_FILE:.elf=.size)",
            "",
            "# 7. INCLUDE ALL DEPENDENCY FILES",
            "# ===============================",
            "# Dependency (.d) files are generated by the preprocessor with the -MMD flag.",
            "# These files give make the necessary information to decide which files to",
            "# recompile when a header files changes.",
            "include $(wildcard $(patsubst %, %*.d, . $(SUBDIRS)))",
            "",
            "# 8. RULE TO IGNORE OLD H-FILES",
            "# =============================",
            "# Imagine following situation:",
            "#     main.c -> #include \"foo.h\"",
            "#     foo.c  -> #include \"foo.h\"",
            "#",
            "# After building foo.o, make creates the foo.d and main.d dependency files,",
            "# which contains the following lines:",
            "#     main.d -> main.o: main.c foo.h",
            "#     foo.d  -> foo.o: foo.c foo.h",
            "#",
            "# Imagine you then change the name \"foo\" into \"bar\".",
            "#     main.c -> #include \"bar.h\"",
            "#     bar.c  -> #include \"bar.h\"",
            "#",
            "# The dependency files are still the old ones upon the next build. So make",
            "# believes that main.o still depends on foo.h. Make searches for a rule to build",
            "# foo.h, cannot find such rule and gives the error: \"cannot find rule to build",
            "# foo.h\".  The empty rule below provides a rule to build foo.h (although nothing",
            "# really happens).  Make believes that foo.h is built, and moves on.",
            "#",
            "# As the dependency files are still the old ones, the dependency from main.o",
            "# upon bar.h is not yet declared in main.d.  However, main.o rebuilds anyhow",
            "# because the timestamp of main.c has changed.  Rebuilding main.o results in",
            "# overwriting the main.d dependency file, such that this dependency file is now",
            "# updated for the next run.",
            "%.h:",
            "\t$(ECHO) $@ moved or deleted",
            "",
            "# 9. DEPENDENCIES FOR THE FINAL .ELF FILE",
            "# =======================================",
            "# If you included Embeetle's 'filetree.mk' file in this makefile, then the",
            "# source file lists CFILES, CPPFILES and SFILES have been defined. Otherwise,",
            "# you have to define them yourself.",
            "#",
            "# The dependency on 'filetree.mk' below makes sure that the elf file is relinked",
            "# when the source file lists have changed, even if no source files changed. Feel",
            "# free to remove it if you do not want that behavior.",
            "#",
            "$(ELF_FILE): $(MAKEFILE_DIR)filetree.mk $(patsubst %, %.o, \\",
            "  $(CFILES) \\",
            "  $(CPPFILES) \\",
            "  $(SFILES) \\",
            ")",
            "",
            "# 10. RULES TO BUILD .O AND .ELF FILES",
            "# ====================================",
            "%.c.o: %.c",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Compile: $<",
            "\t$(CC) $(CFLAGS) $< -o $@ -c",
            "",
            "%.cpp.o: %.cpp",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Compile: $<",
            "\t$(CXX) $(CPPFLAGS) $< -o $@ -c",
            "",
            "%.cxx.o: %.cxx",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Compile: $<",
            "\t$(CXX) $(CPPFLAGS) $< -o $@ -c",
            "",
            "%.c++.o: %.c++",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Compile: $<",
            "\t$(CXX) $(CPPFLAGS) $< -o $@ -c",
            "",
            "%.cc.o: %.cc",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Compile: $<",
            "\t$(CXX) $(CPPFLAGS) $< -o $@ -c",
            "",
            "%.C.o: %.C",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Compile: $<",
            "\t$(CXX) $(CPPFLAGS) $< -o $@ -c",
            "",
            "%.s.o: %.s",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Compile: $<",
            "\t$(CC) $(SFLAGS) $< -o $@ -c",
            "",
            "%.asm.o: %.asm",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Compile: $<",
            "\t$(CC) $(SFLAGS) $< -o $@ -c",
            "",
            "%.S.o: %.S",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Compile: $<",
            "\t$(CC) $(SFLAGS) $< -o $@ -c",
            "",
            "%.elf:",
            "\t$(info )",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Link program into $@",
            "\t$(LD) $(LDFLAGS) -o $@ $(filter %.o %.a %.so,$^)",
            "",
            "# 11. RULES TO BUILD THE BIN AND HEX FILES FROM THE ELF",
            "# =====================================================",
            "%.bin: %.elf",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Preparing: $@",
            "\t$(OBJCOPY) -O binary $< $@",
            "",
            "%.hex: %.elf $(if $(EEPROM_FLAGS),%.eep,)",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Preparing: $@",
            "\t$(OBJCOPY) -O ihex $(HEX_FLAGS) $< $@",
            "",
            "%.eep: %.elf",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Preparing: $@",
            "\t$(OBJCOPY) -O ihex -j .eeprom $(EEPROM_FLAGS) $< $@",
            "",
            "%.size: %.elf",
            "\t$(info )",
            "\t$(info )",
            "\t$(ECHO) Preparing: $@",
            "\t$(OBJSIZE) $<",
            "",
            "# 12. CLEAN",
            "# =========",
            "# Remove files generated during the build process. For a shadow build,  all",
            "# files can be safely removed.  In the source or configuration directory or any",
            "# of their subdirectories, remove files only selectively.",
            "PROTECT_PATTERN = $(patsubst %,%/%,$(realpath $(SOURCE_DIR) $(MAKEFILE_DIR)))",
            ".PHONY: clean",
            "clean: print_clean",
            "ifeq ($(filter $(PROTECT_PATTERN), $(realpath .)/),)",
            "\t$(REMOVE_ALL)",
            "else",
            "\t$(RM) $(wildcard $(patsubst %, %*.o %*.d, $(SUBDIRS)) *.elf *.size *.bin)",
            "endif",
            "",
            "# 13. FLASH",
            "# =========",
            "# The 'flash' target flashes the .elf, .bin or .hex file to the microcontroller.",
            "# This is usually achieved through invoking the GDB tool and instructing it to",
            "# perform the flashing.",
            "#",
            "# The flash recipee looks like this:",
            "#   $(FLASH) $(FLASHFLAGS)",
            "#",
            "# Both variables are defined in 'dashboard.mk':",
            "#",
            "#   $(FLASH): The front-end software used to flash the firmware. Usually it's",
            "#             GDB (the Gnu Debugger, part of the gcc toolchain), but it can also",
            "#             be another software, eg. 'avrdude' for AVR microcontrollers.",
            "#",
            "#   $(FLASHFLAGS): The flags to run GDB/avrdude/... in such a way that it",
            "#                  flashes the .elf, .bin or .hex file to the microcontroller",
            "#                  and then exits.",
            "#",
            "#",
            "# 13.1 FLASHING BASED ON GDB - OPENOCD",
            "# ------------------------------------",
            "# The $(FLASHFLAGS) variable typically looks like this (if GDB uses OpenOCD",
            "# to do the flashing):",
            "#",
            "# FLASHFLAGS = \\",
            "#    -n \\",
            "#    -batch \\",
            "#    -x $(GDB_FLASHFILE) \\",
            "#    -ex \"flash-remote $(ELF_FILE) \"$(OCD)\" $(OPENOCD_PROBEFILE) $(OPENOCD_CHIPFILE)\" \\",
            "#",
            "# Let's first examine these flags one-by-one. Then we can check out the",
            "# variables inside them.",
            "#",
            "# 13.1.1 flags explained",
            "# ----------------------",
            "# These are the meanings of the flags given to GDB:",
            "#",
            "#   -n                Do not execute commands from any '.gdbinit' initialization",
            "#                     file (unless one is passed explicitely with '-x'). We add",
            "#                     this flag to ensure complete control over which files are",
            "#                     passed to GDB.",
            "#",
            "#   -batch            Run in batch mode. Exit with status 0 after processing all",
            "#                     the command files specified with '-x'. Exit with nonzero",
            "#                     status if an error occurs while executing the GDB commands",
            "#                     in the command files.",
            "#",
            "#   -x <file>         The '-x' flag instructs GDB to execute/evaluate the",
            "#                     commands in the given file. In this case, the file we",
            "#                     pass to GDB is a specific '.gdbinit' file.",
            "#",
            "#   -ex \"<command> <arg0> <arg1> .. <argn>\"     The '-ex' flag instructs GDB to",
            "#                                               execute a specific command,",
            "#                                               potentially with some arguments.",
            "#                                               In this case, the command is",
            "#                                               'flash-remote', a user-defined",
            "#                                               command you can find in the",
            "#                                               '.gdbinit' file.",
            "# 13.1.2 variables explained",
            "# --------------------------",
            "# The flags given to GDB contain quite a lot of variables. Let's figure out what",
            "# they mean:",
            "#",
            "#   $(FLASHFILE):  Relative path to the '.gdbinit' file (with respect to the",
            "#                  build folder). This file contains the commands to flash the",
            "#                  microcontroller.",
            "#",
            "#   $(ELF_FILE): Relative path to the '.elf' file (with respect to the build",
            "#                folder). This file is the binary output from the build and",
            "#                contains some extra debug info.",
            "#",
            "#   $(OCD):  Absolute path to OpenOCD or PyOCD, if given as argument on the",
            "#            commandline. Otherwise, 'dashboard.mk' provides a fallback value:",
            "#            either 'openocd' or 'pyocd'.",
            "#",
            "#   $(OPENOCD_PROBEFILE):  Relative path to 'openocd_probe.cfg' file (with",
            "#                          respect to the build folder).",
            "#",
            "#   $(OPENOCD_CHIPFILE):  Relative path to 'openocd_chip.cfg' file (with respect",
            "#                         to the build folder).",
            "#",
            "# 13.1.3 Flash mechanism",
            "# ----------------------",
            "# In short, the 'flash' target starts GDB (Gnu Debugger) and passes it a",
            "# '.gdbinit' file. This file contains the commands to flash the .elf file to the",
            "# microcontroller. The commands are grouped into a user-defined function named",
            "# 'flash-remote'.",
            "# The '-ex' flag instructs GDB to execute this 'flash-remote' function, and",
            "# passes it four parameters:",
            "#     - $(ELF_FILE): where to find the .elf file",
            "#     - $(OCD): where to find OpenOCD",
            "#     - $(OPENOCD_PROBEFILE): where to find the OpenOCD probe file",
            "#     - $(OPENOCD_CHIPFILE): where to find the OpenOCD microcontroller file",
            "#",
            "# With this information, the user-defined 'flash-remote' function is able to",
            "# flash the microcontroller!",
            "#",
            "#",
            "# 13.2 FLASHING BASED ON AVRDUDE",
            "# ------------------------------",
            "# To let $(FLASH) point to 'avrdude', you have to do a string replace-",
            "# ment. This is how 'dashboard.mk' defines the $(FLASH) variable:",
            "#",
            "# FLASH = $(subst avr-\\$,avrdude,$(TOOLPREFIX)\\$)",
            "#",
            "# To use 'avrdude' properly, you need to pass it the 'avrdude.conf' file which",
            "# is inside the 'avrdude' installation folder. To do that, you need the absolute",
            "# path to 'avrdude'. We use a trick for that:",
            "#",
            "# FLASH_PATH = $(if $(filter $(dir .),$(dir $(FLASH))),$(firstword $(wildcard $(patsubst %,%/$(FLASH),$(subst $(PATHSEP), ,$(PATH))))),$(FLASH))",
            "#",
            "# The actual flags to flash the firmware depends on your microcontroller and the",
            "# probe you use for flashing. The following flags would be defined for an",
            "# Arduino UNO to be flashed in the standard way (no external programmer device):",
            "#",
            "# FLASHFLAGS = \\",
            "#     -C \"$(subst /bin/../,/,$(subst /avrdude\\$,,$(FLASH_PATH)\\$)/../)etc/avrdude.conf\" \\",
            "#     -v \\",
            "#     -patmega328p \\",
            "#     -carduino \\",
            "#     -P$(COM) \\",
            "#     -b115200 \\",
            "#     -D \\",
            "#     -Uflash:w:$(ELF_FILE:.elf=.hex):i \\",
            "#",
            "# Let's examine these flags one-by-one:",
            "#",
            "#   -C <file>           We use this flag to pass the avrdude.conf file to",
            "#                       avrdude.",
            "#",
            "#   -v                  Enable verbose output.",
            "#",
            "#   -p <partno>         Specify what type of part (MCU) that is connected to the",
            "#                       programmer.",
            "#",
            "#   -c <programmer-id>  Specify the programmer to be used.",
            "#",
            "#   -P <port>           Identify the device to which the programmer is attached.",
            "#",
            "#   -b <baudrate>       Override the RS-232 connection baud rate specified in",
            "#                       the respective programmer’s entry of the configuration",
            "#                       file.",
            "#",
            "#   -D                  Disable auto erase for flash.",
            "#",
            "#   -U <memtype>:<op>:<filename>:<format>   Perform a memory operation.",
            "#",
            "#                       <memtype>  specifies the memory type to operate on, such",
            "#                                  as 'flash', 'eeprom', 'fuse', 'lock', ...",
            "#",
            "#                       <op>       specifies what operation to perform, such as",
            "#                                  'r' for read, 'w' for write and 'v' for",
            "#                                  verify.",
            "#",
            "#                       <filename> indicates the name of the file to read or",
            "#                                  write.",
            "#",
            "#                       <format>   contains the format of the file to read or",
            "#                                  write, such as 'i' for Intel Hex, 's' for",
            "#                                  Motorola S-record, 'r' for raw binary and 'e'",
            "#                                  for elf and 'a' for autodetect.",
            "#",
            "",
            "",
            ".PHONY: flash",
            "flash: $(BINARIES) print_flash",
            "\t$(FLASH) $(FLASHFLAGS)",
            "",
            "# Some microcontrollers - such as those on Arduino boards - are preloaded with",
            "# a bootloader to enable flashing over a UART connection (virtual COM-port).",
            "# In case the bootloader itself gets corrupted, you need to reflash it with",
            "# a probe. The 'flash_bootloader' target is intended for this operation. The",
            "# variables 'PREPARE_FLASH_BOOTLOADER_FLAGS' and 'FLASH_BOOTLOADER_FLAGS' are",
            "# defined in 'dashboard.mk'.",
            ".PHONY: flash_bootloader",
            "flash_bootloader: print_flash_bootloader",
            "\t$(FLASH) $(PREPARE_FLASH_BOOTLOADER_FLAGS)",
            "\t$(FLASH) $(FLASH_BOOTLOADER_FLAGS)",
            "",
            "# 14. ASCII ART",
            "# ==============",
            "# We provide some nice ascii-art, to be printed for each target.",
            "LPAREN :=(",
            "RPAREN :=)",
            "",
            ".PHONY: print_clean",
            "print_clean:",
            "\t$(info)",
            "\t$(info)",
            "\t$(info # ----------------------------------------------------------)",
            "\t$(info #        __         **************)",
            "\t$(info #      __\\ \\___     * make clean *)",
            "\t$(info #      \\ _ _ _ \\    **************)",
            "\t$(info #       \\_`_`_`_\\ )",
            "\t$(info # )",
            "\t$(info #  Operating system:    $(OS))",
            "\t$(info #  Source folder:       $(abspath $(SOURCE_DIR)))",
            "\t$(info #  Build folder:        $(abspath .))",
            "\t$(info # ----------------------------------------------------------)",
            "",
            ".PHONY: print_build",
            "print_build:",
            "\t$(info)",
            "\t$(info # ----------------------------------------------------------------)",
            "\t$(info #             $(RPAREN)\\     **************)",
            "\t$(info #   $(LPAREN) =_=_=_=<| |    * make build *)",
            "\t$(info #             $(RPAREN)$(LPAREN)     **************)",
            "\t$(info #             \"\" )",
            "\t$(info # )",
            "\t$(info #  Operating system:    $(OS))",
            "\t$(info #  Source folder:       $(abspath $(SOURCE_DIR)))",
            "\t$(info #  Build folder:        $(abspath .))",
            "\t$(info #  Binary output:       $(BINARIES))",
            "\t$(info # ----------------------------------------------------------------)",
            "    ",
            ".PHONY: print_flash",
            "print_flash:",
            "\t$(info)",
            "\t$(info)",
            "\t$(info # ----------------------------------------------------------)",
            "\t$(info #        __         **************)",
            "\t$(info #       / /_        * make flash *)",
            "\t$(info #      /_  /        **************)",
            "\t$(info #        /` )",
            "\t$(info #       ` )",
            "\t$(info #  Operating system:    $(OS))",
            "\t$(info #  Source folder:       $(abspath $(SOURCE_DIR)))",
            "\t$(info #  Build folder:        $(abspath .))",
            "\t$(info # ----------------------------------------------------------)",
            "",
            ".PHONY: print_flash_bootloader",
            "print_flash_bootloader:",
            "\t$(info)",
            "\t$(info)",
            "\t$(info # ----------------------------------------------------------)",
            "\t$(info #        /\\         *************************)",
            "\t$(info #       $(LPAREN)  $(RPAREN)        * make flash_bootloader *)",
            "\t$(info #       $(LPAREN)  $(RPAREN)        *************************)",
            "\t$(info #      /|/\\|\\ )",
            "\t$(info #     /_||||_\\ )",
            "\t$(info #  Operating system:    $(OS))",
            "\t$(info #  Source folder:       $(abspath $(SOURCE_DIR)))",
            "\t$(info #  Build folder:        $(abspath .))",
            "\t$(info # ----------------------------------------------------------)",
            "",
            "# ADDENDUM 1. MIT LICENSE",
            "# ------------------------",
            "# Copyright 2020 Johan Cockx",
            "#",
            "# Permission is hereby granted, free of charge, to any person obtaining a copy",
            "# of this software and associated documentation files (the \"Software\"), to deal",
            "# in the Software without restriction, including without limitation the rights",
            "# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell",
            "# copies of the Software, and to permit persons to whom the Software is furn-",
            "# ished to do so, subject to the following conditions:",
            "#",
            "# The above copyright notice and this permission notice shall be included in all",
            "# copies or substantial portions of the Software.",
            "#",
            "# THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR",
            "# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,",
            "# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE",
            "# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER",
            "# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,",
            "# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE",
            "# SOFTWARE.",
            "#",
            "# ADDENDUM 2. WHY WE USE ABSOLUTE PATHS FOR THE COMPILERS",
            "# --------------------------------------------------------",
            "# Please note that we use absolute(!) paths to refer to the compilers. Normally,",
            "# it would be sufficient to add the absolute paths just once to the PATH",
            "# environment variable and call the compiler by its name from then forward.",
            "# Unfortunately, on Windows this causes the error 'CreateProcess: No such file",
            "# or directory'. Apparently this is because of a missing registry key, one that",
            "# gets written to the registry when you install the compiler through the",
            "# official installer. That registry key then points to the location of the",
            "# installation. We strongly dislike this approach, because:",
            "#",
            "#       - We don't like messing with your registry.",
            "#",
            "#       - The location of your compiler must be stored in two places: the",
            "#         registry and the PATH environment variable. If you move the compiler,",
            "#         you should change both of them. Most often, people forget to adapt the",
            "#         registry key and disaster strikes...",
            "#",
            "# Therefore, we see no other option than calling the compilers by their absolute",
            "# path. To do that practically, we glue the TOOLCHAIN variable to the compiler",
            "# name.",
        });
        return makeFile;
    }

    private AbstractFile createFileTreeFile()
    {
        TextFile filesMkFile = new TextFile("config/filetree.mk");
        filesMkFile.separateSectionWithEmptyLine(true);
        filesMkFile.createSections(new String[]
                { MAKEFILE_FILE_COMMENT_SECTION_NAME });

        filesMkFile.addLines(MAKEFILE_FILE_COMMENT_SECTION_NAME,
                       new String[] {
           "################################################################################",
           "#                              MAKEFILE INCLUDES                               #",
           "################################################################################",
           "# COPYRIGHT (c) 2020 Embeetle",
           "# This software component is licensed by Embeetle under the MIT license. Please",
           "# consult the license text at the bottom of this file.",
           "#",
           "# Compatible with Embeetle makefile interface version 2",
           "# To avoid a warning when opening this project in Embeetle, always keep the",
           "# above line somewhere in this file.",
           "#",
           "# This file should be included in the makefile. It lists all source files (c,",
           "# c++, asm..) that take part in the compilation.",
           "#",
           "# WARNING: This file gets automatically generated. Your manual changes will be",
           "# lost!",
           "#",
           "# NOTE: Take a look at the Embeetle filetree. All files with a green checkmark",
           "#       are the ones ending up here in this file. Embeetle attempts to list",
           "#       automatically the right set of files, starting from 'main.c'. However,",
           "#       you can manually overrule the beetle's decisions by clicking on the",
           "#       file's checkmark.",
           "",
           "",
           "# INCLUDED CFILES:",
           "CFILES =",
           "",
           "# INCLUDED CPPFILES:",
           "CPPFILES =",
           "",
           "# INCLUDED SFILES:",
           "SFILES =",
           "",
           "# INCLUDED HDIRS:",
           "HDIRS =",
           "        ",
           "# MIT LICENSE",
           "# -----------",
           "# COPYRIGHT (c) 2020 Embeetle",
           "#",
           "# Permission is hereby granted, free of charge, to any person obtaining a copy",
           "# of this software and associated documentation files (the \"Software\"), to deal",
           "# in the Software without restriction, including without limitation the rights",
           "# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell",
           "# copies of the Software, and to permit persons to whom the Software is furn-",
           "# ished to do so, subject to the following conditions:",
           "#",
           "# The above copyright notice and this permission notice shall be included in all",
           "# copies or substantial portions of the Software.",
           "#",
           "# THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR",
           "# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,",
           "# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE",
           "# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER",
           "# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,",
           "# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE",
           "# SOFTWARE.",
        });
        return filesMkFile;
    }

    private AbstractFile createDashboardFile()
    {
        TextFile variablesMkFile = new TextFile("config/dashboard.mk");
        variablesMkFile.separateSectionWithEmptyLine(true);
        variablesMkFile.createSections(new String[]
                { MAKEFILE_FILE_COMMENT_SECTION_NAME });

        variablesMkFile.addLines(MAKEFILE_FILE_COMMENT_SECTION_NAME,
                       new String[] {
           "################################################################################",
           "#                                 DASHBOARD.MK                                 #",
           "################################################################################",
           "# COPYRIGHT (c) 2020 Embeetle",
           "# This software component is licensed by Embeetle under the MIT license. Please",
           "# consult the license text at the bottom of this file.",
           "#",
           "# Compatible with Embeetle makefile interface version 2",
           "#",
           "# This file is intended to be included in the makefile. It contains all",
           "# variables that depend on dashboard settings in Embeetle.",
           "#",
           "# WARNING: This file gets automatically generated. Your manual changes will be",
           "#          merged with changes from the dashboard.",
           "#",
           "# NOTE: We suggest to include this file in your makefile like so:",
           "#",
           "#     MAKEFILE := $(lastword $(MAKEFILE_LIST))",
           "#     MAKEFILE_DIR := $(dir $(MAKEFILE))",
           "#     include $(MAKEFILE_DIR)dashboard.mk",
           "#",
           "",
           "# 1.TOOLS",
           "# =======",
           "# When invoking the makefile, Embeetle passes absolute paths to the toolchain",
           "# and the flash/debug server (OpenOCD or PyOCD) on the commandline. Example:",
           "#",
           "#   > \"TOOLPREFIX=C:/my_tools/gnu_arm_toolchain_9.2.1/bin/arm-none-eabi-\"",
           "#   > \"OCD=C:/my_tools/openocd_0.10.0_dev01138_32b/bin/openocd.exe\"",
           "#",
           "# If you ever invoke the makefile without these commandline-arguments,",
           "# you need a fallback mechanism. Therefore, we provide a default value",
           "# for these variables here:",
           "#",
           "TOOLPREFIX = ",
           "OCD = openocd",
           "# You might wonder: why bother with a default value? Embeetle could simply",
           "# insert the actual paths (as selected in the dashboard) here.",
           "# However, that would make this dashboard.mk file location dependent: the",
           "# location of the tool would be hardcoded. This is a problem if you access",
           "# this project from two computers where the same tool is stored in different",
           "# locations. Note that dashboard.mk is a config file, and therefore treated",
           "# as a source file (\"config as code\" principle).",
           "",
           "# 2. PROJECT LAYOUT",
           "# =================",
           "# The PROJECT LAYOUT section in the dashboard points to all important config",
           "# file locations (eg. linkerscript, openocd config files, ...). If you change",
           "# any of those locations in the dashboard, Embeetle changes the variables below",
           "# accordingly.",
           "#",
           "#   NOTES:",
           "#       - These paths are all relative to the build directory.",
           "#       - Unused variables can get value 'None'.",
           "#       - Locations of 'dashboard.mk' and 'filetree.mk' are not",
           "#         defined here. That's because they're always located in",
           "#         the same folder with the makefile.",
           "#",
        });

        String projectName = ctx.cfg().getString(Configuration.PROJECT_FILE_CFG);

        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "ELF_FILE = " + projectName + ".elf");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "SOURCE_DIR = ../source/");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "LINKERSCRIPT = ../config/linkerscript.ld");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "GDB_FLASHFILE = ../config/.gdbinit");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "OPENOCD_PROBEFILE = ../config/openocd_probe.cfg");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "OPENOCD_CHIPFILE = ../config/openocd_chip.cfg");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "# 3. COMPILATION FLAGS");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "# ====================");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "# CPU specific flags for C++, C and assembly compilation and linking.");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "TARGET_COMMONFLAGS = ");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "");

        String cflags = listVariables.get("CFLAGS");
        if(null == cflags)
        {
            cflags = "";
        }
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "# CPU specific C compilation flags");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "TARGET_CFLAGS = " + cflags);
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "");

        String cppflags = listVariables.get("CPFLAGS");
        if(null == cppflags)
        {
            cppflags = "";
        }
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "# CPU specific C++ compilation flags");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "TARGET_CPPFLAGS = " + cppflags);
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "");

        String asflags = listVariables.get("ASFLAGS");
        if(null == asflags)
        {
            asflags = "";
        }
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "# CPU specific assembler flags");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "TARGET_SFLAGS = " + asflags);
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "");

        String ldflags = listVariables.get("LFLAGS");
        if(null == ldflags)
        {
            ldflags = "";
        }
        // remove the initial refernce to the linker file (the linker file has been renamed)
        String[] parts = ldflags.split(" ");
        StringBuffer linkerFlags = new StringBuffer();
        for(int i = 0; i < parts.length; i++)
        {
            String curPart = parts[i];
            if(true == curPart.startsWith("-T"))
            {
                // do not add this
            }
            else
            {
                linkerFlags.append(curPart);
                linkerFlags.append(" ");
            }
        }

        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "# CPU specific linker flags");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "TARGET_LDFLAGS = " + linkerFlags.toString() + "\\");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "                 -T $(LINKERSCRIPT) \\");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "                 -L $(dir $(LINKERSCRIPT)) \\");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "                 -L $(MAKEFILE_DIR) \\");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "");
        variablesMkFile.addLine(MAKEFILE_FILE_COMMENT_SECTION_NAME, "");

        variablesMkFile.addLines(MAKEFILE_FILE_COMMENT_SECTION_NAME,
                   new String[] {
           "# 4. FLASH SOFTWARE",
           "# =================",
           "# $(FLASH) is the front-end software used to flash the firmware to the",
           "# microcontroller (usually GDB) and $(FLASHFLAGS) are the flags given to this",
           "# software (usually these flags contain a reference to the .gdbinit file where",
           "# instructions are found to launch OpenOCD).",
           "# For a complete explanation, read the comments in the makefile at the 'flash'",
           "# target.",
           "FLASH = $(GDB)",
           "FLASHFLAGS = \\",
           "    -n \\",
           "    -batch \\",
           "    -x $(GDB_FLASHFILE) \\",
           "    -ex \"flash-remote $(ELF_FILE) \"$(OCD)\" $(OPENOCD_PROBEFILE) $(OPENOCD_CHIPFILE)\" \\",
           "",
           "",
           "# MIT LICENSE",
           "# -----------",
           "# COPYRIGHT (c) 2020 Embeetle",
           "#",
           "# Permission is hereby granted, free of charge, to any person obtaining a copy",
           "# of this software and associated documentation files (the \"Software\"), to deal",
           "# in the Software without restriction, including without limitation the rights",
           "# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell",
           "# copies of the Software, and to permit persons to whom the Software is furn-",
           "# ished to do so, subject to the following conditions:",
           "#",
           "# The above copyright notice and this permission notice shall be included in all",
           "# copies or substantial portions of the Software.",
           "#",
           "# THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR",
           "# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,",
           "# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE",
           "# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER",
           "# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,",
           "# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE",
           "# SOFTWARE.",
        });
        return variablesMkFile;
    }

    @Override
    public FileGroup createBuildFor(FileGroup files)
    {
        if(null == files)
        {
            return null;
        }
        // get hardware configuration
        // add the stuff required by the hardware (targets, variables, files)
        Environment e = ctx.getEnvironment();
        if(null == e)
        {
            ctx.addError(this, "No Environment available !");
            return null;
        }
        if(false == configureBuild(e, requiredEnvironmentVariables))
        {
            ctx.addError(this, "Could not get configuration from environment !");
            return null;
        }

        log.trace("adding {} files.", buildFiles.numEntries());
        files.addAll(buildFiles);

        FileGroup out = new FileGroup();
        log.trace("restructure Files");
        Iterator<String> names = files.getFileIterator();
        while(names.hasNext())
        {
            String name = names.next();
            AbstractFile f = files.getFileWithName(name);
            log.trace("looking at {}", name);

            if(true == name.endsWith(".ld"))
            {
                // put linker script into the config folder
                f.setFileName("config/linkerscript.ld");
            }
            else if( (true == name.endsWith(".c"))
                    || (true == name.endsWith(".h"))
                    || (true == name.endsWith(".s")) )
            {
                // all sources go into the source folder
                f.setFileName("source/" + name);
            }
            log.trace("adding as {}", f.getFileName());
            out.add(f);
        }
        log.trace("restructure done.");

        // create the Makefile
        out.add(createMakeFile());

        // create the fileTree file
        out.add(createFileTreeFile());

        // create the dash board file
        out.add(createDashboardFile());

        // add empty build folder
        out.add(new EmptyFolder("build/"));
        return out;
    }

}
