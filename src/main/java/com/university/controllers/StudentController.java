// src/main/java/com/university/controllers/StudentController.java
package com.university.controllers;

import com.university.models.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StudentController {
    @FXML private TableView<StudentData> studentTable;
    @FXML private TableColumn<StudentData, Integer> idCol;
    @FXML private TableColumn<StudentData, String> uniqueIdCol;
    @FXML private TableColumn<StudentData, String> nameCol;
    @FXML private TableColumn<StudentData, String> emailCol;
    @FXML private TableColumn<StudentData, String> rollCol;
    @FXML private TableColumn<StudentData, String> deptCol;
    @FXML private TableColumn<StudentData, Integer> semesterCol;
    @FXML private TableColumn<StudentData, Double> cgpaCol;
    @FXML private TableColumn<StudentData, String> statusCol;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterDept;
    @FXML private ComboBox<String> filterSemester;

    private ObservableList<StudentData> studentList = FXCollections.observableArrayList();
    private ObservableList<StudentData> filteredList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        uniqueIdCol.setCellValueFactory(new PropertyValueFactory<>("uniqueId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        rollCol.setCellValueFactory(new PropertyValueFactory<>("rollNumber"));
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));
        cgpaCol.setCellValueFactory(new PropertyValueFactory<>("cgpa"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add CSS styling to table
        studentTable.getStyleClass().add("table-view");

        // Load data
        loadDepartments();
        loadStudents();

        // Setup search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterStudents());
        filterDept.valueProperty().addListener((obs, oldVal, newVal) -> filterStudents());
        filterSemester.valueProperty().addListener((obs, oldVal, newVal) -> filterStudents());
    }

    private void loadDepartments() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT dept_name FROM departments");
            ResultSet rs = stmt.executeQuery();

            filterDept.getItems().clear();
            filterDept.getItems().add("All Departments");
            while (rs.next()) {
                filterDept.getItems().add(rs.getString("dept_name"));
            }
            filterDept.setValue("All Departments");

            rs.close();
            stmt.close();

            // Load semesters
            filterSemester.getItems().addAll("All Semesters", "1", "2", "3", "4", "5", "6", "7", "8");
            filterSemester.setValue("All Semesters");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadStudents() {
        studentList.clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT u.id, u.unique_id, u.full_name, u.email, u.is_active, " +
                    "s.roll_number, s.department, s.current_semester, s.cgpa " +
                    "FROM users u " +
                    "INNER JOIN students s ON u.id = s.user_id " +
                    "ORDER BY u.full_name";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StudentData student = new StudentData(
                        rs.getInt("id"),
                        rs.getString("unique_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("roll_number"),
                        rs.getString("department"),
                        rs.getInt("current_semester"),
                        rs.getDouble("cgpa"),
                        rs.getBoolean("is_active")
                );
                studentList.add(student);
            }

            rs.close();
            stmt.close();

            filteredList.setAll(studentList);
            studentTable.setItems(filteredList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load students: " + e.getMessage());
        }
    }

    @FXML
    private void filterStudents() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        String deptFilter = filterDept.getValue();
        String semesterFilter = filterSemester.getValue();

        filteredList.clear();

        for (StudentData student : studentList) {
            boolean matchesSearch = searchTerm.isEmpty() ||
                    student.getFullName().toLowerCase().contains(searchTerm) ||
                    student.getUniqueId().toLowerCase().contains(searchTerm) ||
                    student.getRollNumber().toLowerCase().contains(searchTerm) ||
                    student.getEmail().toLowerCase().contains(searchTerm);

            boolean matchesDept = deptFilter.equals("All Departments") ||
                    student.getDepartment().equals(deptFilter);

            boolean matchesSemester = semesterFilter.equals("All Semesters") ||
                    String.valueOf(student.getSemester()).equals(semesterFilter);

            if (matchesSearch && matchesDept && matchesSemester) {
                filteredList.add(student);
            }
        }

        studentTable.setItems(filteredList);
    }

    @FXML
    private void showAddStudent() {
        Dialog<StudentData> dialog = new Dialog<>();
        dialog.setTitle("Add New Student");
        dialog.setHeaderText("Enter student details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField fullName = new TextField();
        fullName.setPromptText("Full Name");
        TextField email = new TextField();
        email.setPromptText("Email");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        TextField phone = new TextField();
        phone.setPromptText("Phone");
        TextField rollNumber = new TextField();
        rollNumber.setPromptText("Roll Number");
        TextField department = new TextField();
        department.setPromptText("Department");
        ComboBox<Integer> semester = new ComboBox<>();
        semester.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8);
        semester.setValue(1);

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullName, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(email, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(password, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phone, 1, 3);
        grid.add(new Label("Roll Number:"), 0, 4);
        grid.add(rollNumber, 1, 4);
        grid.add(new Label("Department:"), 0, 5);
        grid.add(department, 1, 5);
        grid.add(new Label("Semester:"), 0, 6);
        grid.add(semester, 1, 6);

        dialog.getDialogPane().setContent(grid);

        // Enable/disable save button
        dialog.getDialogPane().lookupButton(saveButtonType).setDisable(true);
        fullName.textProperty().addListener((obs, old, newVal) ->
                dialog.getDialogPane().lookupButton(saveButtonType).setDisable(
                        newVal.trim().isEmpty() || email.getText().trim().isEmpty()
                )
        );
        email.textProperty().addListener((obs, old, newVal) ->
                dialog.getDialogPane().lookupButton(saveButtonType).setDisable(
                        fullName.getText().trim().isEmpty() || newVal.trim().isEmpty()
                )
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new StudentData(0, null, fullName.getText(), email.getText(),
                        rollNumber.getText(), department.getText(), semester.getValue(), 0.0, true);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(studentData -> {
            // Save to database
            saveStudent(studentData, password.getText(), phone.getText());
        });
    }

    private void saveStudent(StudentData studentData, String password, String phone) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Generate unique ID
            String uniqueId = "STU" + System.currentTimeMillis() % 1000000;

            // Insert into users table
            String userQuery = "INSERT INTO users (unique_id, full_name, email, password, role, phone) " +
                    "VALUES (?, ?, ?, ?, 'student', ?)";
            PreparedStatement userStmt = conn.prepareStatement(userQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, uniqueId);
            userStmt.setString(2, studentData.getFullName());
            userStmt.setString(3, studentData.getEmail());
            userStmt.setString(4, password);
            userStmt.setString(5, phone);

            int affected = userStmt.executeUpdate();
            if (affected > 0) {
                ResultSet generatedKeys = userStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    // Insert into students table
                    String studentQuery = "INSERT INTO students (user_id, roll_number, department, current_semester) " +
                            "VALUES (?, ?, ?, ?)";
                    PreparedStatement studentStmt = conn.prepareStatement(studentQuery);
                    studentStmt.setInt(1, userId);
                    studentStmt.setString(2, studentData.getRollNumber());
                    studentStmt.setString(3, studentData.getDepartment());
                    studentStmt.setInt(4, studentData.getSemester());

                    studentStmt.executeUpdate();
                    studentStmt.close();

                    conn.commit();
                    showAlert("Success", "Student added successfully!\nUnique ID: " + uniqueId);
                    loadStudents();
                }
            }

            userStmt.close();
            conn.setAutoCommit(true);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add student: " + e.getMessage());
        }
    }

    @FXML
    private void editStudent() {
        StudentData selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a student to edit.");
            return;
        }

        // Create edit dialog
        Dialog<StudentData> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");
        dialog.setHeaderText("Edit details for " + selected.getFullName());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField fullName = new TextField(selected.getFullName());
        TextField email = new TextField(selected.getEmail());
        TextField rollNumber = new TextField(selected.getRollNumber());
        TextField department = new TextField(selected.getDepartment());
        ComboBox<Integer> semester = new ComboBox<>();
        semester.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8);
        semester.setValue(selected.getSemester());
        TextField cgpa = new TextField(String.valueOf(selected.getCgpa()));

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullName, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(email, 1, 1);
        grid.add(new Label("Roll Number:"), 0, 2);
        grid.add(rollNumber, 1, 2);
        grid.add(new Label("Department:"), 0, 3);
        grid.add(department, 1, 3);
        grid.add(new Label("Semester:"), 0, 4);
        grid.add(semester, 1, 4);
        grid.add(new Label("CGPA:"), 0, 5);
        grid.add(cgpa, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                selected.setFullName(fullName.getText());
                selected.setEmail(email.getText());
                selected.setRollNumber(rollNumber.getText());
                selected.setDepartment(department.getText());
                selected.setSemester(semester.getValue());
                try {
                    selected.setCgpa(Double.parseDouble(cgpa.getText()));
                } catch (NumberFormatException e) {
                    selected.setCgpa(0.0);
                }
                return selected;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(studentData -> {
            updateStudent(studentData);
        });
    }

    private void updateStudent(StudentData studentData) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Update users table
            String userQuery = "UPDATE users SET full_name = ?, email = ? WHERE id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userQuery);
            userStmt.setString(1, studentData.getFullName());
            userStmt.setString(2, studentData.getEmail());
            userStmt.setInt(3, studentData.getUserId());
            userStmt.executeUpdate();

            // Update students table
            String studentQuery = "UPDATE students SET roll_number = ?, department = ?, " +
                    "current_semester = ?, cgpa = ? WHERE user_id = ?";
            PreparedStatement studentStmt = conn.prepareStatement(studentQuery);
            studentStmt.setString(1, studentData.getRollNumber());
            studentStmt.setString(2, studentData.getDepartment());
            studentStmt.setInt(3, studentData.getSemester());
            studentStmt.setDouble(4, studentData.getCgpa());
            studentStmt.setInt(5, studentData.getUserId());
            studentStmt.executeUpdate();

            conn.commit();

            userStmt.close();
            studentStmt.close();
            conn.setAutoCommit(true);

            showAlert("Success", "Student updated successfully!");
            loadStudents();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update student: " + e.getMessage());
        }
    }

    @FXML
    private void deleteStudent() {
        StudentData selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a student to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Student");
        alert.setHeaderText("Confirm Delete");
        alert.setContentText("Are you sure you want to delete " + selected.getFullName() + "?\n" +
                "This action cannot be undone!");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = DatabaseConnection.getInstance().getConnection();

                // Soft delete - just deactivate
                String query = "UPDATE users SET is_active = 0 WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, selected.getUserId());

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showAlert("Success", "Student deactivated successfully!");
                    loadStudents();
                }

                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete student: " + e.getMessage());
            }
        }
    }

    @FXML
    private void refreshStudents() {
        loadStudents();
        showAlert("Info", "Student list refreshed!");
    }

    @FXML
    private void exportStudents() {
        // Export to CSV functionality
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Export");
        info.setHeaderText("Export Students");
        info.setContentText("Export functionality will be implemented soon!");
        info.showAndWait();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) studentTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for table data
    public static class StudentData {
        private int userId;
        private String uniqueId;
        private String fullName;
        private String email;
        private String rollNumber;
        private String department;
        private int semester;
        private double cgpa;
        private boolean isActive;

        public StudentData(int userId, String uniqueId, String fullName, String email,
                           String rollNumber, String department, int semester, double cgpa, boolean isActive) {
            this.userId = userId;
            this.uniqueId = uniqueId;
            this.fullName = fullName;
            this.email = email;
            this.rollNumber = rollNumber;
            this.department = department;
            this.semester = semester;
            this.cgpa = cgpa;
            this.isActive = isActive;
        }

        // Getters
        public int getUserId() { return userId; }
        public String getUniqueId() { return uniqueId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getRollNumber() { return rollNumber; }
        public String getDepartment() { return department; }
        public int getSemester() { return semester; }
        public double getCgpa() { return cgpa; }
        public String getStatus() { return isActive ? "Active" : "Inactive"; }

        // Setters
        public void setUserId(int userId) { this.userId = userId; }
        public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public void setEmail(String email) { this.email = email; }
        public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
        public void setDepartment(String department) { this.department = department; }
        public void setSemester(int semester) { this.semester = semester; }
        public void setCgpa(double cgpa) { this.cgpa = cgpa; }
        public void setActive(boolean active) { isActive = active; }
    }
}