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

if exist "%1" goto IOR_OK
echo usage: %0 service_ref
goto END

:IOR_OK

rem make sure that neither JAVA_HOME nor TIDNOTIFJ_HOME have SPACES in their values

rem %JAVA_HOME%\bin\java -jar %TIDNOTIFJ_HOME%\lib\stop.jar < %1 
%JAVA_HOME%\bin\java -classpath %TIDNOTIFJ_HOME%\lib\TIDNotifJ.jar;%TIDORBJ_HOME%\lib\tidorbj.jar < %1 

:END
