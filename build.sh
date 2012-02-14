#!/bin/bash
PLAY="/opt/play/play"
OUTPUT_WAR_DIR="/tmp/svnadmin"

RELEASE=`cat RELEASE 2> /dev/null`
if [ "$RELEASE" == "" ]; then
    RELEASE=1
    echo $RELEASE > RELEASE
fi

WAR_FILE="svnadmin##0$RELEASE.war"

function autotest() {
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
    fi
    
    cd ..
}

function create_war() {
    rm -rf war 2> /dev/null
    
    $PLAY war . -o $OUTPUT_WAR_DIR --exclude .svn:.git:war:test:test-result:tmp:eclipse:logs:application.log --%prod

    # Add tomcat basic auth in web.xml
    cp conf/tomcat-basic-auth-web.xml $OUTPUT_WAR_DIR/WEB-INF/web.xml
    
    # Create war file
    cd $OUTPUT_WAR_DIR
    zip ../$WAR_FILE -1 -r .
    cd -
    
    # Increment release number
    expr $RELEASE + 1 > RELEASE
}

function deploy() {
    local server=$1
    if [ "$server" == "" ]; then
        echo "What server?"
        exit 1
    fi

    local release=$2
    if [ "$release" == "" ]; then
        echo "What release?"
        exit 1
    fi

    local file="svnadmin##0$release.war"
    scp $OUTPUT_WAR_DIR/../$file ips@$server:/opt/ips/tomcat7/webapps/
}

case "$1" in
   deploy)
       echo "deploying..."
       deploy $2 $3
       ;;
   *)
       echo "building..."
       autotest
       create_war
       ;;
esac

