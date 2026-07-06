// src/main/java/com/university/controllers/TeacherController.java
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

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

    private ObservableList<TeacherData> teacherList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup columns
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
                    "FROM users u " +
                    "INNER JOIN teachers t ON u.id = t.user_id " +
                    "ORDER BY u.full_name";

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

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load teachers: " + e.getMessage());
        }
    }

    @FXML
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
                    teacher.getDepartment().equals(deptFilter);

            if (matchesSearch && matchesDept) {
                filtered.add(teacher);
            }
        }

        teacherTable.setItems(filtered);
    }

    @FXML
    private void showAddTeacher() {
        Dialog<TeacherData> dialog = new Dialog<>();
        dialog.setTitle("Add New Teacher");
        dialog.setHeaderText("Enter teacher details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

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
        TextField employeeId = new TextField();
        employeeId.setPromptText("Employee ID");
        TextField department = new TextField();
        department.setPromptText("Department");
        TextField designation = new TextField();
        designation.setPromptText("Designation");

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullName, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(email, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(password, 1, 2);
        grid.add(new Label("Employee ID:"), 0, 3);
        grid.add(employeeId, 1, 3);
        grid.add(new Label("Department:"), 0, 4);
        grid.add(department, 1, 4);
        grid.add(new Label("Designation:"), 0, 5);
        grid.add(designation, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new TeacherData(0, null, fullName.getText(), email.getText(),
                        employeeId.getText(), department.getText(), designation.getText(), true);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(teacherData -> {
            saveTeacher(teacherData, password.getText());
        });
    }

    private void saveTeacher(TeacherData teacherData, String password) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            String uniqueId = "TCH" + System.currentTimeMillis() % 1000000;

            String userQuery = "INSERT INTO users (unique_id, full_name, email, password, role) " +
                    "VALUES (?, ?, ?, ?, 'teacher')";
            PreparedStatement userStmt = conn.prepareStatement(userQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, uniqueId);
            userStmt.setString(2, teacherData.getFullName());
            userStmt.setString(3, teacherData.getEmail());
            userStmt.setString(4, password);

            int affected = userStmt.executeUpdate();
            if (affected > 0) {
                ResultSet keys = userStmt.getGeneratedKeys();
                if (keys.next()) {
                    int userId = keys.getInt(1);

                    String teacherQuery = "INSERT INTO teachers (user_id, employee_id, department, designation) " +
                            "VALUES (?, ?, ?, ?)";
                    PreparedStatement teacherStmt = conn.prepareStatement(teacherQuery);
                    teacherStmt.setInt(1, userId);
                    teacherStmt.setString(2, teacherData.getEmployeeId());
                    teacherStmt.setString(3, teacherData.getDepartment());
                    teacherStmt.setString(4, teacherData.getDesignation());

                    teacherStmt.executeUpdate();
                    teacherStmt.close();

                    conn.commit();
                    showAlert("Success", "Teacher added successfully!\nUnique ID: " + uniqueId);
                    loadTeachers();
                }
            }

            userStmt.close();
            conn.setAutoCommit(true);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add teacher: " + e.getMessage());
        }
    }

    @FXML
    private void editTeacher() {
        TeacherData selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a teacher to edit.");
            return;
        }

        // Similar edit dialog as student
        showAlert("Info", "Edit functionality for teachers coming soon!");
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
        alert.setHeaderText("Confirm Delete");
        alert.setContentText("Are you sure you want to delete " + selected.getFullName() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = DatabaseConnection.getInstance().getConnection();
                String query = "UPDATE users SET is_active = 0 WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, selected.getUserId());

                stmt.executeUpdate();
                stmt.close();

                showAlert("Success", "Teacher deactivated successfully!");
                loadTeachers();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete teacher: " + e.getMessage());
            }
        }
    }

    @FXML
    private void refreshTeachers() {
        loadTeachers();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) teacherTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
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

        // Getters
        public int getUserId() { return userId; }
        public String getUniqueId() { return uniqueId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getEmployeeId() { return employeeId; }
        public String getDepartment() { return department; }
        public String getDesignation() { return designation; }
        public String getStatus() { return isActive ? "Active" : "Inactive"; }

        // Setters
        public void setUserId(int userId) { this.userId = userId; }
        public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public void setEmail(String email) { this.email = email; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        public void setDepartment(String department) { this.department = department; }
        public void setDesignation(String designation) { this.designation = designation; }
        public void setActive(boolean active) { isActive = active; }
    }
}