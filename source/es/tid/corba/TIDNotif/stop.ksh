#!/bin/ksh

# @ TELEFONICA I+D.   jueves, 22 de noviembre de 2001, 14:32:50 MET

# Script ejecucion programa: /users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/TIDNotif/source/main/bin/Server.jar

export CLASSPATH=/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/TIDNotif/source/main/bin/Server.jar:../util/lib/util.jar:../TIDConstraint/lib/TIDConstraint.jar:../TIDnotif/TIDNotif.jar::/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/util/lib/utilIdl.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/util/lib/utilSrv.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/CosEvent/lib/CosEventIdl.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/CosEvent/lib/CosEventSrv.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/CosLifeCycle/lib/CosLifeCycleIdl.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/CosLifeCycle/lib/CosLifeCycleSrv.jar::/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/TIDNotif/lib/TIDNotifIdl.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/TIDNotif/lib/TIDNotifSrv.jar:.:/opt/TIDorbJ/lib/tidorbj.jar:/usr/j2se/jre/lib/rt.jar:/users/ars07/ccm_wa/eforce/eForce,ars07/eForce/java/common/TIDNotif/source/main/_class::

if [ .$1. == .. ]
then
  /usr/j2se/bin/java $JAVA_RUN_OPTS es.tid.corba.TIDNotif.Shutdown < admin.ior
else
  /usr/j2se/bin/java $JAVA_RUN_OPTS es.tid.corba.TIDNotif.Shutdown -f $1
fi
