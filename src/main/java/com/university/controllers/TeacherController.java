// src/main/java/com/university/controllers/TeacherController.java
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

public class TeacherController {
    @FXML private TableView<TeacherData> teacherTable;
    @FXML private TableColumn<TeacherData, Integer> idCol;
    @FXML private TableColumn<TeacherData, String> uniqueIdCol;
    @FXML private TableColumn<TeacherData, String> nameCol;
    @FXML private TableColumn<TeacherData, String> emailCol;
    @FXML private TableColumn<TeacherData, String> empIdCol;
    @FXML private TableColumn<TeacherData, String> deptCol;
    @FXML private TableColumn<TeacherData, String> designationCol;
    @FXML private TableColumn<TeacherData, String> statusCol;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterDept;
    @FXML private Label recordCount;

    private ObservableList<TeacherData> teacherList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        uniqueIdCol.setCellValueFactory(new PropertyValueFactory<>("uniqueId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        empIdCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        designationCol.setCellValueFactory(new PropertyValueFactory<>("designation"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadDepartments();
        loadTeachers();

        searchField.textProperty().addListener((obs, old, newVal) -> filterTeachers());
        filterDept.valueProperty().addListener((obs, old, newVal) -> filterTeachers());
    }

    private void loadDepartments() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT department FROM teachers WHERE department IS NOT NULL");
            ResultSet rs = stmt.executeQuery();

            filterDept.getItems().clear();
            filterDept.getItems().add("All Departments");
            while (rs.next()) {
                filterDept.getItems().add(rs.getString("department"));
            }
            filterDept.setValue("All Departments");

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadTeachers() {
        teacherList.clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT u.id, u.unique_id, u.full_name, u.email, u.is_active, " +
                    "t.employee_id, t.department, t.designation " +
                    "FROM users u INNER JOIN teachers t ON u.id = t.user_id ORDER BY u.full_name";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TeacherData teacher = new TeacherData(
                        rs.getInt("id"),
                        rs.getString("unique_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("employee_id"),
                        rs.getString("department"),
                        rs.getString("designation"),
                        rs.getBoolean("is_active")
                );
                teacherList.add(teacher);
            }

            rs.close();
            stmt.close();

            teacherTable.setItems(teacherList);
            updateRecordCount();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load teachers: " + e.getMessage());
        }
    }

    private void filterTeachers() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        String deptFilter = filterDept.getValue();

        ObservableList<TeacherData> filtered = FXCollections.observableArrayList();

        for (TeacherData teacher : teacherList) {
            boolean matchesSearch = searchTerm.isEmpty() ||
                    teacher.getFullName().toLowerCase().contains(searchTerm) ||
                    teacher.getUniqueId().toLowerCase().contains(searchTerm) ||
                    teacher.getEmployeeId().toLowerCase().contains(searchTerm);

            boolean matchesDept = deptFilter.equals("All Departments") ||
                    (teacher.getDepartment() != null && teacher.getDepartment().equals(deptFilter));

            if (matchesSearch && matchesDept) {
                filtered.add(teacher);
            }
        }

        teacherTable.setItems(filtered);
        updateRecordCount();
    }

    private void updateRecordCount() {
        int count = teacherTable.getItems().size();
        recordCount.setText("Showing " + count + " records");
    }

    @FXML
    private void refreshTeachers() {
        loadTeachers();
        showAlert("Info", "Teacher list refreshed!");
    }

    @FXML
    private void showAddTeacher() {
        showAlert("Info", "Add Teacher functionality will be implemented in the next version!");
    }

    @FXML
    private void editTeacher() {
        TeacherData selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a teacher to edit.");
            return;
        }
        showAlert("Info", "Edit Teacher: " + selected.getFullName() +
                "\nThis functionality will be implemented in the next version!");
    }

    @FXML
    private void deleteTeacher() {
        TeacherData selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a teacher to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Teacher");
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

                showAlert("Success", "Teacher deleted successfully!");
                loadTeachers();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete teacher: " + e.getMessage());
            }
        }
    }

    @FXML
    private void viewDetails() {
        TeacherData selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a teacher to view.");
            return;
        }
        showAlert("Teacher Details",
                "Name: " + selected.getFullName() +
                        "\nUnique ID: " + selected.getUniqueId() +
                        "\nEmail: " + selected.getEmail() +
                        "\nEmployee ID: " + selected.getEmployeeId() +
                        "\nDepartment: " + selected.getDepartment() +
                        "\nDesignation: " + selected.getDesignation() +
                        "\nStatus: " + selected.getStatus()
        );
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) teacherTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class TeacherData {
        private int userId;
        private String uniqueId;
        private String fullName;
        private String email;
        private String employeeId;
        private String department;
        private String designation;
        private boolean isActive;

        public TeacherData(int userId, String uniqueId, String fullName, String email,
                           String employeeId, String department, String designation, boolean isActive) {
            this.userId = userId;
            this.uniqueId = uniqueId;
            this.fullName = fullName;
            this.email = email;
            this.employeeId = employeeId;
            this.department = department;
            this.designation = designation;
            this.isActive = isActive;
        }

        public int getUserId() { return userId; }
        public String getUniqueId() { return uniqueId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getEmployeeId() { return employeeId; }
        public String getDepartment() { return department; }
        public String getDesignation() { return designation; }
        public String getStatus() { return isActive ? "Active" : "Inactive"; }
    }
}