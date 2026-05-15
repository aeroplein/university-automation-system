import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class RepairApp {
    public static void main(String[] args) {
        try {
            Path path = Paths.get("src/ui/UniversityAutomationApp.java");
            System.out.println("Reading " + path.toAbsolutePath());
            
            // Try different encodings
            byte[] bytes = Files.readAllBytes(path);
            String content = new String(bytes, StandardCharsets.UTF_8);
            
            // 1. Fix the double brace and merged line at 1589-1591
            String oldBlock1 = "return panel;\n    }\n    }\n\n    // ── Utility ─────────────────────────────────────────────    private void refreshTranscript() {";
            String newBlock1 = "return panel;\n    }\n\n    // ── Utility ─────────────────────────────────────────────\n    private void refreshTranscript() {";
            
            // Try with \r\n too
            String oldBlock1_win = oldBlock1.replace("\n", "\r\n");
            String newBlock1_win = newBlock1.replace("\n", "\r\n");
            
            if (content.contains(oldBlock1)) {
                content = content.replace(oldBlock1, newBlock1);
                System.out.println("Fixed block 1 (LF)");
            } else if (content.contains(oldBlock1_win)) {
                content = content.replace(oldBlock1_win, newBlock1_win);
                System.out.println("Fixed block 1 (CRLF)");
            } else {
                System.out.println("Block 1 not found. Attempting fuzzy match for merged line...");
                content = content.replace("── Utility ─────────────────────────────────────────────    private void", 
                                          "── Utility ─────────────────────────────────────────────\n    private void");
            }

            // 2. Add logout method after refreshTranscript
            if (!content.contains("private void logout()")) {
                int index = content.indexOf("gpaLabel.setText");
                if (index != -1) {
                    int braceIndex = content.indexOf("}", index);
                    if (braceIndex != -1) {
                        String logoutMethod = "\n\n    private void logout() {\n        currentUser = null;\n        cardLayout.show(mainPanel, LOGIN_CARD);\n    }\n";
                        content = content.substring(0, braceIndex + 1) + logoutMethod + content.substring(braceIndex + 1);
                        System.out.println("Added logout method");
                    }
                }
            }

            // 3. Fix the corrupted createCurriculumPanel and the junk at the end
            // We'll look for the last "return panel;" and replace everything after it with a proper closing brace and main method.
            int lastReturn = content.lastIndexOf("return panel;");
            if (lastReturn != -1) {
                // Find the first "}" after this return
                int braceIndex = content.indexOf("}", lastReturn);
                if (braceIndex != -1) {
                    String mainAndClose = "\n    }\n\n    public static void main(String[] args) {\n        try {\n            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());\n        } catch (Exception ignored) {}\n        \n        SwingUtilities.invokeLater(() -> {\n            new UniversityAutomationApp().setVisible(true);\n        });\n    }\n}\n";
                    content = content.substring(0, braceIndex + 1) + mainAndClose;
                    System.out.println("Fixed end of file and added main method");
                }
            }
            
            // 4. Fix unresolved codeField/nameField in createCurriculumPanel
            // The createCurriculumPanel method starts at line 1739.
            // We'll insert the missing field declarations if they are missing.
            if (content.contains("private JPanel createCurriculumPanel()") && !content.contains("JTextField codeField = new JTextField();")) {
                int methodStart = content.indexOf("private JPanel createCurriculumPanel()");
                int openBrace = content.indexOf("{", methodStart);
                if (openBrace != -1) {
                    String fields = "\n        JTextField codeField = new JTextField();\n        JTextField nameField = new JTextField();\n";
                    content = content.substring(0, openBrace + 1) + fields + content.substring(openBrace + 1);
                    System.out.println("Added missing fields to createCurriculumPanel");
                }
            }

            // Remove any potential NULL bytes or non-text characters that might confuse tools
            content = content.replace("\0", "");
            
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
            System.out.println("Repair complete. Please try to compile and run.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
