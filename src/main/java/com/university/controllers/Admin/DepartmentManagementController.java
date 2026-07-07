// src/main/java/com/university/controllers/DepartmentManagementController.java
package com.university.controllers.Admin;

import com.university.models.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DepartmentManagementController {
    @FXML private TableView<DepartmentData> deptTable;
    @FXML private TableColumn<DepartmentData, Integer> deptIdCol;
    @FXML private TableColumn<DepartmentData, String> deptCodeCol;
    @FXML private TableColumn<DepartmentData, String> deptNameCol;
    @FXML private TableColumn<DepartmentData, String> deptFacultyCol;
    @FXML private TableColumn<DepartmentData, String> deptHodCol;
    @FXML private TextField searchField;
    @FXML private Label recordCount;

    private ObservableList<DepartmentData> deptList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        deptIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        deptCodeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        deptNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        deptFacultyCol.setCellValueFactory(new PropertyValueFactory<>("faculty"));
        deptHodCol.setCellValueFactory(new PropertyValueFactory<>("hodName"));

        searchField.textProperty().addListener((obs, old, newVal) -> filterDepartments());

        loadDepartments();
    }

    @FXML
    private void loadDepartments() {
        deptList.clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT d.*, u.full_name as hod_name " +
                    "FROM departments d " +
                    "LEFT JOIN teachers t ON d.hod_id = t.id " +
                    "LEFT JOIN users u ON t.user_id = u.id " +
                    "ORDER BY d.dept_name";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DepartmentData dept = new DepartmentData(
                        rs.getInt("id"),
                        rs.getString("dept_code"),
                        rs.getString("dept_name"),
                        rs.getString("faculty"),
                        rs.getString("hod_name")
                );
                deptList.add(dept);
            }

            rs.close();
            stmt.close();
            deptTable.setItems(deptList);
            updateRecordCount();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load departments: " + e.getMessage());
        }
    }

    @FXML
    private void filterDepartments() {
        String search = searchField.getText().toLowerCase();
        ObservableList<DepartmentData> filtered = FXCollections.observableArrayList();

        for (DepartmentData dept : deptList) {
            if (search.isEmpty() ||
                    dept.getName().toLowerCase().contains(search) ||
                    dept.getCode().toLowerCase().contains(search) ||
                    dept.getFaculty().toLowerCase().contains(search)) {
                filtered.add(dept);
            }
        }

        deptTable.setItems(filtered);
        updateRecordCount();
    }

    private void updateRecordCount() {
        int count = deptTable.getItems().size();
        recordCount.setText("Showing " + count + " records");
    }

    @FXML
    private void refreshDepartments() {
        loadDepartments();
        showAlert("Info", "Departments refreshed!");
    }

    @FXML
    private void addDepartment() {
        Dialog<DepartmentData> dialog = new Dialog<>();
        dialog.setTitle("Add Department");
        dialog.setHeaderText("Create a new department");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField code = new TextField();
        code.setPromptText("Department Code (e.g., CS)");
        TextField name = new TextField();
        name.setPromptText("Department Name");
        TextField faculty = new TextField();
        faculty.setPromptText("Faculty");

        grid.add(new Label("Code:"), 0, 0);
        grid.add(code, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(name, 1, 1);
        grid.add(new Label("Faculty:"), 0, 2);
        grid.add(faculty, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                return new DepartmentData(0, code.getText(), name.getText(),
                        faculty.getText(), null);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(dept -> {
            saveDepartment(dept);
        });
    }

    private void saveDepartment(DepartmentData dept) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "INSERT INTO departments (dept_code, dept_name, faculty) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, dept.getCode().toUpperCase());
            stmt.setString(2, dept.getName());
            stmt.setString(3, dept.getFaculty());

            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "Department added successfully!");
            loadDepartments();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add department: " + e.getMessage());
        }
    }

    @FXML
    private void editDepartment() {
        DepartmentData selected = deptTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a department to edit.");
            return;
        }

        Dialog<DepartmentData> dialog = new Dialog<>();
        dialog.setTitle("Edit Department");
        dialog.setHeaderText("Edit: " + selected.getName());

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField code = new TextField(selected.getCode());
        TextField name = new TextField(selected.getName());
        TextField faculty = new TextField(selected.getFaculty());

        grid.add(new Label("Code:"), 0, 0);
        grid.add(code, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(name, 1, 1);
        grid.add(new Label("Faculty:"), 0, 2);
        grid.add(faculty, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                selected.setCode(code.getText());
                selected.setName(name.getText());
                selected.setFaculty(faculty.getText());
                return selected;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(dept -> {
            updateDepartment(dept);
        });
    }

    private void updateDepartment(DepartmentData dept) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "UPDATE departments SET dept_code = ?, dept_name = ?, faculty = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, dept.getCode().toUpperCase());
            stmt.setString(2, dept.getName());
            stmt.setString(3, dept.getFaculty());
            stmt.setInt(4, dept.getId());

            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "Department updated successfully!");
            loadDepartments();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update department: " + e.getMessage());
        }
    }

    @FXML
    private void assignHOD() {
        DepartmentData selected = deptTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a department to assign HOD.");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Assign HOD");
        dialog.setHeaderText("Assign Head of Department for: " + selected.getName());

        ButtonType assignButton = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<String> teacherCombo = new ComboBox<>();

        // Load teachers
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT u.full_name, t.id FROM teachers t JOIN users u ON t.user_id = u.id"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                teacherCombo.getItems().add(rs.getString("full_name") + " (ID: " + rs.getInt("id") + ")");
            }
            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        grid.add(new Label("Select Teacher:"), 0, 0);
        grid.add(teacherCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == assignButton) {
                return teacherCombo.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(teacher -> {
            if (teacher != null) {
                int teacherId = Integer.parseInt(teacher.substring(teacher.lastIndexOf("ID: ") + 4, teacher.length() - 1));
                assignHODToDepartment(selected.getId(), teacherId);
            }
        });
    }

    private void assignHODToDepartment(int deptId, int teacherId) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "UPDATE departments SET hod_id = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, teacherId);
            stmt.setInt(2, deptId);

            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "HOD assigned successfully!");
            loadDepartments();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to assign HOD: " + e.getMessage());
        }
    }

    @FXML
    private void deleteDepartment() {
        DepartmentData selected = deptTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a department to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Department");
        alert.setContentText("Are you sure you want to delete " + selected.getName() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = DatabaseConnection.getInstance().getConnection();
                String query = "DELETE FROM departments WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
                stmt.close();

                showAlert("Success", "Department deleted successfully!");
                loadDepartments();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete department: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class DepartmentData {
        private int id;
        private String code;
        private String name;
        private String faculty;
        private String hodName;

        public DepartmentData(int id, String code, String name, String faculty, String hodName) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.faculty = faculty;
            this.hodName = hodName != null ? hodName : "Not Assigned";
        }

        public int getId() { return id; }
        public String getCode() { return code; }
        public String getName() { return name; }
        public String getFaculty() { return faculty; }
        public String getHodName() { return hodName; }

        public void setCode(String code) { this.code = code; }
        public void setName(String name) { this.name = name; }
        public void setFaculty(String faculty) { this.faculty = faculty; }
    }
}