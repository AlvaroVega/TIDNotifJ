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
  echo "  run-serie-300.ksh <num.test>"
  echo
  exit 0
fi

ORBJ_TRACE_SI="es.tid.TIDorbj.trace.level 3 es.tid.TIDorbj.trace.file tidorbj.out"
NOTIF_TRACE_SI="-TIDNotif.trace.level 3 -TIDNotif.debug.time true -TIDNotif.trace.date false -TIDNotif.trace.appname null"

#ORBJ_TRACE=$ORBJ_TRACE_SI
#NOTIF_TRACE=$NOTIF_TRACE_SI
ORBJ_TRACE=
NOTIF_TRACE=


echo "run-serie-300.ksh" $1
echo

# 
# -Xss<size>        set maximum native stack size for any thread
# -Xoss<size>       set maximum Java stack size for any thread
# -Xms<size>        set initial Java heap size
# -Xmx<size>        set maximum Java heap size
# 
# -Xmx100m
# 

if [ $1 == 300 ] || [ $1 == 350 ]
then

  echo "Supplier Threads: 1 - Internal Threads: 1 - Consumer_Threads: 1"
  echo
  /usr/j2se/bin/java -Xmx100m $JAVA_RUN_OPTS es.tid.corba.TIDNotif.Server $ORBJ_TRACE $NOTIF_TRACE es.tid.TIDorbj.poa.max_threads 1 -TIDNotif.supplier.minthreads 1 -TIDNotif.consumer.minthreads 1 -TIDNotif.internal.minthreads 1 es.tid.TIDorbj.iiop.max_connections 180

elif [ $1 == 310 ] || [ $1 == 360 ]
then

  echo "Supplier Threads: 1 - Internal Threads: 1 - Consumer_Threads: 20"
  echo
  /usr/j2se/bin/java -Xmx100m $JAVA_RUN_OPTS es.tid.corba.TIDNotif.Server $ORBJ_TRACE $NOTIF_TRACE es.tid.TIDorbj.poa.max_threads 1 -TIDNotif.consumer.maxthreads 20 -TIDNotif.supplier.minthreads 1 -TIDNotif.consumer.minthreads 20 -TIDNotif.internal.minthreads 1 es.tid.TIDorbj.iiop.max_connections 180

elif [ $1 == 320 ] || [ $1 == 370 ]
then

  echo "Supplier Threads: 1 - Internal Threads: 20 - Consumer_Threads: 1"
  echo
  /usr/j2se/bin/java -Xmx100m $JAVA_RUN_OPTS es.tid.corba.TIDNotif.Server $ORBJ_TRACE $NOTIF_TRACE es.tid.TIDorbj.poa.max_threads 1 -TIDNotif.internal.maxthreads 20 -TIDNotif.supplier.minthreads 1 -TIDNotif.consumer.minthreads 1 -TIDNotif.internal.minthreads 20 es.tid.TIDorbj.iiop.max_connections 180

elif [ $1 == 330 ] || [ $1 == 380 ]
then

  echo "Supplier Threads: 1 - Internal Threads: 20 - Consumer_Threads: 20"
  echo
  /usr/j2se/bin/java -Xmx100m $JAVA_RUN_OPTS es.tid.corba.TIDNotif.Server $ORBJ_TRACE $NOTIF_TRACE es.tid.TIDorbj.poa.max_threads 1 -TIDNotif.supplier.minthreads 1 -TIDNotif.consumer.minthreads 20 -TIDNotif.internal.minthreads 20 -TIDNotif.consumer.maxthreads 20 -TIDNotif.internal.maxthreads 20 es.tid.TIDorbj.iiop.max_connections 180

elif [ $1 == 340 ] || [ $1 == 390 ] || [ $1 == 391 ] || [ $1 == 392 ]
then

  echo "Supplier Threads: 20 - Internal Threads: 20 - Consumer_Threads: 20"
  echo
  /usr/j2se/bin/java -Xmx100m $JAVA_RUN_OPTS es.tid.corba.TIDNotif.Server $ORBJ_TRACE $NOTIF_TRACE -TIDNotif.supplier.minthreads 20 -TIDNotif.consumer.minthreads 20 -TIDNotif.internal.minthreads 20 es.tid.TIDorbj.iiop.max_connections 180

else
  echo No Permitido.
  echo
  exit 0
fi

if [ -a tidnotif.out ]
then
  mv tidnotif.out OUT/tidnotif-$1.out
fi

if [ -a tidorbj.out ]
then
  mv tidorbj.out OUT/tidorbj-$1.out
fi

if [ -a consumer.out ]
then
  mv consumer.out OUT/consorbj-$1.out
fi

if [ -a supplier.out ]
then
  mv supplier.out OUT/supporbj-$1.out
fi

ver-out.ksh $1 > OUT/resumen-$1.out

