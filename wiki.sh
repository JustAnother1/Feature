#!/bin/bash
ant
if [ $? -ne 0 ] ; then
    echo ""
    echo "ERROR: Ant reportet an error !"
    exit
fi

rm test_out -r 2> /dev/null
rm wiki.err 2> /dev/null
rm wiki.zip 2> /dev/null

echo -e "<?xml version='1.0' encoding='utf-8' ?>\n" \
"<project>\n" \
"    <environment>\n" \
"        <tool name='avr/atmega2560' />\n" \
"        <root_api name='program_entry_point' />\n" \
"        <resources>\n" \
"            <greenLed algorithm='gpio' port='B' pin='7' />\n" \
"        </resources>\n" \
"    </environment>\n" \
"    <solution ref='solution.xml' />\n" \
"</project>\n" | java -jar Puzzler.jar -v -v --zip_to_stdout --prj_name wiki_avr -l res/lib/ -w tests/data/ -e feature/ > wiki.zip 2> wiki.err

# cat test_out/avr/main.c
ls -l

if (($(wc -c < wiki.zip)<1)) ; then
    cat wiki.err
fi
