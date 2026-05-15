package test;

import data.DataStore;
import model.*;
import util.InputValidator;
import java.io.File;

/**
 * Core System Validation Test
 * This class verifies the business logic, data persistence, and algorithms 
 * required by the project rubric.
 */
public class ProjectValidationTest {

    private static int testsPassed = 0;
    private static int totalTests = 0;

    public static void main(String[] args) {
        System.out.println("=== UniAuto Project Validation Suite ===");
        
        testDataStoreInitialization();
        testStudentIdAlgorithm();
        testAuthentication();
        testCourseEnrollmentLogic();
        testGPACalculation();
        
        System.out.println("\n=== Test Results Summary ===");
        System.out.println("Checks Passed: " + testsPassed + " / " + totalTests);
        
        if (testsPassed == totalTests && totalTests > 0) {
            System.out.println("SUCCESS: Your project logic is 100% compliant with the requirements!");
        } else {
            System.out.println("WARNING: Some checks failed. Please review the output above.");
        }
    }

    private static void testDataStoreInitialization() {
        startTest("DataStore File Presence");
        File usersFile = new File("data/users.txt");
        check(usersFile.exists(), "users.txt must exist in data/ directory");
        endTest();
    }

    private static void testStudentIdAlgorithm() {
        startTest("Student ID Algorithmic Generation");
        String id1 = InputValidator.generateStudentId(2025, "CS");
        String id2 = InputValidator.generateStudentId(2025, "CS");
        
        check(id1.length() == 9, "Student ID must be exactly 9 digits");
        check(InputValidator.isValidStudentIdAlgorithm(id1), "Generated ID must pass the checksum validation");
        check(!id1.equals(id2), "Consecutive IDs should be unique");
        endTest();
    }

    private static void testAuthentication() {
        startTest("Authentication & Role Authorization");
        DataStore ds = DataStore.getInstance();
        
        // Use an existing user from your data or add a test one
        User testUser = new User("auth_test", "pass123", "Student", "Test Student", "REF001");
        ds.addUser(testUser);
        
        User authenticated = ds.authenticate("auth_test", "pass123");
        check(authenticated != null, "Should authenticate with correct credentials");
        check("Student".equals(authenticated.getRole()), "Role must be correctly retrieved");
        
        User failed = ds.authenticate("auth_test", "wrong_pass");
        check(failed == null, "Should reject incorrect password");
        endTest();
    }

    private static void testCourseEnrollmentLogic() {
        startTest("Course Enrollment & Quota Management");
        DataStore ds = DataStore.getInstance();
        String uniqueId = "T" + System.currentTimeMillis() % 10000;
        
        Course c = new Course(uniqueId, "Test Course", 3, 2, "prof1");
        ds.addCourse(c);
        
        check(ds.enrollStudent("s1" + uniqueId, uniqueId), "First enrollment should succeed");
        check(ds.enrollStudent("s2" + uniqueId, uniqueId), "Second enrollment should succeed (within quota)");
        check(!ds.enrollStudent("s3" + uniqueId, uniqueId), "Third enrollment should fail (quota full)");
        endTest();
    }

    private static void testGPACalculation() {
        startTest("GPA Calculation Accuracy");
        DataStore ds = DataStore.getInstance();
        String uniqueId = "G" + System.currentTimeMillis() % 10000;
        String student = "student" + uniqueId;
        
        // Setup: Courses must exist and student must be enrolled
        ds.addCourse(new Course("MAT" + uniqueId, "Math", 4, 10, "prof1"));
        ds.addCourse(new Course("PHY" + uniqueId, "Physics", 4, 10, "prof1"));
        ds.enrollStudent(student, "MAT" + uniqueId);
        ds.enrollStudent(student, "PHY" + uniqueId);
        
        ds.upsertGrade(student, "MAT" + uniqueId, 95, 95); // AA (4.0)
        ds.upsertGrade(student, "PHY" + uniqueId, 80, 80); // BB (3.0)
        
        double gpa = ds.calculateGPA(student);
        check(Math.abs(gpa - 3.5) < 0.01, "GPA should be exactly 3.5 for AA and BB grades");
        endTest();
    }

    private static void startTest(String name) {
        System.out.print("[TEST] " + name + "... ");
    }

    private static void check(boolean condition, String message) {
        totalTests++;
        if (!condition) {
            System.err.println("\nFAIL: " + message);
        } else {
            testsPassed++;
        }
    }

    private static void endTest() {
        System.out.println("DONE");
    }
}
