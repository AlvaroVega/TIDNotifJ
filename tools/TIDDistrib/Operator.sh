#!/bin/ksh

JVM="$JDK_HOME/bin/java $JAVA_RUN_OPTS"

#Operator add_rule Id \"expresion\" ior_file operation < operator.ior
#Operator replace_rule Id \"expresion\" ior_file operation <operator.ior
#$1 $2 "$3" $4 $5
#Operator insert_rule Id Position \"expresion\" ior_file operation<operator.ior
#$1 $2 $3 "$4" $5 $6

if [ $# -gt 0 ]
then
  if [ $1 = add_rule ]
  then
    $JVM es.tid.corba.TIDDistrib.tools.Operator $1 $2 "$3" $4 $5
  elif [ $1 = replace_rule ]
   then
    $JVM es.tid.corba.TIDDistrib.tools.Operator $1 $2 "$3" $4 $5
  elif [ $1 = insert_rule ]
   then
    $JVM es.tid.corba.TIDDistrib.tools.Operator $1 $2 $3 "$4" $5 $6
  else
    $JVM es.tid.corba.TIDDistrib.tools.Operator $*
  fi
else
  $JVM es.tid.corba.TIDDistrib.tools.Operator $*
fi

