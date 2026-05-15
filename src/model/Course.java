package model;

/**
 * Course class represents an academic course in the system
 */
public class Course {
    private String courseCode;
    private String courseName;
    private int credit;
    private int quota;
    private String instructorUsername;

    // Constructors
    public Course() {
    }

    public Course(String courseCode, String courseName, int credit, int quota, String instructorUsername) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credit = credit;
        this.quota = quota;
        this.instructorUsername = instructorUsername;
    }

    // Getters and Setters
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public String getInstructorUsername() {
        return instructorUsername;
    }

    public void setInstructorUsername(String instructorUsername) {
        this.instructorUsername = instructorUsername;
    }

    /**
     * Converts course data to CSV format for file persistence
     * Format: courseCode,courseName,credit,quota,instructorUsername
     */
    public String toFileString() {
        return String.format("%s,%s,%d,%d,%s",
                courseCode != null ? courseCode : "",
                courseName != null ? courseName.replace(",", ";") : "",
                credit,
                quota,
                instructorUsername != null ? instructorUsername : "");
    }

    /**
     * Creates a Course object from CSV string
     */
    public static Course fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = line.split(",", 5);
        if (parts.length >= 5) {
            try {
                return new Course(
                        parts[0].trim(),
                        parts[1].replace(";", ",").trim(),
                        Integer.parseInt(parts[2].trim()),
                        Integer.parseInt(parts[3].trim()),
                        parts[4].trim()
                );
            } catch (NumberFormatException e) {
                System.err.println("Error parsing course data: " + line);
                return null;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credit=" + credit +
                ", quota=" + quota +
                ", instructorUsername='" + instructorUsername + '\'' +
                '}';
    }
}
