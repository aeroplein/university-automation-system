@echo off
REM University Automation System - Windows Run Script

cd /d "%~dp0"

REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Compile the application
echo Compiling application...
javac --release 8 -d bin -cp "lib/*" -sourcepath src src\ui\UniversityAutomationApp.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Starting University Automation System...
    echo.
    echo Default Admin Login:
    echo   Username: admin
    echo   Password: admin123
    echo.
    
    REM Run the application
    java -cp "bin;lib/*" ui.UniversityAutomationApp
) else (
    echo Compilation failed. Please check for errors.
    pause
    exit /b 1
)
