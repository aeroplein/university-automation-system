# 🎓 University Automation System: Presentation & Study Guide
This document serves as your **ultimate study companion and presentation script** for the University Automation System project. It contains a complete blueprint of the entire codebase: every package, class, method, data relationship, Swing component, and business rule. 

Use this guide to master the codebase, understand the flow of data, and confidently answer any questions your professors or peers may ask during your presentation.

---

## 🏛️ 1. Architecture & Design Patterns
Before diving into the classes, you must understand the high-level design of the system. Professors love architectural questions!

### A. Model-View-Controller (MVC) & Modular UI
*   **Model**: The classes in the `model` package (`User`, `StudentProfile`, `Course`, `GradeRecord`, etc.) represent the raw data. They do not know about the UI; they only hold state and simple calculations (e.g., GPA or letter grade).
*   **View & Controller (Hybrid)**: The `ui` package and its subpackages contain the Swing panels. They display the data (**View**) and handle user events like button clicks and dropdown changes (**Controller**), invoking the `DataStore` to save changes.
*   **Modular Decomposition**: Instead of a giant monolithic Swing file, each tab on the dashboard is extracted into its own independent `JPanel` subclass inside dedicated subpackages (`ui.admin`, `ui.instructor`, `ui.student`). This maintains the **Single Responsibility Principle**.

### B. Singleton Pattern (The DataStore)
*   **Problem**: In a desktop app, multiple UI tabs need to access and modify the same database. If every tab created a new instance of a data-handler, changes in the "Student Management" tab would not be reflected in the "Enrollment" tab.
*   **Solution**: The `DataStore` class implements a thread-safe private constructor and a public `getInstance()` method:
    ```java
    private static DataStore instance;
    public static DataStore getInstance() {
        if (instance == null) { instance = new DataStore(); }
        return instance;
    }
    ```
    This guarantees that there is **exactly one** central in-memory store throughout the entire application lifecycle.

### C. Refreshable Pattern (Observer-like Tab Synchronization)
*   **Problem**: When an Admin approves a student's enrollment request in the "Enrollment Management" tab, how does the "System Reports" tab know to update its "Total Enrolled Courses" statistic immediately?
*   **Solution**: The `Refreshable` interface in `ui.shared` exposes a single contract:
    ```java
    public interface Refreshable { void refresh(); }
    ```
    Every panel implements this interface. When a user switches tabs or clicks the global "Refresh" button on the dashboard, `UniversityAutomationApp` intercepts the event, detects if the active component is an instance of `Refreshable`, and invokes its `refresh()` method to pull the latest data from the `DataStore`.

### D. File-Based CSV Persistence Layer
*   Instead of a database, the system implements a lightweight, high-performance serialization layer. In-memory `ArrayList`s are serialized to and from comma-separated value (CSV) files (`.txt`) under the `data/` directory using standard `BufferedReader` and `BufferedWriter` streams. Special `toFileString()` and `fromFileString()` methods in each model handle parsing and CSV-splitting.

### E. Shared UI Skeleton & Design Standardizer (The ui.shared Framework)
*   **The Foundation**: Rather than having individual tabs dictate their own styling, the `ui.shared` package defines the global visual and structural parameters of the application.
*   **Behavioral Interface (`Refreshable`)**: Establishes a standard sync contract allowing independent dashboard tab components to refresh data dynamically on tab switch or on manual refresh.
*   **Visual Utility (`UIUtils`)**: Acts as a central design registry, housing key hex codes for the Earth-Tone Palette, custom `JTable` formatting logic, and standard search wrappers.

### F. Universal Panel Layout Skeleton & State Bindings (The Panel Boilerplate)
While each management panel (e.g., `UserManagementPanel`, `StructureManagementPanel`, `StudentManagementPanel`) manages distinct business entities, they all share an identical layout skeleton and logical flow tailored to their respective fields:

1. **Consistent Visual Wrapper (Theme & Margins)**
   * Every panel inherits from `JPanel` and sets a standardized layout: `setLayout(new BorderLayout(0, 15));`
   * Applies the background color: `setBackground(UIUtils.WARM_BEIGE);`
   * Adds outer margin padding: `setBorder(new EmptyBorder(15, 15, 15, 15));`

