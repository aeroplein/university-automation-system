package test;

import data.DataStore;
import model.*;
import util.InputValidator;
import java.util.List;
import java.util.Locale;

/**
 * Comprehensive System Test Suite
 * Verifies every major business rule, algorithm, and data relationship.
 */
public class ComprehensiveSystemTest {

    private static int testsPassed = 0;
    private static int totalTests = 0;
    private static DataStore ds;

    public static void main(String[] args) {
        // Enforce Locale.US for GPA formatting consistency during tests
        Locale.setDefault(Locale.US);
        ds = DataStore.getInstance();

        System.out.println("==================================================");
        System.out.println("   UNIVERSITY AUTOMATION COMPREHENSIVE TEST   ");
        System.out.println("==================================================\n");

        try {
            runIdentityTests();
            runAcademicStructureTests();
            runUniIDProtocolTests();
            runDoubleMajorTests();
            runEnrollmentAndQuotaTests();
            runGradingAndGPATests();
            runAdminReportingTests();
            runValidationUtilityTests();
            runInstructorViewTests();
        } finally {
            runCleanup();
        }

        System.out.println("\n==================================================");
        System.out.println("   TEST SUMMARY");
        System.out.println("   Passed: " + testsPassed + " / " + totalTests);
        System.out.println("==================================================");

        if (testsPassed == totalTests) {
            System.out.println("\n✅ SYSTEM STATUS: FULLY COMPLIANT");
        } else {
            System.out.println("\n❌ SYSTEM STATUS: DEFECTS DETECTED");
            System.exit(1);
        }
    }

    // -------------------------------------------------------------------------
    // IDENTITY TESTS
    // -------------------------------------------------------------------------
    private static void runIdentityTests() {
        startModule("IDENTITY & AUTHENTICATION");
        
        // 1. User Creation
        User u = new User("stest_user", "pass123", "Student", "System Test", "T001", "CS");
        check(ds.addUser(u) == 1, "Should allow creating a unique test user");
        check(ds.addUser(u) == -1, "Should block duplicate usernames");
        
        // 2. Authentication
        User auth = ds.authenticate("stest_user", "pass123");
        check(auth != null && auth.getFullName().equals("System Test"), "Should authenticate valid user");
        check(ds.authenticate("stest_user", "wrong") == null, "Should reject invalid password");
        
        // 3. Admin Protection
        check(!ds.deleteUser("admin"), "Should strictly block deletion of main admin account");
    }

    // -------------------------------------------------------------------------
    // ACADEMIC STRUCTURE TESTS
    // -------------------------------------------------------------------------
    private static void runAcademicStructureTests() {
        startModule("ACADEMIC STRUCTURE");
        
        // 1. Department Logic
        Department ce = new Department("CE", "Civil Engineering", "ENG");
        ds.deleteDepartment("CE"); // Clear if exists
        check(ds.addDepartment(ce), "Should allow adding new CE department");
        check(ds.findDepartment("CE") != null, "Department should be retrievable by code");
        
        // 2. Course Logic
        Course c = new Course("STEST101", "Test Course", 3, 30, "stest_prof");
        check(ds.addCourse(c), "Should allow adding a unique course");
        check(!ds.addCourse(c), "Should block duplicate course codes");
    }

    // -------------------------------------------------------------------------
    // UNI-ID PROTOCOL TESTS
    // -------------------------------------------------------------------------
    private static void runUniIDProtocolTests() {
        startModule("UNI-ID PROTOCOL (FULL SWEEP)");
        
        String[][] depts = {{"CS", "11"}, {"EE", "22"}, {"IE", "33"}, {"ME", "44"}, {"CE", "55"}};
        
        for (String[] dept : depts) {
            String code = dept[0];
            String expectedCode = dept[1];
            String id = InputValidator.generateStudentId(2025, code);
            
            check(id.contains(expectedCode), code + " ID should contain '" + expectedCode + "'");
            check(InputValidator.isValidStudentIdAlgorithm(id), code + " ID must pass checksum");
        }
        
        check(!InputValidator.isValidStudentIdAlgorithm("123456789"), "Invalid random string should fail checksum");
    }

