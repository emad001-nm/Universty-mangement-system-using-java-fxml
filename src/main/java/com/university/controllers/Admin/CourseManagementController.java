// src/main/java/com/university/controllers/CourseManagementController.java
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

public class CourseManagementController {
    @FXML private TableView<CourseData> courseTable;
    @FXML private TableColumn<CourseData, Integer> courseIdCol;
    @FXML private TableColumn<CourseData, String> courseCodeCol;
    @FXML private TableColumn<CourseData, String> courseNameCol;
    @FXML private TableColumn<CourseData, Integer> courseCreditsCol;
    @FXML private TableColumn<CourseData, String> courseDeptCol;
    @FXML private TableColumn<CourseData, String> courseTeacherCol;
    @FXML private TableColumn<CourseData, Integer> courseStudentsCol;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterDeptCombo;
    @FXML private Label recordCount;

    private ObservableList<CourseData> courseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        courseIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        courseCodeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        courseCreditsCol.setCellValueFactory(new PropertyValueFactory<>("credits"));
        courseDeptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        courseTeacherCol.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        courseStudentsCol.setCellValueFactory(new PropertyValueFactory<>("studentCount"));

        searchField.textProperty().addListener((obs, old, newVal) -> filterCourses());
        filterDeptCombo.valueProperty().addListener((obs, old, newVal) -> filterCourses());