2. **Master-Detail Layout Structure**
   * **The Form (Detail Input Panel)**: A top-aligned `formPanel` using `GridBagLayout` with a titled border (`BorderFactory.createTitledBorder("...")`) hosting text fields, password fields, or combo boxes specific to that panel.
   * **The Action Controls (Buttons)**: An inline group of action buttons (Add, Update, Delete) styled with uniform semantic colors (Sage Green, Sky Blue, Terracotta).
   * **The Search Wrapper**: A center-aligned layout that dynamically embeds a search text bar styled via `UIUtils.setupSearch(...)`.
   * **The Table (Master List)**: A table styled with `UIUtils.createModernTable(...)` nested inside a scroll pane (`JScrollPane`) to show database records.

3. **Bidirectional State Synchronization Logic**
   * **Table-to-Form (Selection Binding)**: Clicking a row in the table fires a `ListSelectionListener`. The panel captures the view index, converts it to the model index (`convertRowIndexToModel` to support sorted views), and auto-populates the input form fields with the selected entity's data.
   * **Form-to-DataStore (Mutation)**: Filling form inputs and clicking buttons triggers action listener logic. It validates format patterns (via `InputValidator`), invokes database operations on the singleton `DataStore`, and executes view updates.

4. **Smart Selection Preservation Lifecycle on Refresh**
   * Every panel implements a robust, three-step state recovery procedure inside `refresh()` to maintain a seamless user experience:
     1. **Cache Selection**: Before clearing the table, it retrieves and caches the unique model identifier (e.g., `username`, `courseCode`) of the currently selected record.
     2. **Repopulate Model**: Clears table model rows and loads fresh datasets from the `DataStore`.
     3. **Restore Selection**: Iterates through the newly populated rows. When it matches the cached identifier, it highlights that row and safely converts the model index back to the active view index (`convertRowIndexToView`) so the user's active focus remains intact even if they have applied custom table sorting!

---

## 📂 2. Directory Structure & Package Map
Here is how the project is organized. You can draw this on a slide!

```text
src/
├── data/                    <-- Persistence Logic (Singleton Database)
│   └── DataStore.java
├── model/                   <-- Raw Domain Models (Data Containers)
│   ├── User.java, StudentProfile.java, Course.java, GradeRecord.java
│   ├── Department.java, Faculty.java, CurriculumMapping.java
│   └── Enrollment.java
├── ui/                      <-- Front-End Main Frame & Dashboards
│   ├── UniversityAutomationApp.java
│   ├── admin/               <-- Administrator Tab Panels (CRUD & Reports)
│   ├── instructor/          <-- Instructor Tab Panels (Grades & Custom Charts)
│   ├── student/             <-- Student Tab Panels (Enrollment & Transcript)
│   └── shared/              <-- Reusable UI Utilities & Sync Interfaces
├── util/                    <-- Helper libraries (Algorithms, Validators & Exporters)
│   ├── InputValidator.java
│   └── TranscriptExporter.java
└── test/                    <-- QA & Compilation Verification Tests
    ├── ProjectValidationTest.java
    └── ComprehensiveSystemTest.java
```

---

## 🔍 3. Complete Deep-Dive Package, Class & Method Guide

Let's dissect **every single class** and **every single method** in the project, detailing their internal Swing components and operations.

### 📦 Package `ui.shared` (The Core UI Skeleton & Customization Framework)
This package houses the foundational UI skeleton utilities, visual color palettes, search layout standardizers, and synchronization contracts that unite all modular panels across the workspace.

#### 📄 Class `Refreshable.java` (Interface)
*   **Purpose**: Contract interface that allows panels to receive standardized lifecycle update events.
*   **Methods**:
    *   `void refresh()`: Re-reads the updated list of items from the `DataStore` and rebuilds the visual table rows or lists.

#### 📄 Class `UIUtils.java` (Visual & Behavior Customizer)
*   **Purpose**: Enforces visual coherence with the unified **UniAuto Earth-Tone Palette** and styles complex components like JTables.
*   **Palette Colors (Constants)**:
    *   `COCOA_BROWN` (`0x2D241E`): Dark headings, headers, sidebars.
    *   `SAGE_GREEN` (`0x7C9070`): Success actions, confirm buttons, selections.
    *   `WARM_BEIGE` (`0xFDFBF7`): Central panel backgrounds (premium cream look).
    *   `CREAM_SAND` (`0xE9EDDF`): Contrast fields, forms, container backdrops.
    *   `TERRACOTTA` (`0xD98880`): Deletions, rejects, cancellations.
    *   `SKY_BLUE` (`0x5D9CEC`): Reports, updates, exports.
