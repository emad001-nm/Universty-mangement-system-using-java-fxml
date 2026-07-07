// src/main/java/com/university/controllers/Teacher/MyCoursesController.java
package com.university.controllers.Teacher;

import com.university.models.DatabaseConnection;
import com.university.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MyCoursesController {
    @FXML private TableView<CourseData> courseTable;
    @FXML private TableColumn<CourseData, Integer> idCol;
    @FXML private TableColumn<CourseData, String> codeCol;
    @FXML private TableColumn<CourseData, String> nameCol;
    @FXML private TableColumn<CourseData, Integer> creditsCol;
    @FXML private TableColumn<CourseData, Integer> studentsCol;
    @FXML private TableColumn<CourseData, String> typeCol;

    private ObservableList<CourseData> courseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        creditsCol.setCellValueFactory(new PropertyValueFactory<>("credits"));
        studentsCol.setCellValueFactory(new PropertyValueFactory<>("studentCount"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        loadCourses();
    }

    @FXML
    private void loadCourses() {
        courseList.clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            int teacherId = SessionManager.getInstance().getTeacherId();

            String query = "SELECT c.*, " +
                    "(SELECT COUNT(*) FROM enrollments WHERE course_id = c.id) as student_count " +
                    "FROM courses c WHERE c.teacher_id = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CourseData course = new CourseData(
                        rs.getInt("id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("credits"),
                        rs.getInt("student_count"),
                        rs.getString("course_type")
                );
                courseList.add(course);
            }

            rs.close();
            stmt.close();
            courseTable.setItems(courseList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load courses: " + e.getMessage());
        }
    }

    @FXML
    private void viewStudents() {
        CourseData selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a course.");
            return;
        }
        showAlert("Students", "Viewing students for: " + selected.getName() +
                "\nTotal Students: " + selected.getStudentCount());
    }

    @FXML
    private void viewDetails() {
        CourseData selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a course.");
            return;
        }
        showAlert("Course Details",
                "Course: " + selected.getName() +
                        "\nCode: " + selected.getCode() +
                        "\nCredits: " + selected.getCredits() +
                        "\nType: " + selected.getType() +
                        "\nStudents: " + selected.getStudentCount());
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
        private int studentCount;
        private String type;

        public CourseData(int id, String code, String name, int credits, int studentCount, String type) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.credits = credits;
            this.studentCount = studentCount;
            this.type = type;
        }

        public int getId() { return id; }
        public String getCode() { return code; }
        public String getName() { return name; }
        public int getCredits() { return credits; }
        public int getStudentCount() { return studentCount; }
        public String getType() { return type; }
    }
}