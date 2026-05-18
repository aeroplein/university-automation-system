package model;

// Touched to trigger IDE compilation re-index
/**
 * Department class represents an academic department in the university
 */
public class Department {
    private String code;
    private String name;
    private String facultyCode;

    public Department() {
    }

    public Department(String code, String name, String facultyCode) {
        this.code = code;
        this.name = name;
        this.facultyCode = facultyCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacultyCode() {
        return facultyCode;
    }

    public void setFacultyCode(String facultyCode) {
        this.facultyCode = facultyCode;
    }

  
    public String toFileString() {
        return String.format("%s,%s,%s", code, name, facultyCode != null ? facultyCode : "");
    }

    public static Department fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",");
        if (parts.length >= 2) {
            String code = parts[0].trim();
            String name = parts[1].trim();
            String faculty = (parts.length >= 3) ? parts[2].trim() : "";
            return new Department(code, name, faculty);
        }
        return null;
    }

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}