*   **Methods**:
    *   `static JTable createModernTable(DefaultTableModel model)`: Returns a pre-styled `JTable` with custom line heights (30px), Segoe UI bold headers, left-aligned cell renderers, and custom inner horizontal padding.
    *   `static void setupSearch(JPanel container, DefaultTableModel model, JTable table)`: Dynamically appends a right-aligned search text field in a wrapper to `container`. Instantiates a `TableRowSorter` bound to `table`. Adds a `CaretListener` that applies a case-insensitive regular expression filter (`(?i)text`) in real-time as the user types.

---

### 📦 Package `ui` (Global Navigation & Main Entrance)

#### 📄 Class `UniversityAutomationApp.java` (JFrame)
*   **Purpose**: The central window container and application bootstrapper. Manages login card and role-based dashboard compilation.
*   **Swing Components**:
    *   `JFrame` (Main frame container).
    *   `CardLayout` (Switches between `LOGIN` panel and `DASHBOARD` panel).
    *   `JTabbedPane` (Hosts the modular tabs relative to the user's role).
    *   `JTextField`, `JPasswordField`, `JButton` (For login inputs).
    *   `JLabel` (Title labels and welcome indicators).
*   **Methods**:
    *   `UniversityAutomationApp()` (Constructor): Retrieves the `DataStore` singleton instance, applies global UI metrics in `setupGlobalUI()`, and sets up the window in `initializeUI()`.
    *   `private void setupGlobalUI()`: Initializes `FlatIntelliJLaf` look-and-feel. Sets rounded arc styling for buttons (`Button.arc=5`) and inputs (`Component.arc=5`). Configures selection highlight colors using `UIManager.put()`.
    *   `private void initializeUI()`: Standardizes JFrame dimensions (1100x750), default close actions, centers on screen, and registers the Login panel inside the `CardLayout`.
    *   `private JPanel showLoginPanel()`: Creates a grid-based login interface using `GridBagLayout`. Attaches action listeners to the "Login" button and password field to execute credential verification against the `DataStore`.
    *   `private void showDashboard()`: Constructs a dashboard featuring a deep `COCOA_BROWN` header bar holding the logged-in user's name/role, a "Logout" button, and a sky blue "Refresh" button. Triggers `setupAdminTabs()`, `setupInstructorTabs()`, or `setupStudentTabs()` based on role. Appends a `ChangeListener` on the tabbed pane to auto-invoke `.refresh()` on any tab implementing `Refreshable` whenever the user switches tabs.
    *   `private void setupAdminTabs(JTabbedPane tabs)`: Instantiates and appends: `UserManagementPanel`, `StructureManagementPanel`, `CurriculumManagementPanel`, `StudentManagementPanel`, `CourseManagementPanel`, `EnrollmentManagementPanel`, and `ReportsPanel`.
    *   `private void setupInstructorTabs(JTabbedPane tabs)`: Instantiates and appends: `InstructorCoursesPanel`, `GradeEntryPanel`, and `GradeDistributionPanel`.
    *   `private void setupStudentTabs(JTabbedPane tabs)`: Instantiates and appends: `AvailableCoursesPanel`, `MyCoursesPanel`, and `TranscriptPanel`.
    *   `static void main(String[] args)`: Standard JVM main entry. Launches the GUI safely inside the Event Dispatch Thread (EDT) via `SwingUtilities.invokeLater`.

---

### 📦 Package `ui.admin` (System Administrative Dashboard Panels)

#### 📄 Class `UserManagementPanel.java` (JPanel)
*   **Purpose**: Manages system credentials, role assignments, and department mappings.
*   **Swing Components**:
    *   `JTextField` & `JPasswordField` (Details inputs).
    *   `JComboBox` (Role selector: Admin, Instructor, Student; Department dropdown filter).
    *   `JButton` (Add User, Update User, Delete User, Show/Hide Password Toggle, Gen Ref ID).
    *   `JTable` & `DefaultTableModel` (Displays search-sortable active user accounts).
*   **Methods**:
    *   `UserManagementPanel(DataStore)` (Constructor): Registers the data store reference, sets background, and initiates the panel.
    *   `private void initUI()`: Builds a detailed input form using `GridBagLayout`. Embeds an eye-open password visibility toggler next to the password input field. Binds a selection listener to the users table to auto-fill the form whenever a user row is highlighted.
    *   `save/update/delete ActionListeners`: Invokes `dataStore.addUser()`, `dataStore.updateUser()`, or `dataStore.deleteUser()` based on inputs. Uses `InputValidator` to validate password length and safely generate system-wide `Reference IDs`.
    *   `void refresh()`: Pulls the list of users from the `DataStore`, resets the table rows, and restores previous row selections if still active.

#### 📄 Class `StructureManagementPanel.java` (JPanel)
*   **Purpose**: Manages structural units: Faculties (e.g., Engineering) and academic Departments (e.g., Computer Science).
*   **Swing Components**:
    *   `JComboBox` (Type selector: Faculty or Department; Parent faculty combo).
    *   `JTextField` (Structural code and descriptive name).
    *   `JTable` (Left: Faculties table, Right: Departments table).
*   **Methods**:
    *   `StructureManagementPanel(DataStore)` (Constructor): Standard configuration.
    *   `private void initUI()`: Sets up an entity-type listener on the "Type" combo. If "Faculty" is selected, the parent-faculty dropdown hides. If "Department" is chosen, it reappears. Split tables allow administrators to view both hierarchies side-by-side.
    *   `void refresh()`: Updates both tables, updates parent-faculty combo models with the latest codes, and preserves selections.

#### 📄 Class `CurriculumManagementPanel.java` (JPanel)
*   **Purpose**: Maps generic courses (e.g., CS101) to specific departments and student years.
*   **Swing Components**:
    *   `JComboBox` (Dept dropdown, target student Year [1, 2, 3, 4], Course selector).
    *   `JButton` (Assign to Curriculum, Delete from Curriculum).
    *   `JTable` (Displays mappings matching selected dropdown criteria with columns: Dept, Year, Course Code, Course Name, local Credits, and ECTS).
    *   `JLabel` (Totals indicator displaying selected curriculum credits and ECTS sums at the bottom).
*   **Methods**:
    *   `initUI()`: Binds listeners to the Dept and Year combos so changing them automatically filters the mappings visible in the scroll pane below. Binds the "Assign" button to `dataStore.addCurriculumMapping()` and the "Delete" button to `dataStore.removeCurriculumMapping()`.
    *   `void refresh()`: Pulls curriculum lists, retrieves Course info (Name, Credits, ECTS) from the `DataStore`, calculates credit/ECTS totals for the selected department/year combination, filters dropdown lists, and repopulates the UI.

#### 📄 Class `StudentManagementPanel.java` (JPanel)
*   **Purpose**: Handles detailed student profiling, link synchronization with usernames, and Double Major (dual-degree) status.
*   **Swing Components**:
    *   `JTextField` (Student ID [numeric], Full Name [read-only, bound to login profile]).
    *   `JComboBox` (Username link combo, Primary major dept, Primary study year, Second major dept, Second major study year).
    *   `JButton` (Generate Student ID, Save Profile, Delete Profile).
    *   `JTable` (Displays students, italicizes/greys-out "(PENDING)" profiles with missing data).
*   **Methods**:
    *   `initUI()`: Sets up detailed inputs. Incorporates a custom table cell renderer to color and style row fonts differently if the student has a registered login profile but hasn't had their formal Student Profile filled out yet (displays as "(PENDING)" in grey). Binds a "Generate ID" action that calls `InputValidator.generateStudentId()` based on study year and selected department.
    *   `void refresh()`: Updates tables, synchronizes dropdown options to only display active student usernames, and builds the dual-major selections.

#### 📄 Class `CourseManagementPanel.java` (JPanel)
*   **Purpose**: Controls the academic course catalog, assigning quotas, credits, and linked instructors.
*   **Swing Components**:
    *   `JTable` (Active courses).
    *   `JButton` (Add, Update, Delete Course).
*   **Methods**:
    *   `private void showAddCourseDialog() / showUpdateCourseDialog()`: Launches a styled `JOptionPane` form containing a dynamic instructor autocomplete-search text field. As the admin types in the search field, a DocumentListener filters and updates the combo dropdown containing instructor usernames and real names.
    *   `void refresh()`: Reloads courses and keeps selections active.

#### 📄 Class `EnrollmentManagementPanel.java` (JPanel)
*   **Purpose**: Centrally manages student enrollment approvals, individual assignments, and bulk academic operations.
*   **Swing Components**:
    *   `JTabbedPane` (Sub-tabs: Current Enrollments, Pending Requests, Automation Hub).
    *   `JTable` (Displays active approvals and pending registrations).
    *   `JButton` (Unenroll Student, Approve request, Reject request, Run Automated Bulk Enrollment).
*   **Methods**:
    *   `createBulkEnrollmentSubPanel()`: Renders the **Automation Hub**. Administrators can choose a department and year, and click "Run Automated Bulk Enrollment" to automatically enroll all matching students in their mandatory curriculum courses with a single click.
    *   `void refresh()`: Separates approved enrollments from pending registrations and populates both views.

#### 📄 Class `ReportsPanel.java` (JPanel)
*   **Purpose**: Centralized analytics dashboard offering system warnings and department analytics.
*   **Swing Components**:
    *   `JLabel` (Acts as visual stat cards).
    *   `JPanel` (System Health warning center list container).
    *   `JComboBox` (Department analyst select).
*   **Methods**:
    *   `createStatCard(title, initialValue)`: Helper that generates a white, bordered card displaying key stats like Total Users, Total Courses, and Pending Requests.
    *   `void refresh()`: Performs active **integrity checks** across data files, warning admins with red flags if:
        1.  A student profile exists in `students.txt` but has no login account in `users.txt`.
        2.  An enrollment record points to a missing Student ID.
        Otherwise, displays a green success message.

---

### 📦 Package `ui.instructor` (Instructor Dashboard Panels)

#### 📄 Class `InstructorCoursesPanel.java` (JPanel)
*   **Purpose**: Displays the list of courses assigned to the logged-in instructor.
*   **Swing Components**:
    *   `JTable` (Course Code, Name, and current active enrollment count).
*   **Methods**:
    *   `void refresh()`: Queries `dataStore.getCoursesByInstructor()` using the logged-in user's username, counts enrollments for each course, and displays them.

#### 📄 Class `GradeEntryPanel.java` (JPanel)
*   **Purpose**: Allows instructors to enter midterm and final exam scores for enrolled students.
*   **Swing Components**:
    *   `JComboBox` (Instructors can select from their assigned courses).
    *   `JTable` (Lists enrolled student usernames, full names, and grade metrics).
    *   `JButton` (Load Students, Enter Grade).
*   **Methods**:
    *   `showGradeEntryDialog(studentUsername, fullName, courseCode)`: Launches a modal dialog. Contains inputs for Midterm (40% weight) and Final (60% weight). Binds a `DocumentListener` to both input fields to compute and display the weighted average and corresponding letter grade (e.g., AA, BB) **in real-time as the instructor types**. Saves the score via `dataStore.upsertGrade()`.

#### 📄 Class `GradeDistributionPanel.java` (JPanel)
*   **Purpose**: Visual wrapper panel that hosts a custom-drawn chart.
*   **Swing Components**:
    *   `JComboBox` (Course selector).
    *   `JPanel` (Chart container).
*   **Methods**:
    *   `refreshGradeDistribution()`: Cleans the chart container and injects a new instance of `GradeDistributionChart` for the selected course code.

#### 📄 Class `GradeDistributionChart.java` (JPanel)
*   **Purpose**: Renders a beautiful, animated bar chart of course grades.
*   **Graphics & Drawing Logic**:
    *   Uses **pure Java Graphics2D** (`paintComponent(Graphics g)`) with anti-aliasing enabled. **Zero external dependencies** (like JFreeChart) are required, making it lightweight and highly customized.
    *   Calculates grade counts (AA through FF) for the course.
    *   Draws customized Sage Green bars with rounded tops using `RoundRectangle2D.Double`.
    *   Automatically scales bar heights dynamically based on the highest student count.
    *   Prints student counts directly above each bar and grade letter labels centered beneath the X-axis.

---

### 📦 Package `ui.student` (Student Dashboard Panels)

#### 📄 Class AvailableCoursesPanel.java (JPanel)
*   **Purpose**: Allows students to view available courses, check real-time quotas, and request enrollment.
*   **Swing Components**:
    *   `JLabel` (ECTS load indicator: shows current ECTS vs. calculated ECTS limit).
    *   `JComboBox` (Filter by Instructor; Filter by Department/Major).
    *   `JTable` (Course list detailing Code, Name, Instructor, Credits, Quota [e.g., 23/40], and Status).
    *   `JButton` (Request Enrollment).
*   **Methods**:
    *   `applyFilters()`: Intercepts active selections. If set to "My Departments", the panel uses a complex `RowFilter` to only display courses starting with the student's primary department code or secondary department code (for double majors). Binds the "Request" button to `dataStore.requestEnrollment()`.

#### 📄 Class `MyCoursesPanel.java` (JPanel)
*   **Purpose**: Displays the student's active, approved courses.
*   **Swing Components**:
    *   `JTable` (Code, Course Name, Instructor).
*   **Methods**:
    *   `refresh()`: Filters enrollment lists for the student with an "APPROVED" status and populates the table.

#### 📄 Class `TranscriptPanel.java` (JPanel)
*   **Purpose**: Student's official grade center, offering real-time GPA calculations and export features.
*   **Swing Components**:
    *   `JLabel` (Displays cumulative GPA on a large font with Sky Blue text).
    *   `JTable` (Detailed list of all graded courses, showing midterm, final, weighted average, and letter grade).
    *   `JButton` (Export PDF Transcript, Export TXT Transcript).
*   **Methods**:
    *   `export button listeners`: Launches a `JFileChooser` requesting the target destination. Resolves extensions and forwards execution to `TranscriptExporter.export()`.

---

### 📦 Package `data` (The Storage Core)

#### 📄 Class `DataStore.java` (Singleton Database Controller)
*   **Purpose**: Manages all file-based database operations, in-memory caches, relationship constraints, and data generation/migration routines.
*   **Private Fields (`List<T>`)**:
    *   `users`, `students`, `courses`, `enrollments`, `grades`, `departments`, `faculties`, `curriculumMappings`.
*   **Methods**:
    *   `getInstance()`: Retrieves the singleton instance.
    *   `initialize()`: Creates the `data/` folder if missing. Calls load routines for all files (`loadUsers()`, `loadStudents()`, etc.). If empty, runs default creation routines (`createDefaultData()`, etc.). Automatically executes `migrateUsernames()` and `scaleData()`.
    *   `migrateUsernames()` (**Enterprise Migration Logic**): Scans all students. If a student is using a legacy numeric ID as their username (e.g., `202311024`), it automatically migrates their account credentials to a clean, professional `name.surname` format using `InputValidator.generateUsername()`. It preserves all profile settings and updates all linked records in `enrollments.txt` and `grades.txt` to ensure data integrity.
    *   `scaleData()` (**Scalability Routine**): Simulates a large institutional scale. Scans each department. If a department has fewer than 25 students, it automatically generates realistic test students with unique names, matching profiles, and password records. It also ensures at least 20 ECTS-compliant courses and 4 instructors exist per department, saving all additions to text files.
    *   `authenticate(user, pass)`: Authenticates logins. Supports **Dual-Authentication** for students, allowing them to sign in using either their `name.surname` username or their numeric `Student ID`.
    *   `addUser(User) / updateUser(User) / deleteUser(username)`: Performs CRUD on users. Binds a **Cascading Delete** on students: deleting a student's user account automatically purges their student profile, active course enrollments, and academic grades.
    *   `enrollStudent(studentUsername, courseCode, status)`: Evaluates rigorous academic constraints before enrolling a student:
        1.  Major restrictions (e.g., CS majors cannot enroll in EE courses unless they double major).
        2.  Checks if the student is already enrolled.
        3.  Validates that the course has open quota.
        4.  Ensures that adding the course's credits will not exceed the student's dynamic ECTS credit limit.
    *   `calculateCreditLimit(username)`: Calculates dynamic ECTS credit limits (30 to 45 credits) based on academic rules (see Section 4).
    *   `calculateGPA(username)`: Calculates the student's cumulative GPA on a 4.0 scale by multiplying each course's credit by the grade point value of its letter grade, summing the results, and dividing by total credits.
    *   `save/load methods`: Implements file reads and writes for all entities with standard CSV serialization.

---

### 📦 Package `util` (System Utilities & Algorithms)

#### 📄 Class `InputValidator.java` (Format Validators & Normalizers)
*   **Purpose**: Formats and validates input fields, generates custom reference codes, and handles text normalization.
*   **Methods**:
    *   `validateStudentId(id)`: Verifies that the Student ID is exactly 9 digits, starts with "20", and satisfies the checksum algorithm.
    *   `isValidStudentIdAlgorithm(id)`: Runs the checksum algorithm: `(Sum of all digits) % 10 == 7`.
    *   `generateStudentId(year, deptCode)`: Generates a valid 9-digit Student ID. It takes the entrance year, appends a deterministic 2-digit department code, generates a random serial number, and computes the 9th digit to satisfy the checksum.
    *   `validateCourseCode(code)`: Validates alphanumeric formats matching the regex `[A-Z]{2,4}\d{3}` (e.g., `CS101`, `MATH202`).
    *   `normalizeTurkish(text)`: Replaces Turkish letters (ç, ğ, ı, ö, ş, ü) with English equivalents to standardise file paths and usernames.
    *   `generateUsername(fullName)`: Normalizes full names and generates clean `name.surname` usernames.

#### 📄 Class `TranscriptExporter.java` (Transcript Engines)
*   **Purpose**: Exports student transcripts in text and PDF formats.
*   **Methods**:
    *   `exportToText(username, path)`: Generates a formatted text transcript (`.txt`) featuring student demographics, enrolled courses, grades, averages, letter grades, and cumulative GPA.
    *   `exportToPdf(username, path)`: Uses the **iText PDF library** to generate a beautifully styled PDF transcript (`.pdf`). It builds a formal title block, constructs an organized table with light grey headers, and appends a right-aligned cumulative GPA section.

---

### 📦 Package `test` (Quality Assurance Suites)

#### 📄 Class `ProjectValidationTest.java` (Basic Verification)
*   **Purpose**: A simple console script that checks key system functionalities like File Presence, Checksum validation, basic student authentication, enrollment quotas, and GPA calculation.

#### 📄 Class `ComprehensiveSystemTest.java` (Full Compliance Test Suite)
*   **Purpose**: A highly thorough testing suite that covers all business rules under simulation.
*   **Methods**:
    *   `runIdentityTests()`: Tests user registration, duplicate username blocks, and admin account protection.
    *   `runAcademicStructureTests()`: Verifies CRUD operations for departments and courses.
    *   `runUniIDProtocolTests()`: Validates ID checksums and department code mappings.
    *   `runDoubleMajorTests()`: Verifies independent primary/secondary study years and enforces primary-seniority rules.
    *   `runEnrollmentAndQuotaTests()`: Verifies enrollment quotas and major/minor restrictions.
    *   `runGradingAndGPATests()`: Tests GPA calculation accuracy with multiple grades.
    *   `runAdminReportingTests()`: Verifies stats tracking and text/PDF transcript exports.
    *   `runValidationUtilityTests()`: Tests password length, course code formats, and credit limits.
    *   `runInstructorViewTests()`: Verifies that instructors can only view students enrolled in their assigned courses.

---

## 🧮 4. Core Algorithms & Business Rules
These core algorithms drive the application's business logic.

### A. Student ID Checksum Algorithm
To protect the system against fraudulent IDs, all Student IDs must satisfy this formula:
$$\text{Sum of all 9 digits} \pmod{10} = 7$$
*   **Example**: ID `202311024`
    $$\text{Sum} = 2+0+2+3+1+1+0+2+4 = 15 \implies 15 \pmod{10} = 5 \neq 7 \quad \text{(Invalid!)}$$
    ID `202311026`
    $$\text{Sum} = 2+0+2+3+1+1+0+2+6 = 17 \implies 17 \pmod{10} = 7 \quad \text{(Valid!)}$$

### B. Dynamic ECTS Credit Limit Calculator
To support high-performing students, the system dynamically calculates their ECTS limit:
1.  **Double Major**: **45 ECTS** max (Highest priority).
2.  **High GPA (3.5+)**: **40 ECTS** max.
3.  **Good GPA (3.0+)** or **Senior (Year 4)**: **36 ECTS** max.
4.  **Standard**: **30 ECTS** max.

### C. Grade Averages & Letter Grade Conversions
The system calculates a student's weighted average and assigns a letter grade:
$$\text{Average} = (\text{Midterm} \times 0.4) + (\text{Final} \times 0.6)$$

| Average Range | Letter Grade | GPA Points | Status |
| :--- | :--- | :--- | :--- |
| **90 - 100** | AA | 4.0 | Passed |
| **85 - 89.9** | BA | 3.5 | Passed |
| **80 - 84.9** | BB | 3.0 | Passed |
| **75 - 79.9** | CB | 2.5 | Passed |
| **70 - 74.9** | CC | 2.0 | Passed |
| **65 - 69.9** | DC | 1.5 | Passed |
| **60 - 64.9** | DD | 1.0 | Passed |
| **50 - 59.9** | FD | 0.5 | Failed |
| **0 - 49.9** | FF | 0.0 | Failed |

---

## 🎭 5. Presentation Q&A Cheat Sheet
Here are five questions your professors are highly likely to ask during your presentation, along with the perfect answers to help you ace it.

> [!TIP]
> **Q1: Why did you implement a Singleton Pattern for the `DataStore` instead of just passing a normal class instance around?**
> *   **Answer**: "We used the Singleton Pattern to ensure there is exactly one central in-memory store managing our data collections at runtime. Since our interface is modularized into multiple independent JPanels, using a Singleton ensures that when data is modified in one panel (e.g., adding a student profile), all other panels instantly display the updated data. If we didn't use a Singleton, different tabs would get out of sync, leading to data inconsistencies."

> [!TIP]
> **Q2: What happens if a student is deleted? How does your system handle their enrollment and grade history?**
> *   **Answer**: "The system implements a robust **Cascading Delete** routine. When an admin deletes a student's user account, the `deleteUser()` method in `DataStore` automatically triggers a cascade: it removes the student's academic profile from `students.txt`, purges their course registrations from `enrollments.txt`, and deletes their grades from `grades.txt`. This prevents orphan records and maintains overall data integrity."

> [!TIP]
> **Q3: How does your UI refresh dynamically when a user switches tabs?**
> *   **Answer**: "We established the `Refreshable` interface in the `ui.shared` package. Every UI panel implements this interface. In our main frame, `UniversityAutomationApp`, we attached a `ChangeListener` to the dashboard's `JTabbedPane`. Whenever the active tab changes, the app identifies if the new panel implements `Refreshable` and calls its `refresh()` method to fetch the latest data from the `DataStore` and rebuild the tables dynamically."

> [!TIP]
> **Q4: How did you implement your grade distribution charts without using external libraries?**
> *   **Answer**: "To keep our application lightweight and avoid external dependencies, we extended `JPanel` and overrode `paintComponent(Graphics g)`. We used standard **Java Graphics2D** to draw the chart manually. We calculated grade counts, scaled the bars dynamically based on the highest count, and drew them as rounded bars using `RoundRectangle2D.Double`. This gives us full control over the styling and ensures it matches our theme perfectly."

> [!TIP]
> **Q5: What are the academic rules that control course enrollment in your system?**
> *   **Answer**: "Enrollments are evaluated against four key rules in the `enrollStudent()` method of `DataStore`:
>     1.  **Major Restrictions**: A student can only enroll in courses belonging to their primary major or double-major department.
>     2.  **Duplicate check**: Enforces that students cannot register for the same course twice.
>     3.  **Quota Limits**: The current enrollment count must be strictly less than the course quota.
>     4.  **ECTS Credit Limits**: The sum of the student's active course credits plus the new course's credits cannot exceed their dynamic credit limit (between 30 and 45 credits, calculated based on their GPA, double major status, and study year)."
