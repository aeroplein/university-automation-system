package scratch;

import data.DataStore;
import model.*;
import util.InputValidator;
import java.util.*;

/**
 * Script to populate the University Automation System with large-scale realistic data.
 */
public class PopulateData {
    private static final Random rand = new Random();
    private static final String[] FIRST_NAMES = {"Ahmet", "Mehmet", "Ayşe", "Fatma", "Can", "Ece", "Burak", "Deniz", "Emre", "Selin", "Mert", "Zeynep", "Arda", "Pelin", "Kerem", "Gizem", "Oğuz", "Beren", "Kaan", "Duru"};
    private static final String[] LAST_NAMES = {"Yılmaz", "Kaya", "Demir", "Çelik", "Öztürk", "Arslan", "Doğan", "Kılıç", "Aydın", "Yıldız", "Özkan", "Şahin", "Polat", "Güneş", "Bulut", "Yavuz", "Çetin", "Aksoy"};

    public static void main(String[] args) {
        DataStore ds = DataStore.getInstance();
        System.out.println("Starting data population...");

        // 1. Define Department Curricula (Typical courses)
        Map<String, String[]> deptCourses = new HashMap<>();
        deptCourses.put("CS", new String[]{"CS101:Intro to Programming", "CS102:Object Oriented Programming", "CS201:Data Structures", "CS202:Algorithms", "CS301:Database Systems", "CS401:Operating Systems"});
        deptCourses.put("CEN", new String[]{"CEN101:Intro to Computer Eng", "CEN102:Logic Design", "CEN201:Circuits", "CEN202:Microprocessors", "CEN301:Embedded Systems", "CEN401:Robotics"});
        deptCourses.put("EE", new String[]{"EE101:Basic Circuits", "EE201:Signals and Systems", "EE301:Electromagnetic Theory", "EE401:Control Systems"});
        deptCourses.put("MATH", new String[]{"MATH101:Calculus I", "MATH102:Calculus II", "MATH201:Linear Algebra", "MATH202:Differential Equations", "MATH301:Real Analysis"});
        deptCourses.put("PHYS", new String[]{"PHYS101:Physics I", "PHYS102:Physics II", "PHYS201:Modern Physics", "PHYS301:Quantum Mechanics"});
        deptCourses.put("BA", new String[]{"BA101:Intro to Business", "BA201:Marketing", "BA301:Finance", "BA401:Management Strategy"});
        deptCourses.put("ECON", new String[]{"ECON101:Microeconomics", "ECON102:Macroeconomics", "ECON201:Econometrics", "ECON301:International Trade"});
        deptCourses.put("PSY", new String[]{"PSY101:Intro to Psychology", "PSY201:Developmental Psych", "PSY301:Social Psychology", "PSY401:Clinical Psychology"});
        deptCourses.put("SOC", new String[]{"SOC101:Intro to Sociology", "SOC201:Social Theory", "SOC301:Research Methods"});
        deptCourses.put("LAW", new String[]{"LAW101:Intro to Law", "LAW201:Civil Law", "LAW301:Criminal Law", "LAW401:Constitutional Law"});
        deptCourses.put("INTL", new String[]{"INTL101:International Relations", "INTL201:Diplomatic History", "INTL301:International Organizations"});

        // 2. Generate Courses and Curricula
        for (Map.Entry<String, String[]> entry : deptCourses.entrySet()) {
            String deptCode = entry.getKey();
            for (String courseInfo : entry.getValue()) {
                String[] parts = courseInfo.split(":");
                String code = parts[0];
                String name = parts[1];
                int year = Character.getNumericValue(code.charAt(deptCode.length()));
                
                Course c = new Course(code, name, 6, rand.nextInt(36) + 15, deptCode);
                ds.addCourse(c);
                
                // Map to curriculum
                ds.addCurriculumMapping(new CurriculumMapping(deptCode, year, code));
            }
        }

        // 3. Generate Instructors (4 per department)
        List<Department> depts = ds.getDepartments();
        for (Department d : depts) {
            for (int i = 1; i <= 4; i++) {
                String fname = FIRST_NAMES[rand.nextInt(FIRST_NAMES.length)];
                String lname = LAST_NAMES[rand.nextInt(LAST_NAMES.length)];
                String username = "prof." + fname.toLowerCase() + "." + (i + rand.nextInt(100));
                String refId = d.getCode() + "I" + String.format("%03d", i);
                
                User u = new User(username, "pass123", "INSTRUCTOR", fname + " " + lname, refId);
                ds.addUser(u);
            }
        }

        // 4. Generate Students (25-35 per department)
        for (Department d : depts) {
            int count = rand.nextInt(11) + 25; // 25 to 35
            for (int i = 1; i <= count; i++) {
                String fname = FIRST_NAMES[rand.nextInt(FIRST_NAMES.length)];
                String lname = LAST_NAMES[rand.nextInt(LAST_NAMES.length)];
                String username = fname.toLowerCase() + "." + lname.toLowerCase() + rand.nextInt(1000);
                
                // Enrollment Year (1 to 4)
                int year = rand.nextInt(4) + 1;
                int entranceYear = 2025 - year + 1;
                
                String studentId = InputValidator.generateStudentId(entranceYear, d.getCode());
                
                User u = new User(username, "student123", "STUDENT", fname + " " + lname, studentId);
                if (ds.addUser(u)) {
                    StudentProfile p = new StudentProfile(studentId, u.getFullName(), d.getName(), year, username);
                    ds.addStudentProfile(p);
                }
            }
            System.out.println("Generated " + count + " students for " + d.getName());
        }

        System.out.println("Data population complete!");
    }
}
