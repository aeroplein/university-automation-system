package model;

/**
 * CurriculumMapping links a course to a department and year
 */
public class CurriculumMapping {
    private String departmentCode;
    private int year;
    private String courseCode;

    public CurriculumMapping() {}

    public CurriculumMapping(String departmentCode, int year, String courseCode) {
        this.departmentCode = departmentCode;
        this.year = year;
        this.courseCode = courseCode;
    }

    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String toFileString() {
        return String.format("%s,%d,%s", departmentCode, year, courseCode);
    }

    public static CurriculumMapping fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",");
        if (parts.length >= 3) {
            try {
                return new CurriculumMapping(
                    parts[0].trim(),
                    Integer.parseInt(parts[1].trim()),
                    parts[2].trim()
                );
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
