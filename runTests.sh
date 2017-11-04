#!/bin/bash
ant
echo "Test 1: help"
echo "============"
java -jar Puzzler.jar -h
echo " --- Test Finished ---"
echo "Test 2:"
echo "========"
rm test_out -r 2> /dev/null
echo "Test 2a:(STM32)"
echo "==============="
java -jar Puzzler.jar -v -v -o test_out/stm32 -l res/lib/ -w tests/data/ -e res/environment/ blinkyProject_stm32.xml
echo "Test 2a:(AVR)"
echo "============="
java -jar Puzzler.jar -v -v -o test_out/avr   -l res/lib/ -w tests/data/ -e res/environment/ blinkyProject_avr.xml


echo "======================"
echo " --- Test Finished ---"
echo "======================"

