// src/main/java/com/university/controllers/StudentController.java
package com.university.controllers;

import com.university.models.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
    @FXML private Label recordCount;

    private ObservableList<StudentData> studentList = FXCollections.observableArrayList();
    private ObservableList<StudentData> filteredList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        uniqueIdCol.setCellValueFactory(new PropertyValueFactory<>("uniqueId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        rollCol.setCellValueFactory(new PropertyValueFactory<>("rollNumber"));
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));
        cgpaCol.setCellValueFactory(new PropertyValueFactory<>("cgpa"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadDepartments();
        loadStudents();

        searchField.textProperty().addListener((obs, old, newVal) -> filterStudents());
        filterDept.valueProperty().addListener((obs, old, newVal) -> filterStudents());
        filterSemester.valueProperty().addListener((obs, old, newVal) -> filterStudents());
    }

    private void loadDepartments() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT department FROM students WHERE department IS NOT NULL");
            ResultSet rs = stmt.executeQuery();

            filterDept.getItems().clear();
            filterDept.getItems().add("All Departments");
            while (rs.next()) {
                filterDept.getItems().add(rs.getString("department"));
            }
            filterDept.setValue("All Departments");

            rs.close();
            stmt.close();

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
                    "s.roll_number, s.department, s.semester, s.cgpa " +
                    "FROM users u INNER JOIN students s ON u.id = s.user_id ORDER BY u.full_name";

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
                        rs.getInt("semester"),
                        rs.getDouble("cgpa"),
                        rs.getBoolean("is_active")
                );
                studentList.add(student);
            }

            rs.close();
            stmt.close();

            filteredList.setAll(studentList);
            studentTable.setItems(filteredList);
            updateRecordCount();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load students: " + e.getMessage());
        }
    }

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
                    (student.getDepartment() != null && student.getDepartment().equals(deptFilter));

            boolean matchesSemester = semesterFilter.equals("All Semesters") ||
                    String.valueOf(student.getSemester()).equals(semesterFilter);

            if (matchesSearch && matchesDept && matchesSemester) {
                filteredList.add(student);
            }
        }

        studentTable.setItems(filteredList);
        updateRecordCount();
    }

    private void updateRecordCount() {
        int count = studentTable.getItems().size();
        recordCount.setText("Showing " + count + " records");
    }

    @FXML
    private void refreshStudents() {
        loadStudents();
        showAlert("Info", "Student list refreshed!");
    }

    @FXML
    private void showAddStudent() {
        showAlert("Info", "Add Student functionality will be implemented in the next version!");
    }

    @FXML
    private void editStudent() {
        StudentData selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a student to edit.");
            return;
        }
        showAlert("Info", "Edit Student: " + selected.getFullName() +
                "\nThis functionality will be implemented in the next version!");
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
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete " + selected.getFullName() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = DatabaseConnection.getInstance().getConnection();
                String query = "UPDATE users SET is_active = 0 WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, selected.getUserId());
                stmt.executeUpdate();
                stmt.close();

                showAlert("Success", "Student deleted successfully!");
                loadStudents();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete student: " + e.getMessage());
            }
        }
    }

    @FXML
    private void viewDetails() {
        StudentData selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a student to view.");
            return;
        }
        showAlert("Student Details",
                "Name: " + selected.getFullName() +
                        "\nUnique ID: " + selected.getUniqueId() +
                        "\nEmail: " + selected.getEmail() +
                        "\nRoll Number: " + selected.getRollNumber() +
                        "\nDepartment: " + selected.getDepartment() +
                        "\nSemester: " + selected.getSemester() +
                        "\nCGPA: " + selected.getCgpa() +
                        "\nStatus: " + selected.getStatus()
        );
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

        public int getUserId() { return userId; }
        public String getUniqueId() { return uniqueId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getRollNumber() { return rollNumber; }
        public String getDepartment() { return department; }
        public int getSemester() { return semester; }
        public double getCgpa() { return cgpa; }
        public String getStatus() { return isActive ? "Active" : "Inactive"; }
    }
}