@echo off

IF %JAVA_HOME%=="" GOTO JDK_NOT_DEF
GOTO JDK_OK

:JDK_NOT_DEF
echo Environment variable JAVA_HOME must be set
exit /b 1

:JDK_OK

IF %TIDNOTIFJ_HOME%=="" GOTO HOME_NOT_DEF
GOTO HOME_OK

:HOME_NOT_DEF
echo Environment variable TIDNOTIFJ_HOME must be set
echo
exit /b 1

:HOME_OK
rem make sure that neither JAVA_HOME nor TIDNOTIFJ_HOME have SPACES in their values
rem set CMD=%JAVA_HOME%\bin\java -jar %TIDNOTIFJ_HOME%\lib\start.jar %*
set CMD=%JAVA_HOME%\bin\java -classpath %TIDNOTIFJ_HOME%\lib\TIDNotifJ.jar;%TIDORBJ_HOME%\lib\tidorbj.jar es.tid.corba.TIDNotif.Server %*
echo %CMD%
%CMD% 
set CMD=


