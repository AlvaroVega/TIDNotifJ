#!/bin/ksh

# @ TELEFONICA I+D.   jueves, 29 de marzo de 2001, 10:42:31 MET DST

# Script ejecucion programa: 

EFORCE_HOME=/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common
TIDNOTIF_HOME=$EFORCE_HOME/TIDNotif
TOOLS_HOME=$TIDNOTIF_HOME/tools
TEST_HOME=$TOOLS_HOME/tests

TIDORBJ_DIR=/opt/TIDorbJ_1.0.8

export CLASSPATH=../lib/TIDNotifTestClient.jar:$EFORCE_HOME/util/lib/utilIdl.jar:$EFORCE_HOME/util/lib/utilSrv.jar:$EFORCE_HOME/CosLifeCycle/lib/CosLifeCycleIdl.jar:$EFORCE_HOME/CosLifeCycle/lib/CosLifeCycleSrv.jar:$EFORCE_HOME/CosEvent/lib/CosEventIdl.jar:$EFORCE_HOME/CosEvent/lib/CosEventSrv.jar:$TIDNOTIF_HOME/lib/TIDNotifIdl.jar:$TIDNOTIF_HOME/lib/TIDNotifSrv.jar:$TIDORBJ_DIR/lib/tidorbj.jar:/usr/j2se/jre/lib/rt.jar::

if [ .$1. == .. ]
then
  echo
  echo "usage:"
  echo "  run-serie-100.ksh <num.test>"
  echo
  exit 0
fi

ORBJ_TRACE="es.tid.TIDorbj.trace.level 3 es.tid.TIDorbj.trace.file tidorbj.out"
NOTIF_TRACE="-TIDNotif.trace.level 3 -TIDNotif.debug.time true -TIDNotif.trace.d
ate false -TIDNotif.trace.appname null"

#ORBJ_TRACE=$ORBJ_TRACE_SI
#NOTIF_TRACE=$NOTIF_TRACE_SI
ORBJ_TRACE=
NOTIF_TRACE=

echo "run-serie-100.ksh" $1
echo
echo "Supplier Threads: 20 - Internal Threads: 20 - Consumer_Threads: 20"
echo

# 
# -Xss<size>        set maximum native stack size for any thread
# -Xoss<size>       set maximum Java stack size for any thread
# -Xms<size>        set initial Java heap size
# -Xmx<size>        set maximum Java heap size
# 
# -Xmx100m
# 
/usr/j2se/bin/java -Xmx100m $JAVA_RUN_OPTS es.tid.corba.TIDNotif.Server $ORBJ_TRACE $NOTIF_TRACE
