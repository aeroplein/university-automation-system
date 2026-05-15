package model;

/**
 * StudentProfile class stores detailed information about students
 */
public class StudentProfile {
    private String studentId;
    private String fullName;
    private String department;
    private String secondDepartment; // For Double Major
    private int year;
    private int secondYear; // For Double Major
    private String username; // Links to User.username

    // Constructors
    public StudentProfile() {
    }

    public StudentProfile(String studentId, String fullName, String department, int year, String username) {
        this(studentId, fullName, department, null, year, 0, username);
    }

    public StudentProfile(String studentId, String fullName, String department, String secondDepartment, int year, int secondYear, String username) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.department = department;
        this.secondDepartment = secondDepartment;
        this.year = year;
        this.secondYear = secondYear;
        this.username = username;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSecondDepartment() {
        return secondDepartment;
    }

    public void setSecondDepartment(String secondDepartment) {
        this.secondDepartment = secondDepartment;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSecondYear() {
        return secondYear;
    }

    public void setSecondYear(int secondYear) {
        this.secondYear = secondYear;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Converts student profile to CSV format for file persistence
     * Format: studentId,fullName,department,year,username[,secondDepartment]
     */
    public String toFileString() {
        String base = String.format("%s,%s,%s,%d,%s",
                studentId != null ? studentId : "",
                fullName != null ? fullName.replace(",", ";") : "",
                department != null ? department.replace(",", ";") : "",
                year,
                username != null ? username : "");
        if (secondDepartment != null && !secondDepartment.isEmpty()) {
            return base + "," + secondDepartment.replace(",", ";") + "," + secondYear;
        }
        return base;
    }

    /**
     * Creates a StudentProfile object from CSV string
     */
    public static StudentProfile fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = line.split(",");
        if (parts.length >= 5) {
            try {
                String secDept = (parts.length >= 6) ? parts[5].replace(";", ",").trim() : null;
                int secYear = (parts.length >= 7) ? Integer.parseInt(parts[6].trim()) : 0;
                String rawUsername = parts[4].trim();
                String cleanUsername = rawUsername.split(" ")[0]; // Strip (PENDING) if exists
                
                return new StudentProfile(
                        parts[0].trim(),
                        parts[1].replace(";", ",").trim(),
                        parts[2].replace(";", ",").trim(),
                        secDept,
                        Integer.parseInt(parts[3].trim()),
                        secYear,
                        cleanUsername
                );
            } catch (NumberFormatException e) {
                System.err.println("Error parsing student year: " + line);
                return null;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "StudentProfile{" +
                "studentId='" + studentId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", department='" + department + '\'' +
                (secondDepartment != null ? ", secondDepartment='" + secondDepartment + '\'' : "") +
                ", year=" + year +
                (secondDepartment != null ? ", secondYear=" + secondYear : "") +
                ", username='" + username + '\'' +
                '}';
    }
}
