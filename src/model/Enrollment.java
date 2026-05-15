package model;

/**
 * Enrollment class represents a student's registration in a course
 */
public class Enrollment {
    private String studentUsername;
    private String courseCode;
    private String status; // PENDING, APPROVED

    // Constructors
    public Enrollment() {
        this.status = "APPROVED";
    }

    public Enrollment(String studentUsername, String courseCode) {
        this(studentUsername, courseCode, "APPROVED");
    }

    public Enrollment(String studentUsername, String courseCode, String status) {
        this.studentUsername = studentUsername;
        this.courseCode = courseCode;
        this.status = status;
    }

    // Getters and Setters
    public String getStudentUsername() {
        return studentUsername;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Converts enrollment data to CSV format for file persistence
     * Format: studentUsername,courseCode,status
     */
    public String toFileString() {
        return String.format("%s,%s,%s",
                studentUsername != null ? studentUsername : "",
                courseCode != null ? courseCode : "",
                status != null ? status : "APPROVED");
    }

    /**
     * Creates an Enrollment object from CSV string
     */
    public static Enrollment fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = line.split(",");
        if (parts.length >= 2) {
            String status = (parts.length >= 3) ? parts[2].trim() : "APPROVED";
            return new Enrollment(
                    parts[0].trim(),
                    parts[1].trim(),
                    status
            );
        }
        return null;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "studentUsername='" + studentUsername + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        return studentUsername.equals(that.studentUsername) && courseCode.equals(that.courseCode);
    }

    @Override
    public int hashCode() {
        return studentUsername.hashCode() + courseCode.hashCode();
    }
}
