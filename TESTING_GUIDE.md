# University Automation System - Testing Guide

This document provides comprehensive test cases to validate all functionality and ensure the system meets all project requirements.

## 🧪 Testing Overview

### Test Categories
1. Authentication & Authorization
2. Admin Operations
3. Instructor Operations
4. Student Operations
5. Data Persistence
6. Validation & Error Handling
7. UI Responsiveness
8. Advanced Features (Double Major & Custom Algorithms)

## ✅ Test Cases

### 1. Authentication & Authorization Tests

#### TC-1.1: Successful Login
**Precondition**: Default admin account exists
**Steps**:
1. Launch application
2. Enter username: `admin`
3. Enter password: `admin123`
4. Click "Login"

**Expected Result**: 
- Admin dashboard displays
- Welcome message shows "Welcome, System Administrator"
- Four tabs visible: User Management, Student Management, Course Management, System Reports

**Status**: ☐ Pass ☐ Fail

---

#### TC-1.2: Failed Login - Invalid Credentials
**Steps**:
1. Enter username: `invalid`
2. Enter password: `wrong`
3. Click "Login"

**Expected Result**:
- Error dialog: "Invalid username or password."
- User remains on login screen

**Status**: ☐ Pass ☐ Fail

---

#### TC-1.3: Failed Login - Empty Fields
**Steps**:
1. Leave username empty
2. Leave password empty
3. Click "Login"

**Expected Result**:
- Warning dialog: "Please enter both username and password."

**Status**: ☐ Pass ☐ Fail

---

#### TC-1.4: Logout Functionality
**Steps**:
1. Login as admin
2. Click "Logout" button

**Expected Result**:
- Success message: "You have been logged out successfully."
- Returns to login screen

**Status**: ☐ Pass ☐ Fail

---

### 2. Admin Operations Tests

#### TC-2.1: Add New Instructor User
**Precondition**: Logged in as admin
**Steps**:
1. Go to "User Management" tab
2. Enter username: `prof_smith`
3. Enter password: `secure123`
4. Select role: `Instructor`
5. Enter full name: `Professor John Smith`
6. Enter reference ID: `INST001`
7. Click "Add User"

**Expected Result**:
- Success message: "User added successfully!"
- New user appears in the user table
- Fields are cleared
- Data persists in data/users.txt

**Status**: ☐ Pass ☐ Fail

---

#### TC-2.2: Add Duplicate User
**Steps**:
1. Try to add user with username: `admin`
2. Fill other fields
3. Click "Add User"

**Expected Result**:
- Error message: "Username already exists."
- User not added to table

**Status**: ☐ Pass ☐ Fail

---

#### TC-2.3: Add User - Password Too Short
**Steps**:
1. Enter username: `test`
2. Enter password: `123` (only 3 chars)
3. Fill other fields
4. Click "Add User"

**Expected Result**:
- Warning: "Password must be at least 6 characters long."

**Status**: ☐ Pass ☐ Fail

---

#### TC-2.4: Add Student Profile
**Precondition**: Student user exists (created in user management)
**Steps**:
1. First create student user: `alice_j` / `password123` / `Student` / `Alice Johnson` / `STU001`
2. Go to "Student Management" tab
3. Enter student ID: `2024001`
4. Enter full name: `Alice Johnson`
5. Enter department: `Computer Science`
6. Select year: `1`
7. Enter username: `alice_j`
8. Click "Add Student"

**Expected Result**:
- Success message displayed
- Student appears in table
- Data saved to data/students.txt

**Status**: ☐ Pass ☐ Fail

---

#### TC-2.5: Add Student Profile - Invalid Username
**Steps**:
1. Try to create student profile with username: `nonexistent_user`

**Expected Result**:
- Error: "Username does not exist. Please create a user account first."

**Status**: ☐ Pass ☐ Fail

---

#### TC-2.6: Add Student Profile - Wrong Role
**Precondition**: Instructor user exists
**Steps**:
1. Try to create student profile with instructor username

**Expected Result**:
- Error: "The user must have 'Student' role."

**Status**: ☐ Pass ☐ Fail

---

#### TC-2.7: Add New Course
**Precondition**: Instructor user exists
**Steps**:
1. Go to "Course Management" tab
2. Enter course code: `CS101`
3. Enter course name: `Introduction to Programming`
4. Enter credit: `3`
5. Enter quota: `30`
6. Select instructor: `prof_smith`
7. Click "Add Course"

**Expected Result**:
- Success message
- Course appears in table with 0/30 enrollment
- Data saved to data/courses.txt

**Status**: ☐ Pass ☐ Fail

