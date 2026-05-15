import data.DataStore;
import model.*;
import java.util.List;

public class TestFacultyCurriculum {
    public static void main(String[] args) {
        DataStore ds = DataStore.getInstance();
        
        System.out.println("--- Testing Faculty ---");
        ds.addFaculty(new Faculty("ENG", "Engineering"));
        ds.addFaculty(new Faculty("SCI", "Science"));
        
        for (Faculty f : ds.getAllFaculties()) {
            System.out.println("Faculty: " + f.getName() + " (" + f.getCode() + ")");
        }

        System.out.println("\n--- Testing Department-Faculty Link ---");
        ds.addDepartment(new Department("CS", "Computer Science", "ENG"));
        Department cs = ds.findDepartment("CS");
        System.out.println("CS Dept Faculty Code: " + cs.getFacultyCode());

        System.out.println("\n--- Testing Curriculum ---");
        ds.addCurriculumMapping(new CurriculumMapping("CS", 1, "CS101"));
        ds.addCurriculumMapping(new CurriculumMapping("CS", 1, "MAT101"));
        
        List<CurriculumMapping> curriculum = ds.getCurriculumByDeptAndYear("CS", 1);
        System.out.println("CS Year 1 Curriculum size: " + curriculum.size());
        for (CurriculumMapping m : curriculum) {
            System.out.println(" - " + m.getCourseCode());
        }

        System.out.println("\n--- Testing Student Curriculum Codes ---");
        // Create a test student
        User studentUser = new User("test_student", "pass", "Student", "Test Student", "REF001");
        ds.addUser(studentUser);
        StudentProfile profile = new StudentProfile("2024CS001", "Test Student", "Computer Science", 1, "test_student");
        ds.addStudentProfile(profile);
        
        List<String> codes = ds.getCurriculumCourseCodesForStudent("test_student");
        System.out.println("Curriculum codes for test_student: " + codes);
        
        if (codes.contains("CS101") && codes.contains("MAT101")) {
            System.out.println("SUCCESS: Curriculum codes retrieved correctly!");
        } else {
            System.out.println("FAILURE: Curriculum codes missing!");
        }
    }
}
