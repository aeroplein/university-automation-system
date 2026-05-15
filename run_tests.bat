@echo off
echo Compiling tests...
javac -cp ".;lib/*" -d bin src/test/ProjectValidationTest.java
if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Running Project Validation Suite...
java -cp "bin;.;lib/*" test.ProjectValidationTest
pause