---

#### TC-2.8: Add Course - Invalid Credit
**Steps**:
1. Enter credit: `15` (exceeds maximum)
2. Fill other fields
3. Click "Add Course"

**Expected Result**:
- Warning: "Credit must be between 1 and 10."

**Status**: ☐ Pass ☐ Fail

---

#### TC-2.9: Add Course - Invalid Quota
**Steps**:
1. Enter quota: `0` (below minimum)
2. Fill other fields
3. Click "Add Course"

**Expected Result**:
- Warning: "Quota must be between 1 and 500."

**Status**: ☐ Pass ☐ Fail

---

#### TC-2.10: View System Reports
**Steps**:
1. Go to "System Reports" tab

**Expected Result**:
- Table displays statistics:
  - Total Users
  - Total Students
  - Total Courses
  - Total Enrollments
  - Admin Users count
  - Instructor Users count
  - Student Users count

**Status**: ☐ Pass ☐ Fail

---

### 3. Instructor Operations Tests

#### TC-3.1: View Assigned Courses
**Precondition**: Logged in as instructor with assigned courses
**Steps**:
1. Login as `prof_smith`
2. Go to "My Courses" tab

**Expected Result**:
- Table displays all courses assigned to this instructor
- Shows course code, name, credit, enrolled count, quota

**Status**: ☐ Pass ☐ Fail

---

#### TC-3.2: Load Students for Grading
**Precondition**: Course has enrolled students
**Steps**:
1. Go to "Grade Entry" tab
2. Select course from dropdown
3. Click "Load Students"

**Expected Result**:
- Table populates with enrolled students
- Shows username, name, midterm (0.0), final (0.0), average, letter grade

**Status**: ☐ Pass ☐ Fail

---

#### TC-3.3: Enter Valid Grades
**Steps**:
1. Load students for a course
2. Click on midterm cell for a student
3. Enter: `85`
4. Click on final cell
5. Enter: `90`
6. Click "Save Grades"

**Expected Result**:
- Success message: "Grades saved successfully!"
- Average updates to: 88.00 (85*0.4 + 90*0.6)
- Letter grade updates to: BA
- Data saved to data/grades.txt

**Status**: ☐ Pass ☐ Fail

---

#### TC-3.4: Enter Invalid Grade - Out of Range
**Steps**:
1. Load students
2. Enter midterm: `150` (exceeds 100)
3. Click "Save Grades"

**Expected Result**:
- Warning: "Grades must be between 0 and 100 for student: [username]"
- Grades not saved

**Status**: ☐ Pass ☐ Fail

---

#### TC-3.5: Enter Invalid Grade - Non-numeric
**Steps**:
1. Load students
2. Enter midterm: `abc` (text)
3. Click "Save Grades"

**Expected Result**:
- Warning: "Invalid grade format for student: [username]"

**Status**: ☐ Pass ☐ Fail

---

#### TC-3.6: Update Existing Grades
**Precondition**: Student already has grades
**Steps**:
1. Load students with existing grades
2. Modify midterm from 85 to 95
3. Click "Save Grades"

**Expected Result**:
- Grades update successfully
- Average recalculates: 93.00
- Letter grade updates: AA

**Status**: ☐ Pass ☐ Fail

---

### 4. Student Operations Tests

#### TC-4.1: View Available Courses
**Precondition**: Logged in as student
**Steps**:
1. Login as `alice_j`
2. Go to "Available Courses" tab

**Expected Result**:
- Table shows all courses in system
- Displays course code, name, credit, instructor, enrolled/quota

**Status**: ☐ Pass ☐ Fail

---

#### TC-4.2: Successful Course Enrollment
**Precondition**: Course has available quota
**Steps**:
1. In "Available Courses" tab
2. Select CS101 from table
3. Click "Enroll in Selected Course"

**Expected Result**:
- Success message: "Successfully enrolled in the course!"
- Enrollment count increases
- Data saved to data/enrollments.txt

**Status**: ☐ Pass ☐ Fail

---

#### TC-4.3: Duplicate Enrollment Prevention
**Steps**:
1. Try to enroll in CS101 again

**Expected Result**:
- Error: "Enrollment failed. Possible reasons: ... You are already enrolled in this course"

**Status**: ☐ Pass ☐ Fail

---

#### TC-4.4: Enrollment - Quota Full
**Precondition**: Course quota is reached
**Steps**:
1. Try to enroll in a course with 30/30 enrollment

**Expected Result**:
- Error message indicating quota is full

**Status**: ☐ Pass ☐ Fail

---

#### TC-4.5: View Enrolled Courses
**Steps**:
1. Go to "My Courses" tab

