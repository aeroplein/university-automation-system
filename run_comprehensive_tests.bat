@echo off
setlocal
echo ==================================================
echo   COMPILING COMPREHENSIVE TEST SUITE (JAVA 8 COMPATIBLE)
echo ==================================================

:: Create bin directory if it doesn't exist
if not exist bin mkdir bin

:: Compile with compatibility flags for Java 8
javac --release 8 -cp ".;lib/*" -d bin src/model/*.java src/data/*.java src/util/*.java src/test/ComprehensiveSystemTest.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed!
    pause
    exit /b %errorlevel%
)

echo.
echo ==================================================
echo   RUNNING COMPREHENSIVE SYSTEM TESTS
echo ==================================================

:: Run the test
java -cp "bin;.;lib/*" test.ComprehensiveSystemTest

if %errorlevel% neq 0 (
    echo.
    echo [FAIL] System test suite failed.
)

echo.
pause
