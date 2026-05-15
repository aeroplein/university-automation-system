#!/bin/bash

# University Automation System - Run Script

cd "$(dirname "$0")"

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile the application
echo "Compiling application..."
javac -d bin -cp "lib/*" -sourcepath src src/ui/UniversityAutomationApp.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Starting University Automation System..."
    echo ""
    echo "Default Admin Login:"
    echo "  Username: admin"
    echo "  Password: admin123"
    echo ""
    
    # Run the application
    java -cp "bin:lib/*" ui.UniversityAutomationApp
else
    echo "Compilation failed. Please check for errors."
    exit 1
fi
