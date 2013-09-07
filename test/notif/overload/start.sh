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


# ADMIN_IOR=`cat $TIDNOTIFJ_HOME/bin/agent_2002.ior`
ADMIN_IOR="`cat ./agent_2002.ior`"

JVM=$JDK_HOME/bin/java
JDB=$JDK_HOME/bin/jdb

START_CLASSPATH=.:$TIDNOTIFJ_HOME/lib/TIDNotifJ.jar:$TIDORBJ_HOME/lib/tidorbj.jar

$JVM -classpath $START_CLASSPATH \
    -Dorg.omg.CORBA.ORBClass=es.tid.TIDorbj.core.TIDORB \
    -Dorg.omg.CORBA.ORBSingletonClass=es.tid.TIDorbj.core.SingletonORB \
    ChannelManager \
    es.tid.TIDorbj.trace.file channel_manager.log \
    es.tid.TIDorbj.trace.level 5 &

sleep 2


$JVM -classpath $START_CLASSPATH \
    -Dorg.omg.CORBA.ORBClass=es.tid.TIDorbj.core.TIDORB \
    -Dorg.omg.CORBA.ORBSingletonClass=es.tid.TIDorbj.core.SingletonORB \
    SetupChannel $ADMIN_IOR \
    es.tid.TIDorbj.trace.file setup_channel.log \
    es.tid.TIDorbj.trace.level 5 &

sleep 2


$JVM -classpath $START_CLASSPATH \
    -Dorg.omg.CORBA.ORBClass=es.tid.TIDorbj.core.TIDORB \
    -Dorg.omg.CORBA.ORBSingletonClass=es.tid.TIDorbj.core.SingletonORB \
    PushConsumer $ADMIN_IOR \
    es.tid.TIDorbj.trace.file push_consumer.log \
    es.tid.TIDorbj.trace.level 5 &

sleep 2

#$JVM -classpath $START_CLASSPATH \
#    -Dorg.omg.CORBA.ORBClass=es.tid.TIDorbj.core.TIDORB \
#    -Dorg.omg.CORBA.ORBSingletonClass=es.tid.TIDorbj.core.SingletonORB \
#    PullConsumer $ADMIN_IOR \
#    es.tid.TIDorbj.trace.file pull_consumer.log \
#    es.tid.TIDorbj.trace.level 5 &
#
#sleep 2

$JVM -classpath $START_CLASSPATH \
    -Dorg.omg.CORBA.ORBClass=es.tid.TIDorbj.core.TIDORB \
    -Dorg.omg.CORBA.ORBSingletonClass=es.tid.TIDorbj.core.SingletonORB \
    PushSupplier $ADMIN_IOR \
    es.tid.TIDorbj.trace.file supplier.log \
    es.tid.TIDorbj.trace.level 5 
