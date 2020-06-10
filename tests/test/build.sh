#!/bin/bash

# clean
rm main.o 
rm Makefile 
rm test

#make all
#qmake test.pro
qmake
make
