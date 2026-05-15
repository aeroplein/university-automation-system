package data;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DataStore class manages all data persistence and business logic
 * Implements Singleton pattern to ensure single instance
 */
public class DataStore {
    private static DataStore instance;

    // Data storage directories
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.txt";
    private static final String STUDENTS_FILE = DATA_DIR + "/students.txt";
    private static final String COURSES_FILE = DATA_DIR + "/courses.txt";
    private static final String ENROLLMENTS_FILE = DATA_DIR + "/enrollments.txt";
    private static final String GRADES_FILE = DATA_DIR + "/grades.txt";
    private static final String DEPARTMENTS_FILE = DATA_DIR + "/departments.txt";
    private static final String FACULTIES_FILE = DATA_DIR + "/faculties.txt";
    private static final String CURRICULUM_FILE = DATA_DIR + "/curriculum.txt";

    // In-memory data structures
    private List<User> users;
    private List<StudentProfile> students;
    private List<Course> courses;
    private List<Enrollment> enrollments;
    private List<GradeRecord> grades;
    private List<Department> departments;
    private List<Faculty> faculties;
    private List<CurriculumMapping> curriculumMappings;

    /**
     * Private constructor for Singleton pattern
     */
    private DataStore() {
        users = new ArrayList<>();
        students = new ArrayList<>();
        courses = new ArrayList<>();
        enrollments = new ArrayList<>();
        grades = new ArrayList<>();
        departments = new ArrayList<>();
        faculties = new ArrayList<>();
        curriculumMappings = new ArrayList<>();
        initialize();
    }