**Expected Result**:
- Table shows only courses student is enrolled in
- Displays course code, name, credit, instructor

**Status**: ☐ Pass ☐ Fail

---

#### TC-4.6: View Transcript Without Grades
**Precondition**: Enrolled but no grades yet
**Steps**:
1. Go to "Transcript" tab

**Expected Result**:
- GPA shows: 0.00 / 4.00
- Table is empty (no grades to display)

**Status**: ☐ Pass ☐ Fail

---

#### TC-4.7: View Transcript With Grades
**Precondition**: Has grades for enrolled courses
**Steps**:
1. Go to "Transcript" tab

**Expected Result**:
- GPA calculated correctly
- Table shows all courses with grades
- Displays: course code, name, credit, midterm, final, average, letter grade

**Status**: ☐ Pass ☐ Fail

---

#### TC-4.8: GPA Calculation Accuracy
**Test Data**:
- CS101 (3 credits): Midterm 80, Final 90 → Average 86 → BA (3.5 points)
- MATH101 (4 credits): Midterm 70, Final 80 → Average 76 → CB (2.5 points)

**Expected GPA**: 
- Total points: (3.5 × 3) + (2.5 × 4) = 10.5 + 10 = 20.5
- Total credits: 3 + 4 = 7
- GPA: 20.5 / 7 = 2.93

**Steps**:
1. Enroll in both courses
2. Instructor enters grades
3. Student views transcript

**Expected Result**:
- GPA displays as: 2.93 / 4.00

**Status**: ☐ Pass ☐ Fail

---

### 5. Data Persistence Tests

#### TC-5.1: Data Survives Application Restart
**Steps**:
1. Add a user, student, course, enrollment, and grade
2. Close the application
3. Reopen the application
4. Login and verify data

**Expected Result**:
- All data is still present
- No data loss occurred

**Status**: ☐ Pass ☐ Fail

---

#### TC-5.2: File Creation
**Steps**:
1. Check data directory after first run

**Expected Result**:
- Files created: users.txt, students.txt, courses.txt, enrollments.txt, grades.txt

**Status**: ☐ Pass ☐ Fail

---

#### TC-5.3: File Format Validation
**Steps**:
1. Open data/users.txt
2. Verify format

**Expected Result**:
- Format: `username,password,role,fullName,referenceId`
- Example: `admin,admin123,Admin,System Administrator,ADMIN001`

**Status**: ☐ Pass ☐ Fail

---

### 6. UI/UX Tests

#### TC-6.1: Table Selection
**Steps**:
1. Login as admin
2. Go to any tab with a table
3. Click on a row

**Expected Result**:
- Row highlights
- Single selection mode works

**Status**: ☐ Pass ☐ Fail

---

#### TC-6.2: Tab Navigation
**Steps**:
1. Click through all tabs in each role dashboard

**Expected Result**:
- All tabs load properly
- No errors or blank screens

**Status**: ☐ Pass ☐ Fail

---

#### TC-6.3: Button Responsiveness
**Steps**:
1. Click various buttons throughout the application

**Expected Result**:
- All buttons respond
- Appropriate actions trigger
- No unresponsive buttons

**Status**: ☐ Pass ☐ Fail

---

#### TC-6.4: Field Clearing After Submit
**Steps**:
1. Add a user
2. Check input fields after success

**Expected Result**:
- All fields are cleared/reset
- Ready for next entry

**Status**: ☐ Pass ☐ Fail

---

#### TC-6.5: Password Field Security
**Steps**:
1. Type in password field

**Expected Result**:
- Characters display as dots/asterisks
- Password is masked

**Status**: ☐ Pass ☐ Fail

---

#### TC-6.6: Enter Key Login
**Steps**:
1. Enter credentials
2. Press Enter key (without clicking Login button)

**Expected Result**:
- Login proceeds as if button was clicked

**Status**: ☐ Pass ☐ Fail

---

### 7. Edge Cases & Stress Tests

#### TC-7.1: Special Characters in Names
**Steps**:
1. Add user with name: `O'Brien-Smith, Jr.`

**Expected Result**:
- Name saved correctly
- No parsing errors
- Commas handled properly (semicolon substitution)

**Status**: ☐ Pass ☐ Fail

---

#### TC-7.2: Maximum Enrollment
**Steps**:
1. Create course with quota: 500
2. Enroll 500 students
3. Try to enroll 501st student

**Expected Result**:
- First 500 succeed
- 501st gets quota error

**Status**: ☐ Pass ☐ Fail

---

