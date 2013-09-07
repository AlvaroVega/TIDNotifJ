#!/bin/ksh

# @ TELEFONICA I+D.   jueves, 22 de noviembre de 2001, 14:32:12 MET

# Script ejecucion programa: /users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/TIDNotif/source/main2/bin/Server.jar

export CLASSPATH=/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/TIDNotif/source/main2/bin/Server.jar:../util/lib/util.jar:../TIDConstraint/lib/TIDConstraint.jar:../TIDDistrib/lib/TIDDistrib.jar:../TIDNotif/lib/TIDNotif.jar::/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/util/lib/utilIdl.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/util/lib/utilSrv.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/CosEvent/lib/CosEventIdl.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/CosEvent/lib/CosEventSrv.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/CosLifeCycle/lib/CosLifeCycleIdl.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/CosLifeCycle/lib/CosLifeCycleSrv.jar::/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/TIDNotif/lib/TIDNotifIdl.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/TIDNotif/lib/TIDNotifSrv.jar:.:/opt/TIDorbJ/lib/tidorbj.jar:/usr/j2se/jre/lib/rt.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/TIDNotif/source/main2/_class::

if [ .$1. == .. ]
then
  /usr/j2se/bin/java $JAVA_RUN_OPTS es.tid.corba.TIDDistrib.Shutdown < admin.ior
else
  /usr/j2se/bin/java $JAVA_RUN_OPTS es.tid.corba.TIDDistrib.Shutdown -f $1
fi