    /**
     * Get the singleton instance of DataStore
     */
    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Initialize the data store by creating directory and loading data
     */
    public void initialize() {
        try {
            // Create data directory if it doesn't exist
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            // Load all data from files
            loadUsers();
            loadStudents();
            loadCourses();
            loadEnrollments();
            loadGrades();
            loadDepartments();
            loadFaculties();
            loadCurriculum();

            // Create default data if no users or departments exist
            if (users.isEmpty()) {
                createDefaultData();
            }
            if (departments.isEmpty()) {
                createDefaultDepartments();
            }
            if (faculties.isEmpty()) {
                createDefaultFaculties();
            }
            
            // Scalability: Ensure at least 25 students per department
            scaleData();
        } catch (Exception e) {
            System.err.println("Error initializing DataStore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create default data for testing
     */
    private void createDefaultData() {
        // Create default admin
        User admin = new User("admin", "admin123", "Admin", "System Administrator", "ADMIN001");
        users.add(admin);
        saveUsers();
        System.out.println("Default admin created: username=admin, password=admin123");
    }

    private void createDefaultDepartments() {
        // Engineering
        addDepartment(new Department("CS", "Computer Science", "ENG"));
        addDepartment(new Department("EE", "Electrical Engineering", "ENG"));
        addDepartment(new Department("ME", "Mechanical Engineering", "ENG"));
        addDepartment(new Department("IE", "Industrial Engineering", "ENG"));
        addDepartment(new Department("CEN", "Computer Engineering", "ENG"));
        
        // Science
        addDepartment(new Department("MATH", "Mathematics", "SCI"));
        addDepartment(new Department("PHYS", "Physics", "SCI"));
        
        // Business
        addDepartment(new Department("BA", "Business Administration", "BUS"));
        addDepartment(new Department("ECON", "Economics", "BUS"));
        
        // Arts & Humanities
        addDepartment(new Department("PSY", "Psychology", "ART"));
        addDepartment(new Department("SOC", "Sociology", "ART"));
        
        // Law (New Faculty)
        addFaculty(new Faculty("LAW", "Faculty of Law"));
        addDepartment(new Department("LAW", "Law", "LAW"));
        addDepartment(new Department("INTL", "International Law", "LAW"));
        
        saveDepartments();
    }

    // ==================== Department Management ====================
    public List<Department> getDepartments() {
        return new ArrayList<>(departments);
    }

    public Department findDepartment(String code) {
        if (code == null) return null;
        return departments.stream()
                .filter(d -> d.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

    public boolean addDepartment(Department dept) {
        if (dept == null || dept.getCode() == null || dept.getCode().trim().isEmpty()) return false;
        if (findDepartment(dept.getCode()) != null) return false;
        departments.add(dept);
        saveDepartments();
        return true;
    }

    /**
     * Update department. Returns: 1 if updated, 0 if no changes, -1 if error/not found.
     */
    public int updateDepartment(Department dept) {
        if (dept == null || dept.getCode() == null) return -1;
        Department existing = findDepartment(dept.getCode());
        if (existing == null) return -1;

        boolean changed = !existing.getName().equals(dept.getName()) || 
                         !existing.getFacultyCode().equals(dept.getFacultyCode());
        if (!changed) return 0;

        existing.setName(dept.getName());
        existing.setFacultyCode(dept.getFacultyCode());
        saveDepartments();
        return 1;
    }

    public boolean deleteDepartment(String code) {
        if (code == null) return false;
        boolean removed = departments.removeIf(d -> d.getCode().equalsIgnoreCase(code));
        if (removed) saveDepartments();
        return removed;
    }

    /**
     * Authenticate a user with username and password
     */
    public User authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    // ==================== User Management ====================

    /**
     * Find user by username
     */
    public User findUser(String username) {
        if (username == null) return null;
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Add a new user
     */
    public boolean addUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }

        // Check if username already exists
        if (findUser(user.getUsername()) != null) {
            return false;
        }

        users.add(user);
        saveUsers();
        return true;
    }

    /**
    /**
     * Update an existing user.
     * Returns: 1 if updated, 0 if no changes, -1 if not found.
     */
    public int updateUser(User user) {
        if (user == null || user.getUsername() == null) return -1;
        User existing = findUser(user.getUsername());
        if (existing == null) return -1;

        boolean changed = !existing.getPassword().equals(user.getPassword()) ||
                         !existing.getRole().equals(user.getRole()) ||
                         !existing.getFullName().equals(user.getFullName()) ||
                         (existing.getReferenceId() == null ? user.getReferenceId() != null : !existing.getReferenceId().equals(user.getReferenceId()));
        
        if (!changed) return 0;

        existing.setPassword(user.getPassword());
        existing.setRole(user.getRole());
        existing.setFullName(user.getFullName());
        existing.setReferenceId(user.getReferenceId());
        saveUsers();
        return 1;
    }

    /**
     * Delete a user by username
     */
    public boolean deleteUser(String username) {
        if (username == null || "admin".equalsIgnoreCase(username)) return false; // Prevent deleting main admin
        
        // Cascade delete associated student profile and records if they exist
        User user = findUser(username);
        if (user != null && "Student".equals(user.getRole())) {
            boolean profileRemoved = students.removeIf(s -> s.getUsername().equals(username));
            if (profileRemoved) saveStudents();
            
            boolean enrollmentsRemoved = enrollments.removeIf(e -> e.getStudentUsername().equals(username));
            if (enrollmentsRemoved) saveEnrollments();
            
            boolean gradesRemoved = grades.removeIf(g -> g.getStudentUsername().equals(username));
            if (gradesRemoved) saveGrades();
        }

        boolean removed = users.removeIf(u -> u.getUsername().equals(username));
        if (removed) saveUsers();
        return removed;
    }

    /**
     * Get all users
     */
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    // ==================== Student Management ====================

    /**
     * Find student profile by username
     */
    public StudentProfile findStudentProfileByUsername(String username) {
        if (username == null) return null;
        return students.stream()
                .filter(s -> s.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find student profile by student ID
     */
    public StudentProfile findStudentProfileById(String studentId) {
        if (studentId == null) return null;
        return students.stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Add a new student profile
     */
    public boolean addStudentProfile(StudentProfile student) {
        if (student == null || student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            return false;
        }

        // Check if student ID already exists
        if (findStudentProfileById(student.getStudentId()) != null) {
            return false;
        }

        // Check if username already exists
        if (findStudentProfileByUsername(student.getUsername()) != null) {
            return false;
        }

        students.add(student);
        saveStudents();
        return true;
    }

    /**
     * Updates an existing student profile.
     * Returns: 1 if updated, 0 if no changes, -1 if error or not found.
     */
    public int updateStudentProfile(StudentProfile updatedProfile) {
        if (updatedProfile == null) return -1;
        
        StudentProfile existing = findStudentProfileById(updatedProfile.getStudentId());
        if (existing == null) return -1;

        // Seniority Check
        if (updatedProfile.getSecondDepartment() != null && !updatedProfile.getSecondDepartment().isEmpty()) {
            if (updatedProfile.getYear() < updatedProfile.getSecondYear()) return -1;
        }

        boolean changed = !existing.getFullName().equals(updatedProfile.getFullName()) ||
                         !existing.getDepartment().equals(updatedProfile.getDepartment()) ||
                         existing.getYear() != updatedProfile.getYear() ||
                         (existing.getSecondDepartment() == null ? updatedProfile.getSecondDepartment() != null : !existing.getSecondDepartment().equals(updatedProfile.getSecondDepartment())) ||
                         existing.getSecondYear() != updatedProfile.getSecondYear() ||
                         !existing.getUsername().equals(updatedProfile.getUsername());

        if (!changed) return 0;

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(updatedProfile.getStudentId())) {
                students.set(i, updatedProfile);
                saveStudents();
                return 1;
            }
        }
        return -1;
    }

    // For testing seniority specifically
    public boolean updateSenioritySafe(StudentProfile p) {
        return updateStudentProfile(p) == 1;
    }

    /**
     * Get all students
     */
    public List<StudentProfile> getStudents() {
        return new ArrayList<>(students);
    }

    /**
     * Remove a student profile
     */
    public boolean removeStudentProfile(String username) {
        if (username == null) return false;
        boolean removed = students.removeIf(s -> s.getUsername().equals(username));
        if (removed) saveStudents();
        return removed;
    }

    // ==================== Course Management ====================

    /**
     * Find course by course code
     */
    public Course findCourse(String courseCode) {
        if (courseCode == null) return null;
        return courses.stream()
                .filter(c -> c.getCourseCode().equals(courseCode))
                .findFirst()
                .orElse(null);
    }

    /**
     * Add a new course
     */
    public boolean addCourse(Course course) {
        if (course == null || course.getCourseCode() == null || course.getCourseCode().trim().isEmpty()) {
            return false;
        }

        // Check if course code already exists
        if (findCourse(course.getCourseCode()) != null) {
            return false;
        }

        courses.add(course);
        saveCourses();
        return true;
    }

    /**
     * Remove a course
     */
    public boolean removeCourse(String courseCode) {
        if (courseCode == null) return false;
        boolean removed = courses.removeIf(c -> c.getCourseCode().equals(courseCode));
        if (removed) {
            saveCourses();
            // Cleanup enrollments and grades for this course
            enrollments.removeIf(e -> e.getCourseCode().equals(courseCode));
            saveEnrollments();
            grades.removeIf(g -> g.getCourseCode().equals(courseCode));
            saveGrades();
        }
        return removed;
    }

    /**
     * Get all courses
     */
    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    /**
     * Get courses by instructor username
     */
    public List<Course> getCoursesByInstructor(String instructorUsername) {
        if (instructorUsername == null) return new ArrayList<>();
        return courses.stream()
                .filter(c -> c.getInstructorUsername().equals(instructorUsername))
                .collect(Collectors.toList());
    }

    // ==================== Enrollment Management ====================

    /**
     * Count enrollments for a course
     */
    public int countEnrollmentForCourse(String courseCode) {
        if (courseCode == null) return 0;
        return (int) enrollments.stream()
                .filter(e -> e.getCourseCode().equals(courseCode))
                .count();
    }

    /**
     * Check if student is already enrolled in a course
     */
    public boolean isStudentEnrolled(String studentUsername, String courseCode) {
        if (studentUsername == null || courseCode == null) return false;
        return enrollments.stream()
                .anyMatch(e -> e.getStudentUsername().equals(studentUsername) 
                        && e.getCourseCode().equals(courseCode));
    }

    /**
     * Enroll a student in a course
     */
    /**
     * Finds a department code by its full name or code
     */
    public String getDepartmentCodeByName(String nameOrCode) {
        if (nameOrCode == null || nameOrCode.trim().isEmpty()) return null;
        String search = nameOrCode.trim();
        return departments.stream()
                .filter(d -> d.getName().equalsIgnoreCase(search) || d.getCode().equalsIgnoreCase(search))
                .map(Department::getCode)
                .findFirst()
                .orElse(null);
    }

    public boolean enrollStudent(String studentUsername, String courseCode) {
        // Validate inputs
        if (studentUsername == null || courseCode == null) {
            return false;
        }

        // Check if course exists
        Course course = findCourse(courseCode);
        if (course == null) return false;

        // Check if course belongs to student's major(s)
        StudentProfile profile = findStudentProfileByUsername(studentUsername);
        if (profile != null) {
            String primaryCode = getDepartmentCodeByName(profile.getDepartment());
            String secondCode = getDepartmentCodeByName(profile.getSecondDepartment());
            
            boolean allowed = false;
            String prefix = courseCode.replaceAll("\\d.*", ""); // Extract prefix e.g. CS101 -> CS
            
            if (primaryCode != null && prefix.equalsIgnoreCase(primaryCode)) allowed = true;
            if (secondCode != null && prefix.equalsIgnoreCase(secondCode)) allowed = true;
            
            if (!allowed) return false;
        }

        // Check if already enrolled
        if (isStudentEnrolled(studentUsername, courseCode)) {
            return false;
        }

        // Check quota
        int currentEnrollments = countEnrollmentForCourse(courseCode);
        if (currentEnrollments >= course.getQuota()) {
            return false;
        }

        // Add enrollment
        Enrollment enrollment = new Enrollment(studentUsername, courseCode);
        enrollments.add(enrollment);
        saveEnrollments();
        return true;
    }

    /**
     * Get enrollments for a student
     */
    public List<Enrollment> getEnrollmentsByStudent(String studentUsername) {
        if (studentUsername == null) return new ArrayList<>();
        return enrollments.stream()
                .filter(e -> e.getStudentUsername().equals(studentUsername))
                .collect(Collectors.toList());
    }

    /**
     * Get enrollments for a course
     */
    public List<Enrollment> getEnrollmentsByCourse(String courseCode) {
        if (courseCode == null) return new ArrayList<>();
        return enrollments.stream()
                .filter(e -> e.getCourseCode().equals(courseCode))
                .collect(Collectors.toList());
    }

    /**
     * Get all enrollments in the system
     */
    public List<Enrollment> getAllEnrollments() {
        return new ArrayList<>(enrollments);
    }

    /**
     * Remove enrollment
     */
    public boolean removeEnrollment(String studentUsername, String courseCode) {
        if (studentUsername == null || courseCode == null) return false;
        
        boolean removed = enrollments.removeIf(e -> 
            e.getStudentUsername().equals(studentUsername) && 
            e.getCourseCode().equals(courseCode));
        
        if (removed) {
            saveEnrollments();
        }
        return removed;
    }

    // ==================== Grade Management ====================

    /**
     * Find grade record
     */
    public GradeRecord findGrade(String studentUsername, String courseCode) {
        if (studentUsername == null || courseCode == null) return null;
        return grades.stream()
                .filter(g -> g.getStudentUsername().equals(studentUsername) 
                        && g.getCourseCode().equals(courseCode))
                .findFirst()
                .orElse(null);
    }

    /**
     * Insert or update grade
     */
    public boolean upsertGrade(String studentUsername, String courseCode, double midterm, double finalExam) {
        // Validate inputs
        if (studentUsername == null || courseCode == null) {
            return false;
        }

        if (midterm < 0 || midterm > 100 || finalExam < 0 || finalExam > 100) {
            return false;
        }

        // Check if student is enrolled in the course
        if (!isStudentEnrolled(studentUsername, courseCode)) {
            return false;
        }

        // Find existing grade
        GradeRecord existing = findGrade(studentUsername, courseCode);

        if (existing != null) {
            // Update existing grade
            existing.setMidterm(midterm);
            existing.setFinalExam(finalExam);
        } else {
            // Create new grade record
            GradeRecord newGrade = new GradeRecord(studentUsername, courseCode, midterm, finalExam);
            grades.add(newGrade);
        }

        saveGrades();
        return true;
    }

    /**
     * Get all grades in the system
     */
    public List<GradeRecord> getAllGrades() {
        return new ArrayList<>(grades);
    }

    /**
     * Get grades for a student
     */
    public List<GradeRecord> getGradesByStudent(String studentUsername) {
        if (studentUsername == null) return new ArrayList<>();
        return grades.stream()
                .filter(g -> g.getStudentUsername().equals(studentUsername))
                .collect(Collectors.toList());
    }

    /**
     * Get grades for a course
     */
    public List<GradeRecord> getGradesByCourse(String courseCode) {
        if (courseCode == null) return new ArrayList<>();
        return grades.stream()
                .filter(g -> g.getCourseCode().equals(courseCode))
                .collect(Collectors.toList());
    }

    /**
     * Calculate GPA for a student
     */
    public double calculateGPA(String studentUsername) {
        List<GradeRecord> studentGrades = getGradesByStudent(studentUsername);
        
        if (studentGrades.isEmpty()) {
            return 0.0;
        }

        double totalPoints = 0.0;
        int totalCredits = 0;

        for (GradeRecord grade : studentGrades) {
            Course course = findCourse(grade.getCourseCode());
            if (course != null) {
                totalPoints += grade.getGradePoint() * course.getCredit();
                totalCredits += course.getCredit();
            }
        }

        if (totalCredits == 0) {
            return 0.0;
        }

        return totalPoints / totalCredits;
    }

    // ==================== File I/O Operations ====================

    /**
     * Save users to file
     */
    public void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                writer.write(user.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    /**
     * Load users from file
     */
    public void loadUsers() {
        users.clear();
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = User.fromFileString(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    /**
     * Save students to file
     */
    public void saveStudents() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENTS_FILE))) {
            for (StudentProfile student : students) {
                writer.write(student.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving students: " + e.getMessage());
        }
    }

    /**
     * Load students from file
     */
    public void loadStudents() {
        students.clear();
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                StudentProfile student = StudentProfile.fromFileString(line);
                if (student != null) {
                    students.add(student);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading students: " + e.getMessage());
        }
    }

    /**
     * Save courses to file
     */
    public void saveCourses() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COURSES_FILE))) {
            for (Course course : courses) {
                writer.write(course.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving courses: " + e.getMessage());
        }
    }

    /**
     * Load courses from file
     */
    public void loadCourses() {
        courses.clear();
        File file = new File(COURSES_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Course course = Course.fromFileString(line);
                if (course != null) {
                    courses.add(course);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading courses: " + e.getMessage());
        }
    }

    /**
     * Save enrollments to file
     */
    public void saveEnrollments() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENROLLMENTS_FILE))) {
            for (Enrollment enrollment : enrollments) {
                writer.write(enrollment.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving enrollments: " + e.getMessage());
        }
    }

    /**
     * Load enrollments from file
     */
    public void loadEnrollments() {
        enrollments.clear();
        File file = new File(ENROLLMENTS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Enrollment enrollment = Enrollment.fromFileString(line);
                if (enrollment != null) {
                    enrollments.add(enrollment);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading enrollments: " + e.getMessage());
        }
    }

    /**
     * Save grades to file
     */
    public void saveGrades() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GRADES_FILE))) {
            for (GradeRecord grade : grades) {
                writer.write(grade.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving grades: " + e.getMessage());
        }
    }

    /**
     * Load grades from file
     */
    public void loadGrades() {
        grades.clear();
        File file = new File(GRADES_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                GradeRecord grade = GradeRecord.fromFileString(line);
                if (grade != null) {
                    grades.add(grade);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading grades: " + e.getMessage());
        }
    }

    /**
     * Save departments to file
     */
    public void saveDepartments() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DEPARTMENTS_FILE))) {
            for (Department dept : departments) {
                writer.write(dept.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving departments: " + e.getMessage());
        }
    }

    /**
     * Load departments from file
     */
    public void loadDepartments() {
        departments.clear();
        File file = new File(DEPARTMENTS_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Department dept = Department.fromFileString(line);
                if (dept != null) departments.add(dept);
            }
        } catch (IOException e) {
            System.err.println("Error loading departments: " + e.getMessage());
        }
    }

