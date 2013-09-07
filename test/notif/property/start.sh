#!/bin/sh
set +u

if [ -z "$JAVA_HOME" ]; then
	echo "Environment variable JAVA_HOME must be set"
	exit 1
fi

if [ -z "$TIDORBJ_HOME" ]; then
	echo "Environment variable TIDORBJ_HOME must be set"
	exit 1	
fi

if [ -z "$TIDNOTIFJ_HOME" ]; then
	echo "Environment variable TIDNOTIFJ_HOME must be set"
	exit 1	
fi


ADMIN_IOR=`cat $TIDNOTIFJ_HOME/bin/agent_2002.ior`

JVM=$JDK_HOME/bin/java

START_CLASSPATH=.:$TIDNOTIFJ_HOME/lib/TIDNotifJ.jar:$TIDORBJ_HOME/lib/tidorbj.jar

$JVM -classpath $START_CLASSPATH SetupChannel $ADMIN_IOR \
    es.tid.TIDorbj.trace.file channel.log \
    es.tid.TIDorbj.trace.level 4

$JVM -classpath $START_CLASSPATH PushConsumer $ADMIN_IOR \
    es.tid.TIDorbj.trace.file consumer.log \
    es.tid.TIDorbj.trace.level 4 &

$JVM -classpath $START_CLASSPATH PushSupplier2 $ADMIN_IOR \
    es.tid.TIDorbj.trace.file supplier2.log \
    es.tid.TIDorbj.trace.level 4 &

$JVM -classpath $START_CLASSPATH PushSupplier $ADMIN_IOR \
    es.tid.TIDorbj.trace.file supllier.log \
    es.tid.TIDorbj.trace.level 4
