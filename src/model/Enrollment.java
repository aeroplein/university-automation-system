package model;

/**
 * Enrollment class represents a student's registration in a course
 */
public class Enrollment {
    private String studentUsername;
    private String courseCode;

    // Constructors
    public Enrollment() {
    }

    public Enrollment(String studentUsername, String courseCode) {
        this.studentUsername = studentUsername;
        this.courseCode = courseCode;
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

    /**
     * Converts enrollment data to CSV format for file persistence
     * Format: studentUsername,courseCode
     */
    public String toFileString() {
        return String.format("%s,%s",
                studentUsername != null ? studentUsername : "",
                courseCode != null ? courseCode : "");
    }

    /**
     * Creates an Enrollment object from CSV string
     */
    public static Enrollment fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = line.split(",", 2);
        if (parts.length >= 2) {
            return new Enrollment(
                    parts[0].trim(),
                    parts[1].trim()
            );
        }
        return null;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "studentUsername='" + studentUsername + '\'' +
                ", courseCode='" + courseCode + '\'' +
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
