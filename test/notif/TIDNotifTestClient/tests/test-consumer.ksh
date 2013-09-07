#!/bin/ksh

# @ TELEFONICA I+D.   jueves, 29 de marzo de 2001, 10:42:31 MET DST

# Script ejecucion programa: 

EFORCE_HOME=/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common
TIDNOTIF_HOME=$EFORCE_HOME/TIDNotif
TOOLS_HOME=$TIDNOTIF_HOME/tools
TEST_HOME=$TOOLS_HOME/tests

TIDORBJ_DIR=/opt/TIDorbJ_1.0.8

export CLASSPATH=../lib/TIDNotifTestClient.jar:$EFORCE_HOME/util/lib/utilIdl.jar:$EFORCE_HOME/util/lib/utilSrv.jar:$EFORCE_HOME/CosLifeCycle/lib/CosLifeCycleIdl.jar:$EFORCE_HOME/CosLifeCycle/lib/CosLifeCycleSrv.jar:$EFORCE_HOME/CosEvent/lib/CosEventIdl.jar:$EFORCE_HOME/CosEvent/lib/CosEventSrv.jar:$TIDNOTIF_HOME/lib/TIDNotifIdl.jar:$TIDNOTIF_HOME/lib/TIDNotifSrv.jar:$TIDORBJ_DIR/lib/tidorbj.jar:/usr/j2se/jre/lib/rt.jar::

DATA_DIR=./DATA
OUT_DIR=./OUT

if [ .$1. == .. ]
then
  echo
  echo "usage:"
  echo "  test-consumer.ksh <num.test>"
  echo
  exit 0
fi

echo ThePushConsumerClient
echo
  /usr/j2se/bin/java $JAVA_RUN_OPTS es.tid.corba.TIDNotifTestClient.push_consumer_client.ThePushConsumerServer -TIDorb.max_blocked_time 25000 -f $DATA_DIR/test_info.$1.IN < admin.ior > $OUT_DIR/consumer.$1.out 2>&1

sleep 2

rm -f fin.$1