        loadDepartments();
        loadCourses();
    }

    private void loadDepartments() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT dept_name FROM departments ORDER BY dept_name");
            ResultSet rs = stmt.executeQuery();

            filterDeptCombo.getItems().clear();
            filterDeptCombo.getItems().add("All Departments");
            while (rs.next()) {
                filterDeptCombo.getItems().add(rs.getString("dept_name"));
            }
            filterDeptCombo.setValue("All Departments");

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadCourses() {
        courseList.clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT c.*, d.dept_name, u.full_name as teacher_name, " +
                    "(SELECT COUNT(*) FROM enrollments WHERE course_id = c.id) as enrolled " +
                    "FROM courses c " +
                    "LEFT JOIN departments d ON c.department_id = d.id " +
                    "LEFT JOIN teachers t ON c.teacher_id = t.id " +
                    "LEFT JOIN users u ON t.user_id = u.id " +
                    "ORDER BY c.course_name";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CourseData course = new CourseData(
                        rs.getInt("id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("credits"),
                        rs.getString("dept_name") != null ? rs.getString("dept_name") : "Not Assigned",
                        rs.getString("teacher_name") != null ? rs.getString("teacher_name") : "Not Assigned",
                        rs.getInt("enrolled")
                );
                courseList.add(course);
            }

            rs.close();
            stmt.close();
            courseTable.setItems(courseList);
            updateRecordCount();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load courses: " + e.getMessage());
        }
    }

    @FXML
    private void filterCourses() {
        String search = searchField.getText().toLowerCase();
        String deptFilter = filterDeptCombo.getValue();

        ObservableList<CourseData> filtered = FXCollections.observableArrayList();

        for (CourseData course : courseList) {
            boolean matchesSearch = search.isEmpty() ||
                    course.getName().toLowerCase().contains(search) ||
                    course.getCode().toLowerCase().contains(search);

            boolean matchesDept = deptFilter.equals("All Departments") ||
                    course.getDepartment().equals(deptFilter);

            if (matchesSearch && matchesDept) {
                filtered.add(course);
            }
        }

        courseTable.setItems(filtered);
        updateRecordCount();
    }

    private void updateRecordCount() {
        int count = courseTable.getItems().size();
        recordCount.setText("Showing " + count + " records");
    }

    @FXML
    private void refreshCourses() {
        loadCourses();
        showAlert("Info", "Courses refreshed!");
    }

    @FXML
    private void addCourse() {
        Dialog<CourseData> dialog = new Dialog<>();
        dialog.setTitle("Add Course");
        dialog.setHeaderText("Create a new course");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField code = new TextField();
        code.setPromptText("Course Code");
        TextField name = new TextField();
        name.setPromptText("Course Name");
        TextField credits = new TextField();
        credits.setPromptText("Credits");
        ComboBox<String> deptCombo = new ComboBox<>();

        // Load departments
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT id, dept_name FROM departments");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                deptCombo.getItems().add(rs.getString("dept_name") + " (ID: " + rs.getInt("id") + ")");
            }
            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        grid.add(new Label("Code:"), 0, 0);
        grid.add(code, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(name, 1, 1);
        grid.add(new Label("Credits:"), 0, 2);
        grid.add(credits, 1, 2);
        grid.add(new Label("Department:"), 0, 3);
        grid.add(deptCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                return new CourseData(0, code.getText(), name.getText(),
                        Integer.parseInt(credits.getText()),
                        deptCombo.getValue(), null, 0);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(course -> {
            saveCourse(course);
        });
    }

    private void saveCourse(CourseData course) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String deptStr = course.getDepartment();
            int deptId = 0;
            if (deptStr != null && !deptStr.equals("Not Assigned")) {
                deptId = Integer.parseInt(deptStr.substring(deptStr.lastIndexOf("ID: ") + 4, deptStr.length() - 1));
            }

            String query = "INSERT INTO courses (course_code, course_name, credits, department_id) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, course.getCode());
            stmt.setString(2, course.getName());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, deptId);

            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "Course added successfully!");
            loadCourses();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add course: " + e.getMessage());
        }
    }

    @FXML
    private void editCourse() {
        CourseData selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a course to edit.");
            return;
        }

        Dialog<CourseData> dialog = new Dialog<>();
        dialog.setTitle("Edit Course");
        dialog.setHeaderText("Edit: " + selected.getName());

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField code = new TextField(selected.getCode());
        TextField name = new TextField(selected.getName());
        TextField credits = new TextField(String.valueOf(selected.getCredits()));

        grid.add(new Label("Code:"), 0, 0);
        grid.add(code, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(name, 1, 1);
        grid.add(new Label("Credits:"), 0, 2);
        grid.add(credits, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                selected.setCode(code.getText());
                selected.setName(name.getText());
                selected.setCredits(Integer.parseInt(credits.getText()));
                return selected;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(course -> {
            updateCourse(course);
        });
    }

    private void updateCourse(CourseData course) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "UPDATE courses SET course_code = ?, course_name = ?, credits = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, course.getCode());
            stmt.setString(2, course.getName());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getId());

            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "Course updated successfully!");
            loadCourses();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update course: " + e.getMessage());
        }
    }

    @FXML
    private void assignTeacher() {
        CourseData selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a course to assign teacher.");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Assign Teacher");
        dialog.setHeaderText("Assign teacher to: " + selected.getName());

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
                assignTeacherToCourse(selected.getId(), teacherId);
            }
        });
    }

    private void assignTeacherToCourse(int courseId, int teacherId) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "UPDATE courses SET teacher_id = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, teacherId);
            stmt.setInt(2, courseId);

            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "Teacher assigned successfully!");
            loadCourses();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to assign teacher: " + e.getMessage());
        }
    }

    @FXML
    private void deleteCourse() {
        CourseData selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a course to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Course");
        alert.setContentText("Are you sure you want to delete " + selected.getName() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = DatabaseConnection.getInstance().getConnection();
                String query = "DELETE FROM courses WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
                stmt.close();

                showAlert("Success", "Course deleted successfully!");
                loadCourses();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete course: " + e.getMessage());
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

    public static class CourseData {
        private int id;
        private String code;
        private String name;
        private int credits;
        private String department;
        private String teacher;
        private int studentCount;

        public CourseData(int id, String code, String name, int credits, String department, String teacher, int studentCount) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.credits = credits;
            this.department = department;
            this.teacher = teacher;
            this.studentCount = studentCount;
        }

        public int getId() { return id; }
        public String getCode() { return code; }
        public String getName() { return name; }
        public int getCredits() { return credits; }
        public String getDepartment() { return department; }
        public String getTeacher() { return teacher; }
        public int getStudentCount() { return studentCount; }

        public void setCode(String code) { this.code = code; }
        public void setName(String name) { this.name = name; }
        public void setCredits(int credits) { this.credits = credits; }
        public void setDepartment(String department) { this.department = department; }
    }
}