package model;

/**
 * User class represents a system user with authentication credentials and role
 * Supports three roles: Admin, Instructor, and Student
 */
public class User {
    private String username;
    private String password;
    private String role; // "Admin", "Instructor", "Student"
    private String fullName;
    private String referenceId; // Links to StudentProfile.studentId or instructor identifier
    private String department; // Academic department (e.g., "CENG", "EEE")

    // Constructors
    public User() {
    }

    public User(String username, String password, String role, String fullName, String referenceId, String department) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.referenceId = referenceId;
        this.department = department;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Converts user data to CSV format for file persistence
     * Format: username,password,role,fullName,referenceId,department
     */
    public String toFileString() {
        return String.format("%s,%s,%s,%s,%s,%s",
                username != null ? username : "",
                password != null ? password : "",
                role != null ? role : "",
                fullName != null ? fullName.replace(",", ";") : "",
                referenceId != null ? referenceId : "",
                department != null ? department : "");
    }

    /**
     * Creates a User object from CSV string
     */
    public static User fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = line.split(",", 6);
        if (parts.length >= 6) {
            return new User(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].replace(";", ",").trim(),
                    parts[4].trim(),
                    parts[5].trim()
            );
        } else if (parts.length == 5) {
            // Backward compatibility
            return new User(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].replace(";", ",").trim(),
                    parts[4].trim(),
                    ""
            );
        }
        return null;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
