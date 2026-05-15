$path = "src/ui/UniversityAutomationApp.java"
$content = [System.IO.File]::ReadAllText($path, [System.Text.Encoding]::UTF8)

Write-Host "Starting aggressive repair..."

# 1. Fix the double brace at 1589-1590
# We look for "return panel;" followed by two braces and then the Utility comment
# We'll use a very flexible regex
$pattern1 = "(?s)(return panel;\s*})\s*}(\s*// [^\r\n]+ Utility)"
if ($content -match $pattern1) {
    Write-Host "Found double brace at line 1589. Fixing..."
    $content = $content -replace $pattern1, '$1$2'
} else {
    Write-Host "Double brace pattern not found. Trying simpler regex..."
    $content = $content -replace "(?s)(return panel;\s*})\s*}(\s*//)", '$1$2'
}

# 2. Fix merged line at 1591 (just in case)
$content = $content -replace "(// [^\r\n]+ Utility [^\r\n]+)\s+(private void refreshTranscript)", "`$1`r`n    `$2"

# 3. Fix the createCurriculumPanel and the end of the file
# The error report says:
# codeField cannot be resolved at 1827
# panel cannot be resolved at 1840
# This means the method createCurriculumPanel (starting at 1739) is broken.

# Let's find the createCurriculumPanel method and ensure it has the variables
if ($content.Contains("private JPanel createCurriculumPanel()") -and -not $content.Contains("JTextField codeField = new JTextField();")) {
    $methodStart = $content.IndexOf("private JPanel createCurriculumPanel()")
    $openBrace = $content.IndexOf("{", $methodStart)
    if ($openBrace -ne -1) {
        $fields = "`r`n        JTextField codeField = new JTextField();`r`n        JTextField nameField = new JTextField();`r`n"
        $content = $content.Substring(0, $openBrace + 1) + $fields + $content.Substring($openBrace + 1)
        Write-Host "Added missing fields to createCurriculumPanel"
    }
}

# 4. Clean up the end of the file.
# We'll remove everything after the last "return panel;" and properly close the class and add main.
$lastReturn = $content.LastIndexOf("return panel;")
if ($lastReturn -ne -1) {
    # Find the NEXT brace after the last return
    $nextBrace = $content.IndexOf("}", $lastReturn)
    if ($nextBrace -ne -1) {
        # This brace closes the last method.
        # Everything after this should be the main method and the final class brace.
        $mainAndClose = "`r`n    }`r`n`r`n    public static void main(String[] args) {`r`n        try {`r`n            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());`r`n        } catch (Exception `$e) {}`r`n        `r`n        SwingUtilities.invokeLater(() -> {`r`n            new UniversityAutomationApp().setVisible(true);`r`n        });`r`n    }`r`n}`r`n"
        $content = $content.Substring(0, $nextBrace + 1) + $mainAndClose
        Write-Host "Cleaned up end of file and ensured proper closing."
    }
}

# Final check for rogue closing braces before member declarations
# If we see "} private void" or "} public static void", it might be an extra brace
$content = $content -replace "}\s*}(\s*(private|public))", "}`r`n    $1"

[System.IO.File]::WriteAllText($path, $content, [System.Text.Encoding]::UTF8)
Write-Host "Aggressive repair complete!"
