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
    es.tid.TIDorbj.max_blocked_time 150000 \
    es.tid.TIDorbj.trace.file channel.log \
    es.tid.TIDorbj.trace.level 5

$JVM -classpath $START_CLASSPATH StructuredPushConsumer $ADMIN_IOR \
    es.tid.TIDorbj.max_blocked_time 150000 \
    es.tid.TIDorbj.trace.file consumer.log \
    es.tid.TIDorbj.trace.level 5 &

$JVM -classpath $START_CLASSPATH StructuredPushSupplier $ADMIN_IOR \
    es.tid.TIDorbj.max_blocked_time 150000 \
    es.tid.TIDorbj.trace.file supplier.log \
    es.tid.TIDorbj.trace.level 5
