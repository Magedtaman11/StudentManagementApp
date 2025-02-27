import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class StudentManagementFrame extends JFrame {

    // Student class to store data
    private class Student {
        String id;
        String name;
        String course;
        String email;
        String grade;

        Student(String id, String name, String course, String email) {
            this.id = id;
            this.name = name;
            this.course = course;
            this.email = email;
            this.grade = "Not Graded";
        }
    }

    // Data storage
    private ArrayList<Student> studentList = new ArrayList<>();

    // UI Components
    private JTextField idField, nameField, emailField;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> gradeComboBox;
    private DefaultListModel<String> courseListModel;
    private JList<String> courseList;
    private JComboBox<String> courseDropdown;

    public StudentManagementFrame() {
        // Basic frame setup
        setTitle("Student Management System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize courseListModel first since it's needed by Add Student tab
        courseListModel = new DefaultListModel<String>();
        // Add default courses
        courseListModel.addElement("Computer Science 101");
        courseListModel.addElement("Data Structures");
        courseListModel.addElement("Web Development");
        courseListModel.addElement("Database Systems");
        courseListModel.addElement("Software Engineering");

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.addTab("Home", createHomeTab());
        tabbedPane.addTab("Add Student", createAddStudentTab());
        tabbedPane.addTab("View Students", createViewStudentsTab());
        tabbedPane.addTab("Grade Management", createGradeTab());
        tabbedPane.addTab("Courses", createCoursesTab());

        // Add to frame
        add(tabbedPane);
    }

    // Tab 1: Home tab with information
    private JPanel createHomeTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Header
        JLabel titleLabel = new JLabel("Student Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Information text
        JTextArea infoArea = new JTextArea(
                "\nWelcome to the Student Management System!\n\n" +
                        "This application helps you manage student information efficiently.\n\n" +
                        "Features:\n" +
                        "- Add new students\n" +
                        "- View student records\n" +
                        "- Manage grades\n" +
                        "- View course information\n\n" +
                        "Use the tabs above to navigate between different functions."
        );
        infoArea.setEditable(false);
        infoArea.setBackground(panel.getBackground());
        infoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(infoArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button
        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(panel,
                        "For help using this application, please contact your administrator.",
                        "Help", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(helpButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Tab 2: Add Student tab with form
    private JPanel createAddStudentTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Student ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Course:"));
        // Replace the text field with a dropdown
        courseDropdown = new JComboBox<>();

        // Add a refresh button next to the dropdown to update the course list
        JPanel courseSelectionPanel = new JPanel(new BorderLayout());
        courseSelectionPanel.add(courseDropdown, BorderLayout.CENTER);

        JButton refreshCoursesButton = new JButton("↻");
        refreshCoursesButton.setToolTipText("Refresh course list");
        refreshCoursesButton.addActionListener(e -> {
            // Clear and repopulate the dropdown with courses from the course list model
            courseDropdown.removeAllItems();
            for (int i = 0; i < courseListModel.getSize(); i++) {
                courseDropdown.addItem(courseListModel.getElementAt(i));
            }
        });
        courseSelectionPanel.add(refreshCoursesButton, BorderLayout.EAST);

        formPanel.add(courseSelectionPanel);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        panel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                idField.setText("");
                nameField.setText("");
                emailField.setText("");
                // Don't clear the course selection
            }
        });

        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Validate fields
                if(idField.getText().isEmpty() || nameField.getText().isEmpty() ||
                        emailField.getText().isEmpty() || courseDropdown.getSelectedItem() == null) {

                    JOptionPane.showMessageDialog(panel,
                            "Please fill in all fields",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create new student with the selected course
                Student student = new Student(
                        idField.getText(),
                        nameField.getText(),
                        courseDropdown.getSelectedItem().toString(),
                        emailField.getText()
                );

                // Add to list
                studentList.add(student);

                // Update table if exists
                updateStudentTable();

                // Success message
                JOptionPane.showMessageDialog(panel,
                        "Student added successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Clear fields
                clearButton.doClick();
            }
        });

        buttonPanel.add(clearButton);
        buttonPanel.add(addButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Initial population of the course dropdown
        for (int i = 0; i < courseListModel.getSize(); i++) {
            courseDropdown.addItem(courseListModel.getElementAt(i));
        }

        return panel;
    }

    // Tab 3: View Students tab with table
    private JPanel createViewStudentsTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Create table with columns
        String[] columns = {"ID", "Name", "Course", "Email", "Grade"};
        tableModel = new DefaultTableModel(columns, 0);
        studentTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateStudentTable();
            }
        });

        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = studentTable.getSelectedRow();
                if(row >= 0) {
                    // Remove student
                    String id = (String) tableModel.getValueAt(row, 0);

                    // Find and remove from list
                    for(int i = 0; i < studentList.size(); i++) {
                        if(studentList.get(i).id.equals(id)) {
                            studentList.remove(i);
                            break;
                        }
                    }

                    // Update table
                    tableModel.removeRow(row);

                    JOptionPane.showMessageDialog(panel,
                            "Student deleted",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panel,
                            "Please select a student",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Tab 4: Grade Management tab
    private JPanel createGradeTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Grade assignment panel
        JPanel gradePanel = new JPanel(new GridLayout(3, 2, 10, 10));

        gradePanel.add(new JLabel("Enter Student ID:"));
        JTextField studentIdField = new JTextField();
        gradePanel.add(studentIdField);

        gradePanel.add(new JLabel("Select Grade:"));
        String[] grades = {"A", "B", "C", "D", "F", "Incomplete", "Not Graded"};
        gradeComboBox = new JComboBox<String>(grades);
        gradePanel.add(gradeComboBox);

        // Display selected student info
        JLabel studentInfoLabel = new JLabel("Student: Not selected");
        gradePanel.add(studentInfoLabel);

        // Find student button
        JButton findButton = new JButton("Find Student");
        findButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchId = studentIdField.getText().trim();
                if(searchId.isEmpty()) {
                    JOptionPane.showMessageDialog(panel,
                            "Please enter a student ID",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Find student by ID
                Student foundStudent = null;
                for(Student student : studentList) {
                    if(student.id.equals(searchId)) {
                        foundStudent = student;
                        break;
                    }
                }

                if(foundStudent != null) {
                    studentInfoLabel.setText("Student: " + foundStudent.name + " (Current Grade: " + foundStudent.grade + ")");
                    // Preselect current grade if possible
                    for(int i = 0; i < grades.length; i++) {
                        if(grades[i].equals(foundStudent.grade)) {
                            gradeComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                } else {
                    studentInfoLabel.setText("Student: Not found");
                    JOptionPane.showMessageDialog(panel,
                            "No student found with ID: " + searchId,
                            "Not Found",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        gradePanel.add(findButton);

        JButton assignButton = new JButton("Assign Grade");
        assignButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchId = studentIdField.getText().trim();
                if(searchId.isEmpty()) {
                    JOptionPane.showMessageDialog(panel,
                            "Please enter a student ID",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Find student by ID
                boolean studentFound = false;
                for(Student student : studentList) {
                    if(student.id.equals(searchId)) {
                        // Update grade
                        student.grade = (String) gradeComboBox.getSelectedItem();

                        // Update UI
                        updateStudentTable();
                        studentInfoLabel.setText("Student: " + student.name + " (Current Grade: " + student.grade + ")");

                        JOptionPane.showMessageDialog(panel,
                                "Grade assigned successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);

                        studentFound = true;
                        break;
                    }
                }

                if(!studentFound) {
                    JOptionPane.showMessageDialog(panel,
                            "No student found with ID: " + searchId,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(assignButton);

        panel.add(gradePanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    // Tab 5: Courses tab
    private JPanel createCoursesTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Course list
        courseList = new JList<String>(courseListModel);
        JScrollPane listScroll = new JScrollPane(courseList);
        panel.add(listScroll, BorderLayout.WEST);

        // Course details
        JTextArea courseDetails = new JTextArea();
        courseDetails.setEditable(false);
        courseDetails.setLineWrap(true);
        courseDetails.setWrapStyleWord(true);

        JScrollPane detailsScroll = new JScrollPane(courseDetails);
        panel.add(detailsScroll, BorderLayout.CENTER);

        // When a course is selected, show details
        courseList.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                String selected = courseList.getSelectedValue();
                if(selected != null) {
                    switch(selected) {
                        case "Computer Science 101":
                            courseDetails.setText("Computer Science 101\n\nIntroduction to programming concepts and problem-solving techniques.");
                            break;
                        case "Data Structures":
                            courseDetails.setText("Data Structures\n\nStudy of data structures and algorithms including lists, stacks, queues, and trees.");
                            break;
                        case "Web Development":
                            courseDetails.setText("Web Development\n\nLearn HTML, CSS, JavaScript and server-side programming.");
                            break;
                        case "Database Systems":
                            courseDetails.setText("Database Systems\n\nIntroduction to database design, SQL, and database management systems.");
                            break;
                        case "Software Engineering":
                            courseDetails.setText("Software Engineering\n\nPrinciples of software design, testing, and project management.");
                            break;
                        default:
                            courseDetails.setText(selected + "\n\nNo details available for this course.");
                    }
                }
            }
        });

        // Course management panel
        JPanel coursePanel = new JPanel(new BorderLayout());

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField newCourseField = new JTextField(20);
        JButton addCourseButton = new JButton("Add Course");

        addCourseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newCourse = newCourseField.getText().trim();
                if(!newCourse.isEmpty()) {
                    if(!courseListModel.contains(newCourse)) {
                        courseListModel.addElement(newCourse);
                        newCourseField.setText("");

                        // Update course dropdown in Add Student tab
                        courseDropdown.addItem(newCourse);
                    } else {
                        JOptionPane.showMessageDialog(panel,
                                "Course already exists",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        addPanel.add(new JLabel("New Course:"));
        addPanel.add(newCourseField);
        addPanel.add(addCourseButton);

        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton removeCourseButton = new JButton("Remove Selected Course");

        removeCourseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = courseList.getSelectedIndex();
                if(index >= 0) {
                    String courseToRemove = courseListModel.getElementAt(index);
                    courseListModel.remove(index);

                    // Update course dropdown in Add Student tab
                    for (int i = 0; i < courseDropdown.getItemCount(); i++) {
                        if (courseDropdown.getItemAt(i).equals(courseToRemove)) {
                            courseDropdown.removeItemAt(i);
                            break;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(panel,
                            "Please select a course",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        removePanel.add(removeCourseButton);

        coursePanel.add(addPanel, BorderLayout.NORTH);
        coursePanel.add(removePanel, BorderLayout.SOUTH);

        panel.add(coursePanel, BorderLayout.SOUTH);

        return panel;
    }

    // Update the student table with current data
    private void updateStudentTable() {
        // Clear table
        tableModel.setRowCount(0);

        // Add all students to table
        for(Student student : studentList) {
            Object[] row = {
                    student.id,
                    student.name,
                    student.course,
                    student.email,
                    student.grade
            };
            tableModel.addRow(row);
        }
    }
}