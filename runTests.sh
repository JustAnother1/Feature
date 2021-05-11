#!/bin/bash
Puzzler="target/puzzler-1.0-SNAPSHOT.jar"

mvn clean && mvn verify
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Maven reportet an error !"
    exit
fi

echo ""
echo "Test 1: help"
echo "============"
java -jar $Puzzler -h
# The return code of -h is 1 even if there was no error
# this is because on a failure in the parameters we fall
# back to showing the help informations. It is just easier that way.
if [ $? -ne 1 ] ; then
    echo ""
    echo "ERROR: Showing the Help information failed !"
    exit
else
    echo "OK:  show help test finished !"
fi

echo ""
echo "Test 2:"
echo "========"
rm test_out -r 2> /dev/null
# if this failes then we do not care

# Microchip (Atmel) AVR
echo ""
echo "Test 2a:(AVR)"
echo "============="
java -jar $Puzzler -v -v -o test_out/avr   -l res/lib/ -p tests/data/ -s tests/data/ -e res/environment/ blinkyProject_avr.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for AVR failed !"
    exit
else
    echo "OK:  AVR test finished !"
fi


echo ""
echo "Test 2a:(AVR) - document source code"
echo "===================================="
java -jar $Puzzler -v -v -o test_out/avr_doc   -l res/lib/ -p tests/data/ -s tests/data/ -e res/environment/ -Ddocument_code_source=true blinkyProject_avr.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for AVR with comments failed !"
    exit
else
    echo "OK:  AVR with comments test finished !"
fi

# ST STM32
echo ""
echo "Test 2b:(STM32)"
echo "==============="
java -jar $Puzzler -v -v -o test_out/stm32 -l res/lib/ -p tests/data/ -s tests/data/ -e res/environment/ blinkyProject_stm32.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for STM32 failed !"
    exit
else
    echo "OK:  STM32 test finished !"
fi


echo ""
echo "Test 2b:(STM32) - document source code"
echo "======================================"
java -jar $Puzzler -v -v -o test_out/stm32_doc -l res/lib/ -p tests/data/ -s tests/data/ -e res/environment/ -Ddocument_code_source=true blinkyProject_stm32.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for STM32 with comments failed !"
    exit
else
    echo "OK:  STM32 with comments test finished !"
fi

echo ""
echo "Test 2b:(STM32) - zip - document source code"
echo "============================================"
java -jar $Puzzler -v -v -z test_out/stm32_doc.zip -l res/lib/ -p tests/data/ -s tests/data/ -e res/environment/ -Ddocument_code_source=true blinkyProject_stm32.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for STM32 with comments into zip failed !"
    exit
else
    echo "OK:  STM32 with comment into zip test finished !"
fi

# FPGA: ICE40
echo ""
echo "Test 2c:(FPGA)"
echo "==============="
java -jar $Puzzler -v -v -o test_out/fpga -l res/lib/ -p tests/data/ -s tests/data/ -e res/environment/ blinkyProject_tinyFpga.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for FPGA failed !"
    exit
else
    echo "OK:  FPGA test finished !"
fi


echo ""
echo "Test 2c:(FPGA) - document source code"
echo "====================================="
java -jar $Puzzler -v -v -o test_out/fpga_doc -l res/lib/ -p tests/data/ -s tests/data/ -e res/environment/ -Ddocument_code_source=true blinkyProject_tinyFpga.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for FPGA with comments failed !"
    exit
else
    echo "OK:  FPGA with comments test finished !"
fi

# Renesas S1
echo ""
echo "Test 2d:(Renesas S1JA)"
echo "======================"
java -jar $Puzzler -v -v -o test_out/renesas_s1ja   -l res/lib/ -p tests/data/ -s tests/data/ -e res/environment/ blinkyProject_renesas_s1ja.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for Renesas S1JA failed !"
    exit
else
    echo "OK:  S1JA test finished !"
fi


echo ""
echo "Test 2d:(Renesas S1JA) - document source code"
echo "============================================="
java -jar $Puzzler -v -v -o test_out/renesas_s1ja_doc   -l res/lib/ -p tests/data/ -s tests/data/ -e res/environment/ -Ddocument_code_source=true blinkyProject_renesas_s1ja.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for Renesas S1JA with comments failed !"
    exit
else
    echo "OK:  S1JA with comments test finished !"
fi

## Qt
#echo ""
#echo "Test 2e:(Qt)"
#echo "============="
#java -jar $Puzzler -v -v -o test_out/qt -l res/lib/ -p tests/data/ -s tests/data/ -e res/environment/ testProject_qt.xml
#if [ $? -ne 0 ] ; then
#    echo ""
#    echo "ERROR: Creating test project for Qt failed !"
#    exit
#else
#    echo "OK:  Qt test finished !"
#fi


#echo ""
#echo "Test 2e:(Qt) - document source code"
#echo "==================================="
#java -jar $Puzzler -v -v -o test_out/qt_doc   -l res/lib/ -p tests/data/ -s tests/data/ -e res/environment/ -Ddocument_code_source=true testProject_qt.xml
#if [ $? -ne 0 ] ; then
#    echo ""
#    echo "ERROR: Creating test project for Qt with comments failed !"
#    exit
#else
#    echo "OK:  Qt with comments test finished !"
#fi


echo ""
echo ""
echo "==========================="
echo " --- All tests finished ---"
echo "==========================="

