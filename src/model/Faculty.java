package model;

/**
 * Faculty class represents a university faculty (e.g., Engineering, Science)
 */
public class Faculty {
    private String code;
    private String name;

    public Faculty() {}

    public Faculty(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String toFileString() {
        return String.format("%s,%s", code, name);
    }

    public static Faculty fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",", 2);
        if (parts.length >= 2) {
            return new Faculty(parts[0].trim(), parts[1].trim());
        }
        return null;
    }

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}
