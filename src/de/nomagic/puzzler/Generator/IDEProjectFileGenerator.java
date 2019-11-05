package de.nomagic.puzzler.Generator;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.puzzler.Context;
import de.nomagic.puzzler.FileGroup.FileGroup;
import de.nomagic.puzzler.FileGroup.TextFile;
import de.nomagic.puzzler.configuration.Configuration;

public class IDEProjectFileGenerator
{
    private static final String CLASS_NAME = "IDEProjectFileGenerator";
    private static final Logger LOG = LoggerFactory.getLogger(CLASS_NAME);
    private static final String SECTION_NAME = "data";


    private static TextFile createEclipse_dot_cproject(String ProjectName)
    {
        TextFile dotProject = new TextFile(".cproject");
        dotProject.createSection(SECTION_NAME);
        dotProject.addLine(SECTION_NAME, "    <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        dotProject.addLine(SECTION_NAME, "    <?fileVersion 4.0.0?><cproject storage_type_id=\"org.eclipse.cdt.core.XmlProjectDescriptionStorage\">");
        dotProject.addLine(SECTION_NAME, "        <storageModule moduleId=\"org.eclipse.cdt.core.settings\">");
        dotProject.addLine(SECTION_NAME, "            <cconfiguration id=\"ilg.gnuarmeclipse.managedbuild.cross.config.elf.debug.276895468\">");
        dotProject.addLine(SECTION_NAME, "                <storageModule buildSystemId=\"org.eclipse.cdt.managedbuilder.core.configurationDataProvider\" id=\"ilg.gnuarmeclipse.managedbuild.cross.config.elf.debug.276895468\" moduleId=\"org.eclipse.cdt.core.settings\" name=\"Debug\">");
        dotProject.addLine(SECTION_NAME, "                    <externalSettings/>");
        dotProject.addLine(SECTION_NAME, "                    <extensions>");
        dotProject.addLine(SECTION_NAME, "                        <extension id=\"org.eclipse.cdt.core.ELF\" point=\"org.eclipse.cdt.core.BinaryParser\"/>");
        dotProject.addLine(SECTION_NAME, "                        <extension id=\"org.eclipse.cdt.core.GASErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>");
        dotProject.addLine(SECTION_NAME, "                        <extension id=\"org.eclipse.cdt.core.GmakeErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>");
        dotProject.addLine(SECTION_NAME, "                        <extension id=\"org.eclipse.cdt.core.GLDErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>");
        dotProject.addLine(SECTION_NAME, "                        <extension id=\"org.eclipse.cdt.core.CWDLocator\" point=\"org.eclipse.cdt.core.ErrorParser\"/>");
        dotProject.addLine(SECTION_NAME, "                        <extension id=\"org.eclipse.cdt.core.GCCErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>");
        dotProject.addLine(SECTION_NAME, "                    </extensions>");
        dotProject.addLine(SECTION_NAME, "                </storageModule>");
        dotProject.addLine(SECTION_NAME, "                <storageModule moduleId=\"cdtBuildSystem\" version=\"4.0.0\">");
        dotProject.addLine(SECTION_NAME, "                    <configuration artifactName=\"${ProjName}\" buildArtefactType=\"org.eclipse.cdt.build.core.buildArtefactType.exe\" buildProperties=\"org.eclipse.cdt.build.core.buildArtefactType=org.eclipse.cdt.build.core.buildArtefactType.exe,org.eclipse.cdt.build.core.buildType=org.eclipse.cdt.build.core.buildType.debug\" cleanCommand=\"${cross_rm} -rf\" description=\"\" id=\"ilg.gnuarmeclipse.managedbuild.cross.config.elf.debug.276895468\" name=\"Debug\" optionalBuildProperties=\"org.eclipse.cdt.docker.launcher.containerbuild.property.selectedvolumes=,org.eclipse.cdt.docker.launcher.containerbuild.property.volumes=\" parent=\"ilg.gnuarmeclipse.managedbuild.cross.config.elf.debug\">");
        dotProject.addLine(SECTION_NAME, "                        <folderInfo id=\"ilg.gnuarmeclipse.managedbuild.cross.config.elf.debug.276895468.\" name=\"/\" resourcePath=\"\">");
        dotProject.addLine(SECTION_NAME, "                            <toolChain id=\"ilg.gnuarmeclipse.managedbuild.cross.toolchain.elf.debug.538289494\" name=\"ARM Cross GCC\" nonInternalBuilderId=\"ilg.gnuarmeclipse.managedbuild.cross.builder\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.toolchain.elf.debug\">"); // TODO non ARM environments?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.addtools.createflash.1480751608\" name=\"Create flash image\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.addtools.createflash\" useByScannerDiscovery=\"false\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.addtools.createlisting.623529954\" name=\"Create extended listing\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.addtools.createlisting\" useByScannerDiscovery=\"false\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.addtools.printsize.1722990172\" name=\"Print size\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.addtools.printsize\" useByScannerDiscovery=\"false\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.level.2063150385\" name=\"Optimization Level\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.level\" useByScannerDiscovery=\"true\" value=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.level.debug\" valueType=\"enumerated\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.messagelength.628512389\" name=\"Message length (-fmessage-length=0)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.messagelength\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.signedchar.13534463\" name=\"'char' is signed (-fsigned-char)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.signedchar\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.functionsections.1458896970\" name=\"Function sections (-ffunction-sections)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.functionsections\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.datasections.774696879\" name=\"Data sections (-fdata-sections)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.datasections\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.debugging.level.1406118873\" name=\"Debug level\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.debugging.level\" useByScannerDiscovery=\"true\" value=\"ilg.gnuarmeclipse.managedbuild.cross.option.debugging.level.max\" valueType=\"enumerated\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.debugging.format.2123671034\" name=\"Debug format\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.debugging.format\" useByScannerDiscovery=\"true\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.family.1181901580\" name=\"ARM family (-mcpu)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.family\" useByScannerDiscovery=\"false\" value=\"ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.mcpu.cortex-m23\" valueType=\"enumerated\"/>"); // TODO config target type
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.allwarn.550233562\" name=\"Enable all common warnings (-Wall)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.allwarn\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.extrawarn.151047849\" name=\"Enable extra warnings (-Wextra)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.extrawarn\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.unused.36613988\" name=\"Warn on various unused elements (-Wunused)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.unused\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.uninitialized.764096842\" name=\"Warn on uninitialized variables (-Wuninitialised)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.uninitialized\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.missingdeclaration.1028114372\" name=\"Warn on undeclared global function (-Wmissing-declaration)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.missingdeclaration\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
//        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.conversion.2132706025\" name=\"Warn on implicit conversions (-Wconversion)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.conversion\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.pointerarith.2056548887\" name=\"Warn if pointer arithmetic (-Wpointer-arith)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.pointerarith\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.padded.61961807\" name=\"Warn if padding is included (-Wpadded)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.padded\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.shadow.245320444\" name=\"Warn if shadowed variable (-Wshadow)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.shadow\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.logicalop.83041864\" name=\"Warn if suspicious logical ops (-Wlogical-op)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.logicalop\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.agreggatereturn.1171647734\" name=\"Warn if struct is returned (-Wagreggate-return)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.agreggatereturn\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.floatequal.1971650635\" name=\"Warn if floats are compared as equal (-Wfloat-equal)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.floatequal\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.freestanding.497878078\" name=\"Assume freestanding environment (-ffreestanding)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.freestanding\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.nomoveloopinvariants.1406316633\" name=\"Disable loop invariant move (-fno-move-loop-invariants)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.nomoveloopinvariants\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.lto.1594010045\" name=\"Link-time optimizer (-flto)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.lto\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.toolchain.name.373636846\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.toolchain.name\" useByScannerDiscovery=\"false\" value=\"GNU MCU Eclipse ARM Embedded GCC\" valueType=\"string\"/>");  // TODO cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.architecture.1022441689\" name=\"Architecture\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.architecture\" useByScannerDiscovery=\"false\" value=\"ilg.gnuarmeclipse.managedbuild.cross.option.architecture.arm\" valueType=\"enumerated\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.instructionset.1210629546\" name=\"Instruction set\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.instructionset\" useByScannerDiscovery=\"false\" value=\"ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.instructionset.thumb\" valueType=\"enumerated\"/>");  // TODO cfg
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.prefix.1148278393\" name=\"Prefix\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.prefix\" useByScannerDiscovery=\"false\" value=\"arm-none-eabi-\" valueType=\"string\"/>");  // TODO cfg
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.c.44725204\" name=\"C compiler\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.c\" useByScannerDiscovery=\"false\" value=\"gcc\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.cpp.1955087962\" name=\"C++ compiler\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.cpp\" useByScannerDiscovery=\"false\" value=\"g++\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.ar.841918518\" name=\"Archiver\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.ar\" useByScannerDiscovery=\"false\" value=\"ar\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.objcopy.1017457475\" name=\"Hex/Bin converter\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.objcopy\" useByScannerDiscovery=\"false\" value=\"objcopy\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.objdump.805572833\" name=\"Listing generator\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.objdump\" useByScannerDiscovery=\"false\" value=\"objdump\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.size.1805761900\" name=\"Size command\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.size\" useByScannerDiscovery=\"false\" value=\"size\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.make.320315094\" name=\"Build command\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.make\" useByScannerDiscovery=\"false\" value=\"make\" valueType=\"string\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.rm.449004828\" name=\"Remove command\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.rm\" useByScannerDiscovery=\"false\" value=\"rm\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.toolchain.id.1324318687\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.toolchain.id\" useByScannerDiscovery=\"false\" value=\"962691777\" valueType=\"string\"/>");
        dotProject.addLine(SECTION_NAME, "                                <targetPlatform archList=\"all\" binaryParser=\"org.eclipse.cdt.core.ELF\" id=\"ilg.gnuarmeclipse.managedbuild.cross.targetPlatform.1049164067\" isAbstract=\"false\" osList=\"all\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.targetPlatform\"/>");
        dotProject.addLine(SECTION_NAME, "                                <builder buildPath=\"${workspace_loc:/s1ja-test}/Debug\" id=\"ilg.gnuarmeclipse.managedbuild.cross.builder.698627566\" keepEnvironmentInBuildfile=\"false\" name=\"Gnu Make Builder\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.builder\"/>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.assembler.723346620\" name=\"GNU ARM Cross Assembler\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.assembler\">");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.assembler.usepreprocessor.186136743\" name=\"Use preprocessor\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.assembler.usepreprocessor\" useByScannerDiscovery=\"false\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.assembler.include.paths.1807065117\" name=\"Include paths (-I)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.assembler.include.paths\" useByScannerDiscovery=\"true\" valueType=\"includePath\">");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;${workspace_loc:/${ProjName}/src}&quot;\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.assembler.defs.792918098\" name=\"Defined symbols (-D)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.assembler.defs\" useByScannerDiscovery=\"true\" valueType=\"definedSymbols\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"DEBUG\"/>");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"TRACE\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <inputType id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.assembler.input.403258619\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.assembler.input\"/>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.compiler.1135653666\" name=\"GNU ARM Cross C Compiler\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.compiler\">");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.include.paths.2084441932\" name=\"Include paths (-I)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.include.paths\" useByScannerDiscovery=\"true\" valueType=\"includePath\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;${workspace_loc:/${ProjName}/src}&quot;\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.warning.missingprototypes.1968223803\" name=\"Warn if a global function has no prototype (-Wmissing-prototypes)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.warning.missingprototypes\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.warning.strictprototypes.1160088716\" name=\"Warn if a function has no arg type (-Wstrict-prototypes)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.warning.strictprototypes\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.warning.badfunctioncast.1961767402\" name=\"Warn if wrong cast  (-Wbad-function-cast)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.warning.badfunctioncast\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.defs.1864263059\" name=\"Defined symbols (-D)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.defs\" useByScannerDiscovery=\"true\" valueType=\"definedSymbols\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"DEBUG\"/>");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"TRACE\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.std.1768170101\" name=\"Language standard\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.std\" useByScannerDiscovery=\"true\" value=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.std.gnu99\" valueType=\"enumerated\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.verbose.336075238\" name=\"Verbose (-v)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.verbose\" useByScannerDiscovery=\"false\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.savetemps.993699190\" name=\"Save temporary files (--save-temps Use with caution!)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.savetemps\" useByScannerDiscovery=\"false\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.asmlisting.1447803428\" name=\"Generate assembler listing (-Wa,-adhlns=&quot;$@.lst&quot;)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.asmlisting\" useByScannerDiscovery=\"false\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.otherwarnings.1005868901\" name=\"Other warning flags\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.otherwarnings\" useByScannerDiscovery=\"true\" value=\"-Wno-padded\" valueType=\"string\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <inputType id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.compiler.input.51250587\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.compiler.input\"/>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.compiler.2032695737\" name=\"GNU ARM Cross C++ Compiler\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.compiler\">");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"true\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.compiler.include.paths.673706541\" name=\"Include paths (-I)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.compiler.include.paths\" useByScannerDiscovery=\"true\" valueType=\"includePath\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.compiler.defs.459452720\" name=\"Defined symbols (-D)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.compiler.defs\" useByScannerDiscovery=\"true\" valueType=\"definedSymbols\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"DEBUG\"/>");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"TRACE\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <inputType id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.compiler.input.448048954\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.compiler.input\"/>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.linker.1330741371\" name=\"GNU ARM Cross C Linker\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.linker\">");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.gcsections.1988248746\" name=\"Remove unused sections (-Xlinker --gc-sections)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.gcsections\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.paths.748268271\" name=\"Library search path (-L)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.paths\" valueType=\"libPaths\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../ldscripts&quot;\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.scriptfile.281688485\" name=\"Script files (-T)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.scriptfile\" valueType=\"stringList\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"mem.ld\"/>");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"libs.ld\"/>");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"sections.ld\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.nostart.1620842994\" name=\"Do not use standard start files (-nostartfiles)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.nostart\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.usenewlibnano.1923913676\" name=\"Use newlib-nano (--specs=nano.specs)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.usenewlibnano\" value=\"true\" valueType=\"boolean\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                    <inputType id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.linker.input.1837592375\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.linker.input\">");
        dotProject.addLine(SECTION_NAME, "                                        <additionalInput kind=\"additionalinputdependency\" paths=\"$(USER_OBJS)\"/>");
        dotProject.addLine(SECTION_NAME, "                                        <additionalInput kind=\"additionalinput\" paths=\"$(LIBS)\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </inputType>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.linker.1059031955\" name=\"GNU ARM Cross C++ Linker\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.linker\">");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.gcsections.1143696377\" name=\"Remove unused sections (-Xlinker --gc-sections)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.gcsections\" useByScannerDiscovery=\"false\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.paths.42695402\" name=\"Library search path (-L)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.paths\" useByScannerDiscovery=\"false\" valueType=\"libPaths\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;${workspace_loc:/${ProjName}}&quot;\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.scriptfile.1629267215\" name=\"Script files (-T)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.scriptfile\" useByScannerDiscovery=\"false\" valueType=\"stringList\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;${workspace_loc:/${ProjName}/s124.ld}&quot;\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.nostart.995745327\" name=\"Do not use standard start files (-nostartfiles)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.nostart\" useByScannerDiscovery=\"false\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.usenewlibnano.877987667\" name=\"Use newlib-nano (--specs=nano.specs)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.usenewlibnano\" useByScannerDiscovery=\"false\" value=\"true\" valueType=\"boolean\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.libs.316721007\" name=\"Libraries (-l)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.libs\" useByScannerDiscovery=\"false\" valueType=\"libs\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"DSP_Lib\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <inputType id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.linker.input.1855119539\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.linker.input\">");
        dotProject.addLine(SECTION_NAME, "                                        <additionalInput kind=\"additionalinputdependency\" paths=\"$(USER_OBJS)\"/>");
        dotProject.addLine(SECTION_NAME, "                                        <additionalInput kind=\"additionalinput\" paths=\"$(LIBS)\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </inputType>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.archiver.1746253011\" name=\"GNU ARM Cross Archiver\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.archiver\"/>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.createflash.2111475834\" name=\"GNU ARM Cross Create Flash Image\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.createflash\"/>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.createlisting.697946427\" name=\"GNU ARM Cross Create Listing\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.createlisting\">");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.source.1690950385\" name=\"Display source (--source|-S)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.source\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.allheaders.1413324607\" name=\"Display all headers (--all-headers|-x)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.allheaders\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.demangle.1415701228\" name=\"Demangle names (--demangle|-C)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.demangle\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.linenumbers.1218864837\" name=\"Display line numbers (--line-numbers|-l)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.linenumbers\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.wide.778859963\" name=\"Wide lines (--wide|-w)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.wide\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.printsize.937897463\" name=\"GNU ARM Cross Print Size\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.printsize\">");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.printsize.format.1215238063\" name=\"Size format\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.printsize.format\" useByScannerDiscovery=\"false\"/>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                            </toolChain>");
        dotProject.addLine(SECTION_NAME, "                        </folderInfo>");
        dotProject.addLine(SECTION_NAME, "                        <sourceEntries>");
        dotProject.addLine(SECTION_NAME, "                            <entry flags=\"VALUE_WORKSPACE_PATH|RESOLVED\" kind=\"sourcePath\" name=\"\"/>");
        dotProject.addLine(SECTION_NAME, "                        </sourceEntries>");
        dotProject.addLine(SECTION_NAME, "                    </configuration>");
        dotProject.addLine(SECTION_NAME, "                </storageModule>");
        dotProject.addLine(SECTION_NAME, "                <storageModule moduleId=\"org.eclipse.cdt.core.externalSettings\"/>");
        dotProject.addLine(SECTION_NAME, "                <storageModule moduleId=\"ilg.gnumcueclipse.managedbuild.packs\"/>");
        dotProject.addLine(SECTION_NAME, "            </cconfiguration>");
        dotProject.addLine(SECTION_NAME, "            <cconfiguration id=\"ilg.gnuarmeclipse.managedbuild.cross.config.elf.release.426600561\">");
        dotProject.addLine(SECTION_NAME, "                <storageModule buildSystemId=\"org.eclipse.cdt.managedbuilder.core.configurationDataProvider\" id=\"ilg.gnuarmeclipse.managedbuild.cross.config.elf.release.426600561\" moduleId=\"org.eclipse.cdt.core.settings\" name=\"Release\">");
        dotProject.addLine(SECTION_NAME, "                    <externalSettings/>");
        dotProject.addLine(SECTION_NAME, "                    <extensions>");
        dotProject.addLine(SECTION_NAME, "                        <extension id=\"org.eclipse.cdt.core.ELF\" point=\"org.eclipse.cdt.core.BinaryParser\"/>");
        dotProject.addLine(SECTION_NAME, "                        <extension id=\"org.eclipse.cdt.core.GASErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>");
        dotProject.addLine(SECTION_NAME, "                        <extension id=\"org.eclipse.cdt.core.GmakeErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>");
        dotProject.addLine(SECTION_NAME, "                        <extension id=\"org.eclipse.cdt.core.GLDErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>");
        dotProject.addLine(SECTION_NAME, "                        <extension id=\"org.eclipse.cdt.core.CWDLocator\" point=\"org.eclipse.cdt.core.ErrorParser\"/>");
        dotProject.addLine(SECTION_NAME, "                        <extension id=\"org.eclipse.cdt.core.GCCErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>");
        dotProject.addLine(SECTION_NAME, "                    </extensions>");
        dotProject.addLine(SECTION_NAME, "                </storageModule>");
        dotProject.addLine(SECTION_NAME, "                <storageModule moduleId=\"cdtBuildSystem\" version=\"4.0.0\">");
        dotProject.addLine(SECTION_NAME, "                    <configuration artifactName=\"${ProjName}\" buildArtefactType=\"org.eclipse.cdt.build.core.buildArtefactType.exe\" buildProperties=\"org.eclipse.cdt.build.core.buildArtefactType=org.eclipse.cdt.build.core.buildArtefactType.exe,org.eclipse.cdt.build.core.buildType=org.eclipse.cdt.build.core.buildType.release\" cleanCommand=\"${cross_rm} -rf\" description=\"\" id=\"ilg.gnuarmeclipse.managedbuild.cross.config.elf.release.426600561\" name=\"Release\" optionalBuildProperties=\"\" parent=\"ilg.gnuarmeclipse.managedbuild.cross.config.elf.release\">");
        dotProject.addLine(SECTION_NAME, "                        <folderInfo id=\"ilg.gnuarmeclipse.managedbuild.cross.config.elf.release.426600561.\" name=\"/\" resourcePath=\"\">");
        dotProject.addLine(SECTION_NAME, "                            <toolChain id=\"ilg.gnuarmeclipse.managedbuild.cross.toolchain.elf.release.1047304445\" name=\"ARM Cross GCC\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.toolchain.elf.release\">");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.addtools.createflash.1538272455\" name=\"Create flash image\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.addtools.createflash\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.addtools.createlisting.1029571640\" name=\"Create extended listing\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.addtools.createlisting\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.addtools.printsize.1209582686\" name=\"Print size\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.addtools.printsize\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.level.1435110685\" name=\"Optimization Level\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.level\" value=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.level.size\" valueType=\"enumerated\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.messagelength.1184732156\" name=\"Message length (-fmessage-length=0)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.messagelength\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.signedchar.1059874235\" name=\"'char' is signed (-fsigned-char)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.signedchar\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.functionsections.1484216489\" name=\"Function sections (-ffunction-sections)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.functionsections\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.datasections.1923244541\" name=\"Data sections (-fdata-sections)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.datasections\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.debugging.level.587700763\" name=\"Debug level\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.debugging.level\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.debugging.format.853080918\" name=\"Debug format\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.debugging.format\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.family.1142670424\" name=\"ARM family (-mcpu)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.family\" value=\"ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.mcpu.cortex-m23\" valueType=\"enumerated\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.allwarn.1747969613\" name=\"Enable all common warnings (-Wall)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.allwarn\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.extrawarn.1297134677\" name=\"Enable extra warnings (-Wextra)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.extrawarn\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.unused.1475212776\" name=\"Warn on various unused elements (-Wunused)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.unused\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.uninitialized.1426181514\" name=\"Warn on uninitialized variables (-Wuninitialised)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.uninitialized\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.missingdeclaration.1893874763\" name=\"Warn on undeclared global function (-Wmissing-declaration)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.missingdeclaration\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.conversion.1482548336\" name=\"Warn on implicit conversions (-Wconversion)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.conversion\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.pointerarith.1819451442\" name=\"Warn if pointer arithmetic (-Wpointer-arith)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.pointerarith\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.padded.71107898\" name=\"Warn if padding is included (-Wpadded)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.padded\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.shadow.660468974\" name=\"Warn if shadowed variable (-Wshadow)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.shadow\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.logicalop.1358711764\" name=\"Warn if suspicious logical ops (-Wlogical-op)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.logicalop\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.agreggatereturn.1892078997\" name=\"Warn if struct is returned (-Wagreggate-return)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.agreggatereturn\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.floatequal.838190435\" name=\"Warn if floats are compared as equal (-Wfloat-equal)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.warnings.floatequal\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.freestanding.638095811\" name=\"Assume freestanding environment (-ffreestanding)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.freestanding\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.lto.784849854\" name=\"Link-time optimizer (-flto)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.optimization.lto\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.toolchain.name.380870177\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.toolchain.name\" value=\"GNU MCU Eclipse ARM Embedded GCC\" valueType=\"string\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.architecture.1737904247\" name=\"Architecture\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.architecture\" value=\"ilg.gnuarmeclipse.managedbuild.cross.option.architecture.arm\" valueType=\"enumerated\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.instructionset.1202517049\" name=\"Instruction set\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.instructionset\" value=\"ilg.gnuarmeclipse.managedbuild.cross.option.arm.target.instructionset.thumb\" valueType=\"enumerated\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.prefix.760756932\" name=\"Prefix\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.prefix\" value=\"arm-none-eabi-\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.c.87126645\" name=\"C compiler\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.c\" value=\"gcc\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.cpp.1414665844\" name=\"C++ compiler\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.cpp\" value=\"g++\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.ar.604760204\" name=\"Archiver\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.ar\" value=\"ar\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.objcopy.365006848\" name=\"Hex/Bin converter\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.objcopy\" value=\"objcopy\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.objdump.679580587\" name=\"Listing generator\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.objdump\" value=\"objdump\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.size.1478896072\" name=\"Size command\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.size\" value=\"size\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.make.1779470758\" name=\"Build command\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.make\" value=\"make\" valueType=\"string\"/>");
        dotProject.addLine(SECTION_NAME, "                                <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.rm.1813945263\" name=\"Remove command\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.command.rm\" value=\"rm\" valueType=\"string\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                <targetPlatform archList=\"all\" binaryParser=\"org.eclipse.cdt.core.ELF\" id=\"ilg.gnuarmeclipse.managedbuild.cross.targetPlatform.1930830087\" isAbstract=\"false\" osList=\"all\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.targetPlatform\"/>");
        dotProject.addLine(SECTION_NAME, "                                <builder buildPath=\"${workspace_loc:/s1ja-test}/Release\" id=\"ilg.gnuarmeclipse.managedbuild.cross.builder.1596061717\" keepEnvironmentInBuildfile=\"false\" managedBuildOn=\"true\" name=\"Gnu Make Builder\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.builder\"/>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.assembler.1485120562\" name=\"GNU ARM Cross Assembler\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.assembler\">");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.assembler.usepreprocessor.842231502\" name=\"Use preprocessor\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.assembler.usepreprocessor\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.assembler.include.paths.1471365046\" name=\"Include paths (-I)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.assembler.include.paths\" valueType=\"includePath\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../include&quot;\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../system/include&quot;\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../system/include/cmsis&quot;\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../system/include/R7FS1JA78&quot;\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.assembler.defs.1147099156\" name=\"Defined symbols (-D)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.assembler.defs\" valueType=\"definedSymbols\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"NDEBUG\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <inputType id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.assembler.input.472818843\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.assembler.input\"/>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.compiler.1226355073\" name=\"GNU ARM Cross C Compiler\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.compiler\">");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.include.paths.1651405921\" name=\"Include paths (-I)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.include.paths\" useByScannerDiscovery=\"true\" valueType=\"includePath\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../include&quot;\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../system/include&quot;\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../system/include/cmsis&quot;\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../system/include/R7FS1JA78&quot;\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.warning.missingprototypes.254433080\" name=\"Warn if a global function has no prototype (-Wmissing-prototypes)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.warning.missingprototypes\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.warning.strictprototypes.820697093\" name=\"Warn if a function has no arg type (-Wstrict-prototypes)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.warning.strictprototypes\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.warning.badfunctioncast.90445435\" name=\"Warn if wrong cast  (-Wbad-function-cast)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.warning.badfunctioncast\" useByScannerDiscovery=\"true\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.defs.1217300631\" name=\"Defined symbols (-D)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.compiler.defs\" useByScannerDiscovery=\"true\" valueType=\"definedSymbols\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"NDEBUG\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <inputType id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.compiler.input.471504753\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.compiler.input\"/>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.compiler.1440997213\" name=\"GNU ARM Cross C++ Compiler\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.compiler\">");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.compiler.include.paths.522893819\" name=\"Include paths (-I)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.compiler.include.paths\" useByScannerDiscovery=\"true\" valueType=\"includePath\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../include&quot;\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../system/include&quot;\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../system/include/cmsis&quot;\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../system/include/R7FS1JA78&quot;\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.compiler.defs.904048635\" name=\"Defined symbols (-D)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.compiler.defs\" useByScannerDiscovery=\"true\" valueType=\"definedSymbols\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"NDEBUG\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <inputType id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.compiler.input.1818242185\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.compiler.input\"/>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.linker.1895513109\" name=\"GNU ARM Cross C Linker\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.linker\">");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.gcsections.1356948530\" name=\"Remove unused sections (-Xlinker --gc-sections)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.gcsections\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.paths.202878378\" name=\"Library search path (-L)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.paths\" valueType=\"libPaths\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../ldscripts&quot;\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.scriptfile.777157304\" name=\"Script files (-T)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.scriptfile\" valueType=\"stringList\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"mem.ld\"/>");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"libs.ld\"/>");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"sections.ld\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.nostart.1530217116\" name=\"Do not use standard start files (-nostartfiles)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.nostart\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.usenewlibnano.183324496\" name=\"Use newlib-nano (--specs=nano.specs)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.c.linker.usenewlibnano\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <inputType id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.linker.input.114272906\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.c.linker.input\">");
        dotProject.addLine(SECTION_NAME, "                                        <additionalInput kind=\"additionalinputdependency\" paths=\"$(USER_OBJS)\"/>");
        dotProject.addLine(SECTION_NAME, "                                        <additionalInput kind=\"additionalinput\" paths=\"$(LIBS)\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </inputType>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.linker.1545574615\" name=\"GNU ARM Cross C++ Linker\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.linker\">");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.gcsections.344820884\" name=\"Remove unused sections (-Xlinker --gc-sections)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.gcsections\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.paths.1201357513\" name=\"Library search path (-L)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.paths\" valueType=\"libPaths\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"&quot;../ldscripts&quot;\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option IS_BUILTIN_EMPTY=\"false\" IS_VALUE_EMPTY=\"false\" id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.scriptfile.44802700\" name=\"Script files (-T)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.scriptfile\" valueType=\"stringList\">");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"mem.ld\"/>");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"libs.ld\"/>");
        dotProject.addLine(SECTION_NAME, "                                        <listOptionValue builtIn=\"false\" value=\"sections.ld\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </option>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.nostart.1548636818\" name=\"Do not use standard start files (-nostartfiles)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.nostart\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.usenewlibnano.1518365287\" name=\"Use newlib-nano (--specs=nano.specs)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.usenewlibnano\" value=\"true\" valueType=\"boolean\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "                                    <inputType id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.linker.input.1300436017\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.cpp.linker.input\">");
        dotProject.addLine(SECTION_NAME, "                                        <additionalInput kind=\"additionalinputdependency\" paths=\"$(USER_OBJS)\"/>");
        dotProject.addLine(SECTION_NAME, "                                        <additionalInput kind=\"additionalinput\" paths=\"$(LIBS)\"/>");
        dotProject.addLine(SECTION_NAME, "                                    </inputType>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.archiver.1777318963\" name=\"GNU ARM Cross Archiver\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.archiver\"/>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.createflash.1371612179\" name=\"GNU ARM Cross Create Flash Image\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.createflash\"/>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.createlisting.227266271\" name=\"GNU ARM Cross Create Listing\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.createlisting\">");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.source.1895644662\" name=\"Display source (--source|-S)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.source\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.allheaders.1308845251\" name=\"Display all headers (--all-headers|-x)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.allheaders\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.demangle.326442498\" name=\"Demangle names (--demangle|-C)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.demangle\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.linenumbers.1345499336\" name=\"Display line numbers (--line-numbers|-l)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.linenumbers\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.wide.142508807\" name=\"Wide lines (--wide|-w)\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.createlisting.wide\" value=\"true\" valueType=\"boolean\"/>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                                <tool id=\"ilg.gnuarmeclipse.managedbuild.cross.tool.printsize.32832632\" name=\"GNU ARM Cross Print Size\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.tool.printsize\">");
        dotProject.addLine(SECTION_NAME, "                                    <option id=\"ilg.gnuarmeclipse.managedbuild.cross.option.printsize.format.1425664370\" name=\"Size format\" superClass=\"ilg.gnuarmeclipse.managedbuild.cross.option.printsize.format\"/>");
        dotProject.addLine(SECTION_NAME, "                                </tool>");
        dotProject.addLine(SECTION_NAME, "                            </toolChain>");
        dotProject.addLine(SECTION_NAME, "                        </folderInfo>");
        dotProject.addLine(SECTION_NAME, "                        <sourceEntries>");
        dotProject.addLine(SECTION_NAME, "                            <entry flags=\"VALUE_WORKSPACE_PATH|RESOLVED\" kind=\"sourcePath\" name=\"\"/>");
        dotProject.addLine(SECTION_NAME, "                        </sourceEntries>");
        dotProject.addLine(SECTION_NAME, "                    </configuration>");
        dotProject.addLine(SECTION_NAME, "                </storageModule>");
        dotProject.addLine(SECTION_NAME, "                <storageModule moduleId=\"org.eclipse.cdt.core.externalSettings\"/>");
        dotProject.addLine(SECTION_NAME, "            </cconfiguration>");
        dotProject.addLine(SECTION_NAME, "        </storageModule>");
        dotProject.addLine(SECTION_NAME, "        <storageModule moduleId=\"cdtBuildSystem\" version=\"4.0.0\">");
        dotProject.addLine(SECTION_NAME, "            <project id=\"s1ja-test.ilg.gnuarmeclipse.managedbuild.cross.target.elf.414677979\" name=\"Executable\" projectType=\"ilg.gnuarmeclipse.managedbuild.cross.target.elf\"/>");
        dotProject.addLine(SECTION_NAME, "        </storageModule>");
        dotProject.addLine(SECTION_NAME, "        <storageModule moduleId=\"scannerConfiguration\">");
        dotProject.addLine(SECTION_NAME, "            <autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"\"/>");
        dotProject.addLine(SECTION_NAME, "            <scannerConfigBuildInfo instanceId=\"ilg.gnuarmeclipse.managedbuild.cross.config.elf.debug.276895468;ilg.gnuarmeclipse.managedbuild.cross.config.elf.debug.276895468.;ilg.gnuarmeclipse.managedbuild.cross.tool.c.compiler.1135653666;ilg.gnuarmeclipse.managedbuild.cross.tool.c.compiler.input.51250587\">");
        dotProject.addLine(SECTION_NAME, "                <autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"\"/>");
        dotProject.addLine(SECTION_NAME, "            </scannerConfigBuildInfo>");
        dotProject.addLine(SECTION_NAME, "            <scannerConfigBuildInfo instanceId=\"ilg.gnuarmeclipse.managedbuild.cross.config.elf.release.426600561;ilg.gnuarmeclipse.managedbuild.cross.config.elf.release.426600561.;ilg.gnuarmeclipse.managedbuild.cross.tool.c.compiler.1226355073;ilg.gnuarmeclipse.managedbuild.cross.tool.c.compiler.input.471504753\">");
        dotProject.addLine(SECTION_NAME, "                <autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"\"/>");
        dotProject.addLine(SECTION_NAME, "            </scannerConfigBuildInfo>");
        dotProject.addLine(SECTION_NAME, "        </storageModule>");
        dotProject.addLine(SECTION_NAME, "        <storageModule moduleId=\"org.eclipse.cdt.core.LanguageSettingsProviders\"/>");
        dotProject.addLine(SECTION_NAME, "        <storageModule moduleId=\"refreshScope\" versionNumber=\"2\">");
        dotProject.addLine(SECTION_NAME, "            <configuration configurationName=\"Debug\">");
        dotProject.addLine(SECTION_NAME, "                <resource resourceType=\"PROJECT\" workspacePath=\"/s1ja-test\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "            </configuration>");
        dotProject.addLine(SECTION_NAME, "            <configuration configurationName=\"Release\">");
        dotProject.addLine(SECTION_NAME, "                <resource resourceType=\"PROJECT\" workspacePath=\"/s1ja-test\"/>");  // TDOD cfg?
        dotProject.addLine(SECTION_NAME, "            </configuration>");
        dotProject.addLine(SECTION_NAME, "        </storageModule>");
        dotProject.addLine(SECTION_NAME, "        <storageModule moduleId=\"org.eclipse.cdt.make.core.buildtargets\"/>");
        dotProject.addLine(SECTION_NAME, "    </cproject>");
        return dotProject;
    }


    private static TextFile createEclipse_dot_project(String ProjectName)
    {
        TextFile dotProject = new TextFile(".project");
        dotProject.createSection(SECTION_NAME);
        dotProject.addLine(SECTION_NAME, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        dotProject.addLine(SECTION_NAME, "<projectDescription>");
        dotProject.addLine(SECTION_NAME, "  <name>" + ProjectName + "</name>");
        dotProject.addLine(SECTION_NAME, "  <comment></comment>");
        dotProject.addLine(SECTION_NAME, "  <projects>");
        dotProject.addLine(SECTION_NAME, "  </projects>");
        dotProject.addLine(SECTION_NAME, "  <buildSpec>");
        dotProject.addLine(SECTION_NAME, "      <buildCommand>");
        dotProject.addLine(SECTION_NAME, "          <name>org.eclipse.cdt.managedbuilder.core.genmakebuilder</name>");
        dotProject.addLine(SECTION_NAME, "          <triggers>clean,full,incremental,</triggers>");
        dotProject.addLine(SECTION_NAME, "          <arguments>");
        dotProject.addLine(SECTION_NAME, "          </arguments>");
        dotProject.addLine(SECTION_NAME, "      </buildCommand>");
        dotProject.addLine(SECTION_NAME, "      <buildCommand>");
        dotProject.addLine(SECTION_NAME, "          <name>org.eclipse.cdt.managedbuilder.core.ScannerConfigBuilder</name>");
        dotProject.addLine(SECTION_NAME, "          <triggers>full,incremental,</triggers>");
        dotProject.addLine(SECTION_NAME, "          <arguments>");
        dotProject.addLine(SECTION_NAME, "          </arguments>");
        dotProject.addLine(SECTION_NAME, "      </buildCommand>");
        dotProject.addLine(SECTION_NAME, "  </buildSpec>");
        dotProject.addLine(SECTION_NAME, "  <natures>");
        dotProject.addLine(SECTION_NAME, "      <nature>org.eclipse.cdt.core.cnature</nature>");
        dotProject.addLine(SECTION_NAME, "      <nature>org.eclipse.cdt.managedbuilder.core.managedBuildNature</nature>");
        dotProject.addLine(SECTION_NAME, "      <nature>org.eclipse.cdt.managedbuilder.core.ScannerConfigNature</nature>");
        dotProject.addLine(SECTION_NAME, "      <nature>org.eclipse.cdt.core.ccnature</nature>");
        dotProject.addLine(SECTION_NAME, "  </natures>");
        dotProject.addLine(SECTION_NAME, "</projectDescription>");
        return dotProject;
    }


    public static FileGroup generateFileInto(Context ctx, FileGroup files)
    {
        if(null == files)
        {
            return null;
        }

        String projectName = ctx.cfg().getString(Configuration.PROJECT_FILE_CFG);
        if(null == projectName)
        {
            LOG.error( "No project name provided !");
            ctx.addError(CLASS_NAME, "No project name provided !");
            return null;
        }
        if(1 > projectName.length())
        {
            LOG.error( "Empty project name provided !");
            ctx.addError(CLASS_NAME, "Empty project name provided !");
            return null;
        }
        if(true == projectName.contains(File.separator))
        {
            // remove path
            projectName = projectName.substring(projectName.lastIndexOf(File.separator) + 1);
        }

        files.add(createEclipse_dot_project(projectName));
        files.add(createEclipse_dot_cproject(projectName));
        return files;
    }

}
