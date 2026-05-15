# Quick Reference Guide

## 🚀 Quick Start

```bash
# Linux/Mac
./run.sh

# Windows
run.bat

# Manual
javac -d bin -cp "lib/*" -sourcepath src src/ui/UniversityAutomationApp.java
java -cp "bin;lib/*" ui.UniversityAutomationApp
```

**Default Login**: `admin` / `admin123`

## 📋 Common Operations Cheat Sheet

### Admin Tasks

#### Create Complete Student Account

```
1. User Management → Add User
   - Username: student1
   - Password: pass123
   - Role: Student
   - Full Name: John Doe
   - Reference ID: STU001

2. Student Management → Add Student Profile
   - Student ID: 2024001
   - Full Name: John Doe
   - Department: Computer Science
   - Year: 1
   - Username: student1
```

#### Create Instructor and Course

```
1. User Management → Add User
   - Username: prof_jones
   - Password: prof123
   - Role: Instructor
   - Full Name: Professor Jones
   - Reference ID: INST001

2. Course Management → Add Course
   - Course Code: CS101
   - Course Name: Intro to CS
   - Credit: 3
   - Quota: 30
   - Instructor: prof_jones
```

### Instructor Tasks

#### Grade Students

```
1. Login as instructor
2. Grade Entry tab
3. Select course
4. Load Students
5. Enter grades (0-100)
6. Save Grades
```

### Student Tasks

#### Enroll and View Grades

```
1. Login as student
2. Available Courses → Select → Enroll
3. My Courses → View enrollments
4. Transcript → View GPA and grades
```

## 🎓 Grading Scale Quick Reference

| Grade | Range  | Points |
| ----- | ------ | ------ |
| AA    | 90-100 | 4.0    |
| BA    | 85-89  | 3.5    |
| BB    | 80-84  | 3.0    |
| CB    | 75-79  | 2.5    |
| CC    | 70-74  | 2.0    |
| DC    | 65-69  | 1.5    |
| DD    | 60-64  | 1.0    |
| FD    | 50-59  | 0.5    |
| FF    | 0-49   | 0.0    |

**Average Formula**: Midterm × 40% + Final × 60%

**GPA Formula**: Σ(Points × Credits) / Σ(Credits)

## ⚠️ Validation Rules

| Field    | Rule                 |
| -------- | -------------------- |
| Username | Required, unique     |
| Password | Minimum 6 characters |
| Grades   | 0-100 range          |
| Credits  | 1-10 range           |
| Quota    | 1-500 range          |

## 🗂️ File Locations

```
data/
├── users.txt         # All user accounts
├── students.txt      # Student profiles
├── courses.txt       # Course definitions
├── enrollments.txt   # Student enrollments
└── grades.txt        # Grade records
```

## 🔧 Common Issues

### "User already exists"

→ Username must be unique. Choose a different username.

### "Username does not exist"

→ Create user account first in User Management.

### "Course is full"

→ Quota reached. Admin can increase quota or create new section.

### "Password too short"

→ Use at least 6 characters.

### "Invalid grade format"

→ Enter numbers between 0-100 only.

## 📊 Sample Test Data

### Test Scenario: Complete University Setup

```
# Admin: admin / admin123 (default)

# Instructors
prof_smith / secure123 / Instructor / Dr. Smith / INST001
prof_jones / secure123 / Instructor / Dr. Jones / INST002

# Students  
alice_j / pass123 / Student / Alice Johnson / STU001
bob_s / pass123 / Student / Bob Smith / STU002
carol_w / pass123 / Student / Carol White / STU003

# Student Profiles
2024001 / Alice Johnson / Computer Science / 1 / alice_j
2024002 / Bob Smith / Engineering / 2 / bob_s
2024003 / Carol White / Mathematics / 3 / carol_w

# Courses
CS101 / Intro to Programming / 3 / 30 / prof_smith
CS201 / Data Structures / 4 / 25 / prof_smith
MATH101 / Calculus I / 4 / 40 / prof_jones
ENG101 / Engineering Basics / 3 / 35 / prof_jones
```

## 🎯 Testing Checklist

Quick validation before submission:

- [ ] Login works (admin/admin123)
- [ ] Can create users, students, courses
- [ ] Tables display data correctly
- [ ] Enrollment works with quota checking
- [ ] Grades can be entered and saved
- [ ] GPA calculates correctly
- [ ] Data persists after restart
- [ ] Validation prevents invalid inputs
- [ ] All buttons are responsive
- [ ] Logout returns to login screen

## 📞 Support

For issues or questions:

1. Check README.md for detailed documentation
2. Review TESTING_GUIDE.md for test cases
3. Verify data files exist in data/ directory
4. Check console output for error messages

## 🏆 Rubric Quick Check

| Area          | Points | Check                    |
| ------------- | ------ | ------------------------ |
| Architecture  | 15     | MVC, packages, singleton |
| OOP           | 15     | Encapsulation, classes   |
| GUI           | 15     | Professional design      |
| Events        | 10     | All buttons work         |
| Persistence   | 10     | Files save/load          |
| Functionality | 10     | All features work        |
| Validation    | 10     | Error handling           |
| JTable        | 5      | Multiple tables          |
| Code Quality  | 10     | Clean, readable          |

**Total**: 100 points

---

_Keep this guide handy for quick reference during development and testing!_
