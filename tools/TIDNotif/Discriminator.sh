#!/bin/ksh

JVM="$JDK_HOME/bin/java $JAVA_RUN_OPTS"

if [ $# -gt 0 ]
then
  if [ $1 = add_constraint ]
  then
    $JVM es.tid.corba.TIDNotif.tools.Discriminator $1 "$2"
  elif [ $1 = replace_constraint ]
   then
    $JVM es.tid.corba.TIDNotif.tools.Discriminator $1 $2 "$3"
  else
    $JVM es.tid.corba.TIDNotif.tools.Discriminator $*
  fi
else
  $JVM es.tid.corba.TIDNotif.tools.Discriminator $*
fi