    public void createDefaultFaculties() {
        addFaculty(new Faculty("ENG", "Faculty of Engineering"));
        addFaculty(new Faculty("SCI", "Faculty of Science"));
        addFaculty(new Faculty("BUS", "Faculty of Business"));
        addFaculty(new Faculty("ART", "Faculty of Arts & Humanities"));
    }

    private void loadFaculties() {
        faculties.clear();
        File file = new File(FACULTIES_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Faculty f = Faculty.fromFileString(line);
                if (f != null) faculties.add(f);
            }
        } catch (IOException e) {
            System.err.println("Error loading faculties: " + e.getMessage());
        }
    }

    private void saveFaculties() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FACULTIES_FILE))) {
            for (Faculty f : faculties) {
                writer.write(f.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving faculties: " + e.getMessage());
        }
    }

    public List<Faculty> getFaculties() { return faculties; }

    public boolean addFaculty(Faculty f) {
        if (findFaculty(f.getCode()) != null) return false;
        faculties.add(f);
        saveFaculties();
        return true;
    }

    /**
     * Update faculty. Returns: 1 if updated, 0 if no changes, -1 if error/not found.
     */
    public int updateFaculty(Faculty f) {
        if (f == null || f.getCode() == null) return -1;
        Faculty existing = findFaculty(f.getCode());
        if (existing == null) return -1;

        boolean changed = !existing.getName().equals(f.getName());
        if (!changed) return 0;

        existing.setName(f.getName());
        saveFaculties();
        return 1;
    }

    public Faculty findFaculty(String code) {
        for (Faculty f : faculties) if (f.getCode().equals(code)) return f;
        return null;
    }

    public boolean deleteFaculty(String code) {
        if (code == null) return false;
        boolean removed = faculties.removeIf(f -> f.getCode().equalsIgnoreCase(code));
        if (removed) saveFaculties();
        return removed;
    }

    private void loadCurriculum() {
        curriculumMappings.clear();
        File file = new File(CURRICULUM_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                CurriculumMapping m = CurriculumMapping.fromFileString(line);
                if (m != null) curriculumMappings.add(m);
            }
        } catch (IOException e) {
            System.err.println("Error loading curriculum: " + e.getMessage());
        }
    }

    private void saveCurriculum() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CURRICULUM_FILE))) {
            for (CurriculumMapping m : curriculumMappings) {
                writer.write(m.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving curriculum: " + e.getMessage());
        }
    }

    public List<CurriculumMapping> getCurriculumMappings() { return curriculumMappings; }

    public boolean addCurriculumMapping(CurriculumMapping m) {
        // Check for duplicates
        boolean exists = curriculumMappings.stream().anyMatch(existing -> 
            existing.getDepartmentCode().equals(m.getDepartmentCode()) &&
            existing.getYear() == m.getYear() &&
            existing.getCourseCode().equals(m.getCourseCode())
        );
        
        if (exists) return false;
        
        curriculumMappings.add(m);
        saveCurriculum();
        return true;
    }

    public void removeCurriculumMapping(String dept, int year, String course) {
        curriculumMappings.removeIf(m -> m.getDepartmentCode().equals(dept) && 
                                       m.getYear() == year && 
                                       m.getCourseCode().equals(course));
        saveCurriculum();
    }

    public List<CurriculumMapping> getCurriculumByDeptAndYear(String dept, int year) {
        List<CurriculumMapping> result = new ArrayList<>();
        for (CurriculumMapping m : curriculumMappings) {
            if (m.getDepartmentCode().equals(dept) && m.getYear() == year) {
                result.add(m);
            }
        }
        return result;
    }

    public List<String> getCurriculumCourseCodesForStudent(String username) {
        StudentProfile profile = findStudentProfileByUsername(username);
        if (profile == null) return new ArrayList<>();

        List<String> codes = new ArrayList<>();
        String deptCode = getDepartmentCodeByName(profile.getDepartment());
        if (deptCode != null) {
            for (CurriculumMapping m : getCurriculumByDeptAndYear(deptCode, profile.getYear())) {
                codes.add(m.getCourseCode());
            }
        }

        // Also check second major
        String secondDeptCode = getDepartmentCodeByName(profile.getSecondDepartment());
        if (secondDeptCode != null) {
            for (CurriculumMapping m : getCurriculumByDeptAndYear(secondDeptCode, profile.getSecondYear())) {
                codes.add(m.getCourseCode());
            }
        }

        return codes;
    }

    /**
     * Scales the university data to meet professional thresholds (20-40 students per dept)
     */
    private void scaleData() {
        Random rand = new Random();
        String[] firstNames = {"Ahmet", "Mehmet", "Ayşe", "Fatma", "Can", "Ece", "Burak", "Deniz", "Emre", "Selin", "Mert", "Zeynep", "Arda", "Pelin", "Kerem", "Gizem", "Oğuz", "Beren", "Kaan", "Duru"};
        String[] lastNames = {"Yılmaz", "Kaya", "Demir", "Çelik", "Öztürk", "Arslan", "Doğan", "Kılıç", "Aydın", "Yıldız", "Özkan", "Şahin", "Polat", "Güneş", "Bulut", "Yavuz", "Aksoy"};

        boolean dataChanged = false;

        for (Department d : departments) {
            long studentCount = students.stream().filter(s -> s.getDepartment().equals(d.getName())).count();
            if (studentCount < 25) {
                int toAdd = (int) (25 + rand.nextInt(11) - studentCount);
                for (int i = 0; i < toAdd; i++) {
                    String fname = firstNames[rand.nextInt(firstNames.length)];
                    String lname = lastNames[rand.nextInt(lastNames.length)];
                    String username = fname.toLowerCase() + "." + lname.toLowerCase() + rand.nextInt(10000);
                    int year = rand.nextInt(4) + 1;
                    int entranceYear = 2025 - year + 1;
                    String sid = util.InputValidator.generateStudentId(entranceYear, d.getCode());
                    
                    User u = new User(username, "student123", "STUDENT", fname + " " + lname, sid);
                    if (addUser(u)) {
                        addStudentProfile(new StudentProfile(sid, u.getFullName(), d.getName(), year, username));
                        dataChanged = true;
                    }
                }
            }
            
            // Ensure 4 instructors per dept
            long instructorCount = users.stream().filter(u -> "INSTRUCTOR".equals(u.getRole()) && u.getReferenceId() != null && u.getReferenceId().startsWith(d.getCode())).count();
            if (instructorCount < 4) {
                for (int i = (int)instructorCount; i < 4; i++) {
                    String fname = firstNames[rand.nextInt(firstNames.length)];
                    String lname = lastNames[rand.nextInt(lastNames.length)];
                    String username = "prof." + fname.toLowerCase() + "." + (100 + rand.nextInt(900));
                    User u = new User(username, "pass123", "INSTRUCTOR", fname + " " + lname, d.getCode() + "I" + (i+1));
                    addUser(u);
                    dataChanged = true;
                }
            }
        }
        
        if (dataChanged) {
            saveUsers();
            saveStudents();
        }
    }

    /**
     * Calculates the credit limit for a student based on academic performance and status
     */
    public int calculateCreditLimit(String username) {
        StudentProfile profile = findStudentProfileByUsername(username);
        if (profile == null) return 30; // Default

        // 1. Double Major check (Highest priority)
        if (profile.getSecondDepartment() != null && !profile.getSecondDepartment().isEmpty() && profile.getSecondYear() > 0) {
            return 45; // Double major limit
        }

        // 2. GPA check
        double gpa = calculateGPA(username);
        if (gpa >= 3.5) return 40;
        if (gpa >= 3.0) return 36;
        
        // 3. Senior student check
        if (profile.getYear() == 4) return 36;

        return 30; // Standard limit
    }

    private double calculateGPA(String username) {
        List<GradeRecord> studentGrades = grades.stream()
            .filter(g -> g.getStudentUsername().equals(username))
            .collect(Collectors.toList());
        if (studentGrades.isEmpty()) return 0.0;
        
        double total = studentGrades.stream().mapToDouble(GradeRecord::getGradeValue).sum();
        return total / studentGrades.size() / 25.0; // Assuming 0-100 scale to 0-4
    }
}
