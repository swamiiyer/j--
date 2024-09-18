@echo off

REM This is a convenience script for compiling a Java source file A that uses the CLEmitter interface to
REM programmatically generate a Java class file B, and running the class A to produce class B.

set BASE_DIR=%~dp0
set j="%BASE_DIR%\..\..\"
set JAVA=java
set JAVAC=javac
set CPATH=".;%BASE_DIR%\..\lib\j--.jar"
if "%CLASSPATH%" == "" goto runApp
set CPATH=%CPATH%;"%CLASSPATH%"
if "%1" == "" goto usage

:runApp
REM Run CLEmitter.
%JAVAC% -classpath %CPATH% -d . %1
%JAVA% -classpath %CPATH% %~n1
goto cleanup

:usage
echo Usage: clemitter ^<file^>

:cleanup
REM Cleanup
set JAVA=
set JAVAC=
set BASE_DIR=
set CPATH=
