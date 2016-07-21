#!/bin/bash
#

cd ./src/

export CLASSPATH=$CLASSPATH:../jgroups-3.6.4.Final.jar
find . -name "*.java" -print | xargs javac

java views.UserScreen 2> /dev/null