#### TC-7.3: Empty Data Files
**Steps**:
1. Delete all .txt files from data directory
2. Restart application

**Expected Result**:
- Application creates new files
- Default admin account created
- No crashes

**Status**: ☐ Pass ☐ Fail

---

#### TC-7.4: Concurrent Role Testing
**Steps**:
1. Login as admin, perform operations
2. Logout
3. Login as instructor, perform operations
4. Logout
5. Login as student, perform operations

**Expected Result**:
- All operations work correctly
- No permission errors
- Role-specific features available

**Status**: ☐ Pass ☐ Fail

---

### 8. Advanced Features (Double Major & Custom Algorithms)

#### TC-8.1: Double Major Assignment
**Precondition**: Student profile exists
**Steps**:
1. Login as admin
2. Go to "Student Management"
3. Select an existing student from the "Username" dropdown
4. Select a different department from the "Department" dropdown
5. Click the "Add Double Major" button (verify button text changed dynamically)

**Expected Result**:
- Success message: "Double Major added successfully!"
- Table shows dual enrollment: `Primary (Double Major: Secondary, Year: X)`
- Data persists with `secondDepartment` and `secondYear` fields

**Status**: ☐ Pass ☐ Fail

---

#### TC-8.2: Department-Specific ID Verification
**Steps**:
1. Go to "Student Management"
2. Select "Computer Science" (CS) and click "Generate"
3. Note the ID (verify it contains "11")
4. Select "Electrical Engineering" (EE) and click "Generate"
5. Note the ID (verify it contains "22")

**Expected Result**:
- IDs follow the deterministic department mapping (CS=11, EE=22, IE=33, ME=44, CE=55)

**Status**: ☐ Pass ☐ Fail

---

#### TC-8.3: Independent Year Progress
**Steps**:
1. Create a student as Year 3 in CS
2. Add a Double Major in EE as Year 1
3. Verify the table entry

**Expected Result**:
- Table shows: `CS (Double Major: EE, Year: 1)`
- Year column shows: `3`
- GPA calculation uses the primary username regardless of department

**Status**: ☐ Pass ☐ Fail

---

## 📊 Testing Summary

### Test Results Template

| Category | Total Tests | Passed | Failed | Pass Rate |
|----------|-------------|--------|--------|-----------|
| Authentication | 4 | | | |
| Admin Operations | 10 | | | |
| Instructor Operations | 6 | | | |
| Student Operations | 8 | | | |
| Data Persistence | 3 | | | |
| UI/UX | 6 | | | |
| Edge Cases | 4 | | | |
| Advanced Features | 3 | | | |
| **TOTAL** | **44** | | | |

---

## 🐛 Bug Report Template

**Bug ID**: [BUG-XXX]
**Test Case**: [TC-X.X]
**Severity**: Critical / High / Medium / Low
**Description**: 
**Steps to Reproduce**:
1. 
2. 
3. 
**Expected Result**:
**Actual Result**:
**Screenshots**: (if applicable)

---

## ✅ Acceptance Criteria Checklist

### Functional Requirements
- [ ] Login system with authentication
- [ ] Role-based dashboards (Admin, Instructor, Student)
- [ ] User management (Admin)
- [ ] Student profile management (Admin)
- [ ] Course management (Admin)
- [ ] System reports (Admin)
- [ ] View assigned courses (Instructor)
- [ ] Grade entry and update (Instructor)
- [ ] View available courses (Student)
- [ ] Course enrollment (Student)
- [ ] View enrolled courses (Student)
- [ ] View transcript with GPA (Student)

### Technical Requirements
- [ ] File-based data persistence
- [ ] Data survives application restart
- [ ] At least one JTable used (actually multiple)
- [ ] Input validation implemented
- [ ] ActionListener for buttons
- [ ] Proper event handling
- [ ] Exception handling
- [ ] Professional UI design

### Code Quality
- [ ] Clean code structure
- [ ] Proper naming conventions
- [ ] Package organization (model, data, ui)
- [ ] Encapsulation (private fields, getters/setters)
- [ ] Comments and documentation

---

## 🎯 Performance Metrics

- **Startup Time**: Application should start within 3 seconds
- **Response Time**: Button clicks should respond within 500ms
- **File I/O**: Save operations should complete within 1 second
- **Table Load**: Tables should populate within 1 second for 1000 records

---

**Testing Complete**: ☐ Yes ☐ No
**Ready for Submission**: ☐ Yes ☐ No
**Overall Quality**: ☐ Excellent ☐ Good ☐ Needs Improvement

---

*Remember: Thorough testing ensures a high-quality submission and demonstrates professional software development practices.*
