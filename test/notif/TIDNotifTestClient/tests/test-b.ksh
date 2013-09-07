#!/bin/ksh

# @ TELEFONICA I+D.   jueves, 29 de marzo de 2001, 10:42:31 MET DST

# Script ejecucion programa: 

EFORCE_HOME=/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common
TIDNOTIF_HOME=$EFORCE_HOME/TIDNotif
TOOLS_HOME=$TIDNOTIF_HOME/tools
TEST_HOME=$TOOLS_HOME/tests

TIDORBJ_DIR=/opt/TIDorbJ_1.0.8

export CLASSPATH=../lib/TIDNotifTestClient.jar:$EFORCE_HOME/util/lib/utilIdl.jar:$EFORCE_HOME/util/lib/utilSrv.jar:$EFORCE_HOME/CosLifeCycle/lib/CosLifeCycleIdl.jar:$EFORCE_HOME/CosLifeCycle/lib/CosLifeCycleSrv.jar:$EFORCE_HOME/CosEvent/lib/CosEventIdl.jar:$EFORCE_HOME/CosEvent/lib/CosEventSrv.jar:$TIDNOTIF_HOME/lib/TIDNotifIdl.jar:$TIDNOTIF_HOME/lib/TIDNotifSrv.jar:$TIDORBJ_DIR/lib/tidorbj.jar:/usr/j2se/jre/lib/rt.jar::

OUT_DIR=./OUT

if [ .$1. == .. ]
then
  echo
  echo "usage:"
  echo "  test.ksh <num.test>"
  echo
  exit 0
fi

echo
echo Test6
echo

#echo Removing previous files...
#rm -f $OUT_DIR/*.ior
#rm -f $OUT_DIR/*.out
#echo

test-consumer-b.ksh $1 &

sleep 10

echo "running" > fin.$1

test-supplier-b.ksh $1 &

while [ -a fin.$1 ]
do
  sleep 2
done

echo comprobar
comprobar.ksh $1
echo

echo ChkServer
chkserver.ksh
echo

echo destroyAll
destroyAll.ksh
echo

