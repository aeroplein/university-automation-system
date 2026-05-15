# Manual Testing Guide: University Automation System

Follow these steps to verify that the UI and functional logic are working correctly after the modernization.

## 1. Appearance & Theme Verification
- [ ] **Palette Check**: Ensure the sidebar/headers are **Cocoa Brown** and buttons are **Sage Green**.
- [ ] **L&F Verification**: Check if the components have rounded corners (`arc: 8`) and modern fonts.
- [ ] **Login Screen**: Verify the login card is centered and background is **Warm Beige**.

## 2. Role-Based Access Control
- [ ] **Admin Login**: Login with an admin account. Verify you see 4 tabs (User, Student, Course, Reports).
- [ ] **Instructor Login**: Login with an instructor account. Verify you only see the Grade Entry dashboard.
- [ ] **Student Login**: Login with a student account. Verify you can see available courses and your transcript.

## 3. Core Functionality Tests
- [ ] **User Creation**: Go to Admin -> User Management. Add a new user. Verify they appear in the table instantly.
- [ ] **ID Generation**: Go to Admin -> Student Management. Click "Generate" for Student ID. Verify it creates a 9-digit ID.
- [ ] **Course Enrollment**: Login as a student. Enroll in a course. Logout and log back in to ensure the enrollment persisted.
- [ ] **Grade Entry**: Login as an instructor. Select a course and enter grades (e.g., 85 and 90). Verify the letter grade calculates as **AA** or **BA**.
- [ ] **Transcript Export**: As a student, view the transcript and verify the GPA updates based on entered grades.

## 4. Stability Check
- [ ] **Logout**: Click Logout from any dashboard. Verify you are returned to the login screen.
- [ ] **Persistence**: Close the app and reopen it. Verify all data added during testing is still there.
