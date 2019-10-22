#!/bin/bash
ant
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Ant reportet an error !"
    exit
fi

echo ""
echo "Test 1: help"
echo "============"
java -jar Puzzler.jar -h
# The return code of -h is 1 even if there was no error
# this is because on a failure in the parameters we fall
# back to showing the help informations. It is just easier that way.
if [ $? -ne 1 ] ; then
    echo ""
    echo "ERROR: Showing the Help information failed !"
    exit
fi
echo " --- Test Finished ---"

echo ""
echo "Test 2:"
echo "========"
rm test_out -r 2> /dev/null
# if this failes then we do not care

# FPGA: ICE40
echo ""
echo "Test 2a:(FPGA)"
echo "==============="
java -jar Puzzler.jar -v -v -o test_out/fpga -l res/lib/ -w tests/data/ -e res/environment/ blinkyProject_tinyFpga.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for FPGA failed !"
    exit
fi


echo ""
echo ""
echo ""
java -jar Puzzler.jar -v -v -o test_out/fpga_doc -l res/lib/ -w tests/data/ -e res/environment/ -Ddocument_code_source=true blinkyProject_tinyFpga.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for FPGA with comments failed !"
    exit
fi

# ST STM32
echo ""
echo "Test 2b:(STM32)"
echo "==============="
java -jar Puzzler.jar -v -v -o test_out/stm32 -l res/lib/ -w tests/data/ -e res/environment/ blinkyProject_stm32.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for STM32 failed !"
    exit
fi


echo ""
echo ""
echo ""
java -jar Puzzler.jar -v -v -o test_out/stm32_doc -l res/lib/ -w tests/data/ -e res/environment/ -Ddocument_code_source=true blinkyProject_stm32.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for STM32 with comments failed !"
    exit
fi

echo ""
echo ""
echo ""
java -jar Puzzler.jar -v -v -z test_out/stm32_doc.zip -l res/lib/ -w tests/data/ -e res/environment/ -Ddocument_code_source=true blinkyProject_stm32.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for STM32 with comments failed !"
    exit
fi

# Microchip (Atmel) AVR
echo ""
echo "Test 2c:(AVR)"
echo "============="
java -jar Puzzler.jar -v -v -o test_out/avr   -l res/lib/ -w tests/data/ -e res/environment/ blinkyProject_avr.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for AVR failed !"
    exit
fi


echo ""
echo ""
echo ""
java -jar Puzzler.jar -v -v -o test_out/avr_doc   -l res/lib/ -w tests/data/ -e res/environment/ -Ddocument_code_source=true blinkyProject_avr.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for AVR with comments failed !"
    exit
fi


# Renesas S1
echo ""
echo "Test 2d:(Renesas S1JA)"
echo "============="
java -jar Puzzler.jar -v -v -o test_out/renesas_s1ja   -l res/lib/ -w tests/data/ -e res/environment/ blinkyProject_renesas_s1ja.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for Renesas S1JA failed !"
    exit
fi


echo ""
echo ""
echo ""
java -jar Puzzler.jar -v -v -o test_out/renesas_s1ja_doc   -l res/lib/ -w tests/data/ -e res/environment/ -Ddocument_code_source=true blinkyProject_renesas_s1ja.xml
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Creating blinky project for Renesas S1JA with comments failed !"
    exit
fi



echo " --- Test Finished ---"
echo ""
echo "==========================="
echo " --- All tests finished ---"
echo "==========================="