    // -------------------------------------------------------------------------
    // DOUBLE MAJOR TESTS
    // -------------------------------------------------------------------------
    private static void runDoubleMajorTests() {
        startModule("DOUBLE MAJOR LOGIC");
        
        // 1. Setup Student
        ds.addStudentProfile(new model.StudentProfile("110001", "Double Student", "Computer Science", 3, "stest_dual"));
        StudentProfile profile = ds.findStudentProfileByUsername("stest_dual");
        profile.setSecondDepartment("EE");
        profile.setSecondYear(1);
        ds.updateStudentProfile(profile);
        
        StudentProfile retrieved = ds.findStudentProfileByUsername("stest_dual");
        check("EE".equals(retrieved.getSecondDepartment()), "Second department should be saved");
        check(retrieved.getSecondYear() == 1, "Second major year (1) should be independent of primary (3)");
        
        // senior rule check
        profile.setSecondYear(4); // 4 > 3
        boolean updated = ds.updateSenioritySafe(profile);
        check(!updated, "Should block second major year (4) if primary is (3)");
    }

    // -------------------------------------------------------------------------
    // ENROLLMENT & QUOTA TESTS
    // -------------------------------------------------------------------------
    private static void runEnrollmentAndQuotaTests() {
        startModule("ENROLLMENT & QUOTAS");
        
        // Setup students with profiles for major-restriction testing
        ds.addStudentProfile(new model.StudentProfile("119991", "Test Student 1", "Computer Science", 1, "stest_s1"));
        ds.addStudentProfile(new model.StudentProfile("229992", "Test Student 2", "Electrical Engineering", 1, "stest_s2"));
        
        ds.addCourse(new model.Course("QUOTA1", "Quota Test", 3, 1, "inst1"));
        ds.addCourse(new model.Course("STEST101", "Generic Course", 3, 10, "inst1"));
        
        check(ds.enrollStudent("stest_s1", "QUOTA1"), "First student should enroll");
        check(!ds.enrollStudent("stest_s2", "QUOTA1"), "Second student should be blocked by quota");
        
        // 3. Enrollment Status
        check(ds.isStudentEnrolled("stest_s1", "QUOTA1"), "Enrollment status should be verifiable");
        
        // 4. Major Restriction Check
        // stest_s1 is 'Computer Science' (mapped to CS). Try enrolling in EE101.
        ds.addCourse(new model.Course("EE101", "Circuit Analysis", 4, 40, "ahmet"));
        boolean crossMajor = ds.enrollStudent("stest_s1", "EE101");
        check(!crossMajor, "Student should be BLOCKED from enrolling in a course outside their major");
    }

    // -------------------------------------------------------------------------
    // GRADING & GPA TESTS
    // -------------------------------------------------------------------------
    private static void runGradingAndGPATests() {
        startModule("GRADING & GPA ENGINE");
        
        String user = "stest_grades";
        // Explicitly clean up before starting to ensure isolation
        ds.deleteUser(user); 
        ds.removeCourse("G_AA");
        ds.removeCourse("G_FF");

        ds.addUser(new User(user, "p", "Student", "Grader", "R", "CS"));
        ds.addCourse(new Course("G_AA", "AA Course", 3, 10, "p"));
        ds.addCourse(new Course("G_FF", "FF Course", 3, 10, "p"));
        
        ds.enrollStudent(user, "G_AA");
        ds.enrollStudent(user, "G_FF");
        
        // AA = 4.0
        ds.upsertGrade(user, "G_AA", 95, 95);
        double gpa1 = ds.calculateGPA(user);
        check(Math.abs(gpa1 - 4.0) < 0.01, "GPA for single AA should be 4.0");
        
        // FF = 0.0
        ds.upsertGrade(user, "G_FF", 10, 10);
        double combinedGpa = ds.calculateGPA(user);
        check(Math.abs(combinedGpa - 2.0) < 0.01, "GPA for AA(4.0) and FF(0.0) with equal credits should be 2.0");
    }

