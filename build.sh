#!/bin/bash
set xe
PLAY="/opt/play/play"
OUTPUT_WAR_DIR="war/svnadmin"

$PLAY deps
$PLAY auto-test

cd test-result
(
echo "<html><head><title>Test results</title></head><body><ul>"

for i in *.failed.html ; do
    if [ -e $i ] ; then
        echo "<li><a href="$i">$i</li>"
    fi
done

echo "</ul><p><ul>"

for i in *.passed.html ; do
    if [ -e $i ] ; then
        echo "<li><a href="$i">$i</li>"
    fi
done

echo "</ul></body></html>"
) > index.html

if [ -e result.failed ] ; then
    exit 1
else
    cd ..
    $PLAY war . -o $OUTPUT_WAR_DIR --exclude .svn:war:test-result:tmp:eclipse:logs --%prod --zip
fi
cd -

