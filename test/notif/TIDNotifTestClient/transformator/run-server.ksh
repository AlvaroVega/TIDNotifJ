LIB_HOME=$EFORCE_HOME/work/CORBAServices/NotificationService_TID
JARS=$LIB_HOME/lib/TIDEvent.jar:$LIB_HOME/lib/TIDNotif.jar:$LIB_HOME/lib/CosLifeCycle.jar:$LIB_HOME/lib/EventData.jar:$LIB_HOME/lib/Transformer.jar

LD_LIBRARY_PATH=/usr/j2se/jre/lib/sparc/motif21
export LD_LIBRARY_PATH
DISPLAY=bambam:0.0
export DISPLAY

if [ .$1. == .. ]
then
  echo
  echo "usage:"
  echo "  run-server.ksh <num.test>"
  echo
  exit 0
fi

ORBJ_TRACE_SI="es.tid.TIDorbj.trace.level 3 es.tid.TIDorbj.trace.file tidorbj.out"
NOTIF_TRACE_SI="-TIDNotif.trace.level 4 -TIDNotif.debug.time true -TIDNotif.trace.date false -TIDNotif.trace.appname null"

ORBJ_TRACE=$ORBJ_TRACE_SI
NOTIF_TRACE=$NOTIF_TRACE_SI
#ORBJ_TRACE=
#NOTIF_TRACE=

echo "run-server.ksh" $1
echo

if [ $1 == 900 ]
then
  echo "Supplier Threads: 1 - Internal Threads: 1 - Consumer_Threads: 1"
  echo
  java -Xmx100m -classpath $TIDORBJ_HOME/lib/tidorbj.jar:$JARS es.tid.corba.TIDNotif.Server $ORBJ_TRACE $NOTIF_TRACE es.tid.TIDorbj.poa.max_threads 1 -TIDNotif.supplier.minthreads 1 -TIDNotif.consumer.minthreads 1 -TIDNotif.internal.minthreads 1 es.tid.TIDorbj.iiop.max_connections 180

else 
  echo No Permitido.
  echo
  exit 0
fi
