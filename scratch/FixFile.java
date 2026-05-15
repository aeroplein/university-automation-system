import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class FixFile {
    public static void main(String[] args) throws Exception {
        Path path = Paths.get("src/ui/UniversityAutomationApp.java");
        byte[] bytes = Files.readAllBytes(path);
        String content = new String(bytes, StandardCharsets.UTF_8);
        
        // Fix merged line and extra brace
        String oldBlock1 = "        return panel;\r\n    }\r\n    }\r\n\r\n    // ── Utility ─────────────────────────────────────────────    private void refreshTranscript() {";
        String newBlock1 = "        return panel;\r\n    }\r\n\r\n    // ── Utility ─────────────────────────────────────────────\r\n    private void refreshTranscript() {";
        
        if (content.contains(oldBlock1)) {
            content = content.replace(oldBlock1, newBlock1);
            System.out.println("Fixed block 1");
        } else {
            // Try with \n instead of \r\n
            oldBlock1 = oldBlock1.replace("\r\n", "\n");
            newBlock1 = newBlock1.replace("\r\n", "\n");
            if (content.contains(oldBlock1)) {
                content = content.replace(oldBlock1, newBlock1);
                System.out.println("Fixed block 1 (LF)");
            }
        }
        
        // Add logout method
        String logoutPoint = "    private void refreshTranscript() {";
        if (content.contains(logoutPoint) && !content.contains("private void logout()")) {
            // Find end of refreshTranscript (it ends with gpaLabel.setText(...); \n })
            int index = content.indexOf("gpaLabel.setText", content.indexOf(logoutPoint));
            if (index != -1) {
                int braceIndex = content.indexOf("}", index);
                if (braceIndex != -1) {
                    String logoutMethod = "\n\n    private void logout() {\n        currentUser = null;\n        cardLayout.show(mainPanel, LOGIN_CARD);\n    }\n";
                    content = content.substring(0, braceIndex + 1) + logoutMethod + content.substring(braceIndex + 1);
                    System.out.println("Added logout method");
                }
            }
        }

        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }
}