    // -------------------------------------------------------------------------
    // ADMIN REPORTING TESTS
    // -------------------------------------------------------------------------
    private static void runAdminReportingTests() {
        startModule("ADMIN REPORTING & DUAL EXPORT");
        
        // 1. Enrollment Count
        int initialCount = ds.getAllEnrollments().size();
        ds.enrollStudent("stest_report_user", "STEST101");
        int newCount = ds.getAllEnrollments().size();
        check(newCount == initialCount + 1, "Global enrollment count should increment correctly");
        
        // 2. Dual Transcript Export (TXT & PDF)
        String user = "stest_grades";
        java.io.File dir = new java.io.File("transcripts");
        if (!dir.exists()) dir.mkdirs();
        
        String txtPath = "transcripts/" + user + "_test.txt";
        String pdfPath = "transcripts/" + user + "_test.pdf";
        
        // Test TXT
        util.TranscriptExporter.export(user, txtPath);
        java.io.File txtFile = new java.io.File(txtPath);
        check(txtFile.exists() && txtFile.length() > 0, "TXT Transcript should be generated and non-empty");
        
        // Test PDF
        util.TranscriptExporter.export(user, pdfPath);
        java.io.File pdfFile = new java.io.File(pdfPath);
        check(pdfFile.exists() && pdfFile.length() > 0, "PDF Transcript should be generated and non-empty");
    }

    // -------------------------------------------------------------------------
    // VALIDATION UTILITY TESTS
    // -------------------------------------------------------------------------
    private static void runValidationUtilityTests() {
        startModule("INPUT VALIDATION UTILITY");
        
        // 1. Password Security
        check(InputValidator.validatePassword("123456") == null, "Valid 6-char password should pass");
        check(InputValidator.validatePassword("123") != null, "Short password should be rejected");
        
        // 2. Academic Constraints
        check(InputValidator.validateCredit("5") == null, "Valid credit (5) should pass");
        check(InputValidator.validateCredit("15") != null, "Invalid credit (15) should be rejected");
        check(InputValidator.validateQuota("0") != null, "Zero quota should be rejected");
        
        // 3. Course Code Pattern
        check(InputValidator.validateCourseCode("CS101") == null, "CS101 should pass");
        check(InputValidator.validateCourseCode("INVALID") != null, "Invalid course code format should be rejected");
    }

    // -------------------------------------------------------------------------
    // INSTRUCTOR VIEW TESTS
    // -------------------------------------------------------------------------
    private static void runInstructorViewTests() {
        startModule("INSTRUCTOR COURSE FILTERING");
        
        String prof1 = "stest_prof1";
        String prof2 = "stest_prof2";
        ds.removeCourse("C1");
        ds.removeCourse("C2");
        
        ds.addCourse(new Course("C1", "Course 1", 3, 10, prof1));
        ds.addCourse(new Course("C2", "Course 2", 3, 10, prof2));
        
        List<Course> p1Courses = ds.getCoursesByInstructor(prof1);
        check(p1Courses.size() == 1 && p1Courses.get(0).getCourseCode().equals("C1"), "Instructor 1 should ONLY see their own courses");
        
        List<Course> p2Courses = ds.getCoursesByInstructor(prof2);
        check(p2Courses.size() == 1 && p2Courses.get(0).getCourseCode().equals("C2"), "Instructor 2 should ONLY see their own courses");
        
        ds.removeCourse("C1");
        ds.removeCourse("C2");
    }

    // -------------------------------------------------------------------------
    // UTILS
    // -------------------------------------------------------------------------
    private static void startModule(String name) {
        System.out.println("\n▶ Module: " + name);
    }

    private static void check(boolean condition, String message) {
        totalTests++;
        if (condition) {
            System.out.println("  [PASS] " + message);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + message);
        }
    }

    private static void runCleanup() {
        System.out.println("\n♻ Cleaning up test data...");
        ds.deleteUser("stest_user");
        ds.deleteUser("stest_dual");
        ds.deleteUser("stest_grades");
        ds.deleteUser("stest_report_user");
        ds.deleteUser("stest_s1");
        ds.deleteUser("stest_s2");
        ds.removeCourse("STEST101");
        ds.removeCourse("QUOTA1");
        ds.removeCourse("G_AA");
        ds.removeCourse("G_FF");
        // Department CE left as it's a valid system addition
    }
}
