package util;

// Touched to trigger IDE compilation re-index
public class InputValidator {

    /**
     * Validates username: not empty, min 3 characters
     */
    public static String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Username is required.";
        }
        if (username.trim().length() < 3) {
            return "Username must be at least 3 characters long.";
        }
        return null;
    }

    /**
     * Validates password: not empty, min 6 characters
     */
    public static String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Password is required.";
        }
        if (password.trim().length() < 6) {
            return "Password must be at least 6 characters long.";
        }
        return null;
    }

    /**
     * Validates student ID: not empty, numeric format, and satisfies the university
     * algorithm
     * Algorithm: 9 digits, starts with '20', sum of digits % 10 == 7
     */
    public static String validateStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return "Student ID is required.";
        }
        if (!studentId.matches("\\d{9}")) {
            return "Student ID must be exactly 9 digits.";
        }
        if (!studentId.startsWith("20")) {
            return "Student ID must start with '20'.";
        }
        if (!isValidStudentIdAlgorithm(studentId)) {
            return "Enter a valid student id";
        }
        return null;
    }

    /**
     * Checks if the student ID satisfies the university checksum algorithm
     * Algorithm: (Sum of digits) % 10 == 7
     */
    public static boolean isValidStudentIdAlgorithm(String studentId) {
        if (studentId == null || studentId.length() != 9 || !studentId.matches("\\d+")) {
            return false;
        }
        int sum = 0;
        for (char c : studentId.toCharArray()) {
            sum += Character.getNumericValue(c);
        }
        return sum % 10 == 7;
    }

    /**
     * Generates a valid student ID based on year and department
     * Algorithm: 20 + year(last 2) + deptCode(2) + random(2) + checksum_digit
     * (Total 9 digits)
     */
    public static String generateStudentId(int year, String deptCode) {
        // Use year and deptCode if possible, or just generate a valid 9-digit ID
        java.util.Random rand = new java.util.Random();
        String base = "20" + String.format("%02d", year % 100);

        // Normalize deptCode to 2 digits deterministically
        int dCode = 0;
        if (deptCode == null)
            deptCode = "GEN";
        switch (deptCode.toUpperCase()) {
            case "CS":
                dCode = 11;
                break;
            case "IE":
                dCode = 22;
                break;
            case "EE":
                dCode = 33;
                break;
            case "ME":
                dCode = 44;
                break;
            case "CE":
                dCode = 55;
                break;
            case "BA":
                dCode = 66;
                break;
            case "PSY":
                dCode = 77;
                break;
            case "LAW":
                dCode = 88;
                break;
            case "CEN":
                dCode = 10;
                break;
            default:
                dCode = Math.abs(deptCode.hashCode() % 90) + 10;
        }
        base += String.format("%02d", dCode);

        // Use 2 digits for serial to make base 8 digits total
        base += String.format("%02d", rand.nextInt(100));

        // Find the 9th digit to satisfy sum % 10 == 7
        int sum = 0;
        for (char c : base.toCharArray()) {
            sum += Character.getNumericValue(c);
        }

        int currentMod = sum % 10;
        int checkDigit = (7 - currentMod + 10) % 10;

        return base + checkDigit;
    }

    /**
     * Validates course code: not empty, alphanumeric (e.g. CS101)
     */
    public static String validateCourseCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return "Course code is required.";
        }
        if (!courseCode.matches("[A-Z]{2,4}\\d{3}")) {
            return "Course code must be in format like CS101 or MATH202.";
        }
        return null;
    }

    /**
     * Validates credit: 1-10
     */
    public static String validateCredit(String creditStr) {
        try {
            int credit = Integer.parseInt(creditStr.trim());
            if (credit < 1 || credit > 10) {
                return "Credit must be between 1 and 10.";
            }
        } catch (NumberFormatException e) {
            return "Credit must be a valid number.";
        }
        return null;
    }

    /**
     * Validates quota: 1-500
     */
    public static String validateQuota(String quotaStr) {
        try {
            int quota = Integer.parseInt(quotaStr.trim());
            if (quota < 1 || quota > 500) {
                return "Quota must be between 1 and 500.";
            }
        } catch (NumberFormatException e) {
            return "Quota must be a valid number.";
        }
        return null;
    }

    /**
     * Validates grade: 0-100
     */
    public static String validateGrade(String gradeStr, String fieldName) {
        try {
            double grade = Double.parseDouble(gradeStr.replace(",", ".").trim());
            if (grade < 0 || grade > 100) {
                return fieldName + " must be between 0 and 100.";
            }
        } catch (NumberFormatException e) {
            return "Invalid grade format for " + fieldName + ".";
        }
        return null;
    }

    /**
     * Generates a professional enterprise-grade Reference ID
     * Structure: PREFIX-YEAR-SEQUENCE (e.g. INS-25-001)
     */
    public static String generateReferenceId(String role, int sequence) {
        String prefix = "ADM";
        if ("Instructor".equalsIgnoreCase(role))
            prefix = "INS";
        else if ("Student".equalsIgnoreCase(role))
            prefix = "STU";

        int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100;
        // Enterprise format: PREFIX-YY-000 (e.g. INS-25-001)
        return String.format("%s-%02d-%03d", prefix, year, Math.max(1, sequence));
    }

    /**
     * Enterprise-grade string normalization for Turkish characters.
     * Converts characters like ç, ş, ü, ı, ö, ğ to their English counterparts.
     */
    public static String normalizeTurkish(String text) {
        if (text == null) return null;
        
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            switch (c) {
                case 'ç': case 'Ç': sb.append('c'); break;
                case 'ğ': case 'Ğ': sb.append('g'); break;
                case 'ı': case 'I': case 'İ': sb.append('i'); break;
                case 'ö': case 'Ö': sb.append('o'); break;
                case 'ş': case 'Ş': sb.append('s'); break;
                case 'ü': case 'Ü': sb.append('u'); break;
                default: sb.append(c); break;
            }
        }
        return sb.toString();
    }

    /**
     * Generates a username in name.surname format from a full name
     */
    public static String generateUsername(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "user" + System.currentTimeMillis() % 1000;
        }
        
        // Remove prefixes like "Prof." or "Dr."
        String cleanName = fullName.replace("Prof. ", "").replace("Dr. ", "").trim().toLowerCase();
        
        // Use enterprise normalization
        cleanName = normalizeTurkish(cleanName);
                           
        String[] parts = cleanName.split("\\s+");
        if (parts.length >= 2) {
            String firstName = parts[0];
            String lastName = parts[parts.length - 1];
            
            // If name and surname are the same (e.g. "Ali Ali"), suffix the name
            if (firstName.equals(lastName)) {
                return firstName + ".user" + (System.currentTimeMillis() % 100);
            }
            return firstName + "." + lastName;
        }
        return parts[0];
    }
}
