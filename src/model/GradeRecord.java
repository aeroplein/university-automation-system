package model;

/**
 * GradeRecord class stores student grades for courses
 * Includes methods for average calculation and letter grade conversion
 */
public class GradeRecord {
    private String studentUsername;
    private String courseCode;
    private double midterm;
    private double finalExam;

    // Constructors
    public GradeRecord() {
    }

    public GradeRecord(String studentUsername, String courseCode, double midterm, double finalExam) {
        this.studentUsername = studentUsername;
        this.courseCode = courseCode;
        this.midterm = midterm;
        this.finalExam = finalExam;
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

    public double getMidterm() {
        return midterm;
    }

    public void setMidterm(double midterm) {
        this.midterm = midterm;
    }

    public double getFinalExam() {
        return finalExam;
    }

    public void setFinalExam(double finalExam) {
        this.finalExam = finalExam;
    }

    /**
     * Calculates the weighted average: 40% midterm + 60% final
     */
    public double calculateAverage() {
        return (midterm * 0.4) + (finalExam * 0.6);
    }

    /**
     * Converts numerical grade to letter grade
     */
    public String getLetterGrade() {
        return calculateLetterGradeStatic(calculateAverage());
    }

    /**
     * Static utility to convert numerical grade to letter grade
     */
    public static String calculateLetterGradeStatic(double avg) {
        if (avg >= 90) return "AA";
        else if (avg >= 85) return "BA";
        else if (avg >= 80) return "BB";
        else if (avg >= 75) return "CB";
        else if (avg >= 70) return "CC";
        else if (avg >= 65) return "DC";
        else if (avg >= 60) return "DD";
        else if (avg >= 50) return "FD";
        else return "FF";
    }

    /**
     * Converts letter grade to GPA points (4.0 scale)
     */
    public double getGradePoint() {
        String letter = getLetterGrade();
        switch (letter) {
            case "AA": return 4.0;
            case "BA": return 3.5;
            case "BB": return 3.0;
            case "CB": return 2.5;
            case "CC": return 2.0;
            case "DC": return 1.5;
            case "DD": return 1.0;
            case "FD": return 0.5;
            case "FF": return 0.0;
            default: return 0.0;
        }
    }

    /**
     * Converts grade record to CSV format for file persistence
     * Format: studentUsername,courseCode,midterm,finalExam
     */
    public String toFileString() {
        return String.format(java.util.Locale.US, "%s,%s,%.2f,%.2f",
                studentUsername != null ? studentUsername : "",
                courseCode != null ? courseCode : "",
                midterm,
                finalExam);
    }

    /**
     * Creates a GradeRecord object from CSV string
     */
    public static GradeRecord fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = line.split(",", 4);
        if (parts.length >= 4) {
            try {
                return new GradeRecord(
                        parts[0].trim(),
                        parts[1].trim(),
                        Double.parseDouble(parts[2].trim()),
                        Double.parseDouble(parts[3].trim())
                );
            } catch (NumberFormatException e) {
                System.err.println("Error parsing grade data: " + line);
                return null;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "GradeRecord{" +
                "studentUsername='" + studentUsername + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", midterm=" + midterm +
                ", finalExam=" + finalExam +
                ", average=" + calculateAverage() +
                ", letterGrade='" + getLetterGrade() + '\'' +
                '}';
    }
}
