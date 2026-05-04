@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set JAVA_HOME_CANDIDATE=C:\Program Files\Java\jdk-17

if not "%JAVA_HOME%" == "" goto javaHomeSet
if exist "%JAVA_HOME_CANDIDATE%" (
    set JAVA_HOME=%JAVA_HOME_CANDIDATE%
)
:javaHomeSet

set MAVEN_WRAPPER_JAR=%~dp0.mvn\wrapper\maven-wrapper.jar
set MAVEN_WRAPPER_PROPERTIES=%~dp0.mvn\wrapper\maven-wrapper.properties

if exist "%MAVEN_WRAPPER_JAR%" goto wrapperJarFound

echo Downloading Maven Wrapper...
"%JAVA_HOME%\bin\java" -jar "%MAVEN_WRAPPER_JAR%" %*
goto end

:wrapperJarFound
"%JAVA_HOME%\bin\java" -jar "%MAVEN_WRAPPER_JAR%" %*

:end
endlocal
