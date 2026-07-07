// src/main/java/com/university/controllers/EnrollmentManagementController.java
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
import java.time.LocalDate;

public class EnrollmentManagementController {
    @FXML private TableView<EnrollmentData> enrollmentTable;
    @FXML private TableColumn<EnrollmentData, Integer> enrollIdCol;
    @FXML private TableColumn<EnrollmentData, String> enrollStudentCol;
    @FXML private TableColumn<EnrollmentData, String> enrollCourseCol;
    @FXML private TableColumn<EnrollmentData, String> enrollDateCol;
    @FXML private TableColumn<EnrollmentData, String> enrollGradeCol;
    @FXML private TableColumn<EnrollmentData, String> enrollStatusCol;
    @FXML private TextField searchField;
    @FXML private Label recordCount;

    private ObservableList<EnrollmentData> enrollmentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        enrollIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        enrollStudentCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        enrollCourseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        enrollDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        enrollGradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        enrollStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        searchField.textProperty().addListener((obs, old, newVal) -> filterEnrollments());

        loadEnrollments();
    }

    @FXML
    private void loadEnrollments() {
        enrollmentList.clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT e.*, u.full_name as student_name, c.course_name, " +
                    "CASE WHEN e.is_completed = 1 THEN 'Completed' ELSE 'Active' END as status " +
                    "FROM enrollments e " +
                    "JOIN students s ON e.student_id = s.id " +
                    "JOIN users u ON s.user_id = u.id " +
                    "JOIN courses c ON e.course_id = c.id " +
                    "ORDER BY e.enrollment_date DESC";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                EnrollmentData enroll = new EnrollmentData(
                        rs.getInt("id"),
                        rs.getString("student_name"),
                        rs.getString("course_name"),
                        rs.getDate("enrollment_date").toString(),
                        rs.getString("grade") != null ? rs.getString("grade") : "N/A",
                        rs.getString("status")
                );
                enrollmentList.add(enroll);
            }

            rs.close();
            stmt.close();
            enrollmentTable.setItems(enrollmentList);
            updateRecordCount();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load enrollments: " + e.getMessage());
        }
    }

    @FXML
    private void filterEnrollments() {
        String search = searchField.getText().toLowerCase();
        ObservableList<EnrollmentData> filtered = FXCollections.observableArrayList();

        for (EnrollmentData enroll : enrollmentList) {
            if (search.isEmpty() ||
                    enroll.getStudentName().toLowerCase().contains(search) ||
                    enroll.getCourseName().toLowerCase().contains(search)) {
                filtered.add(enroll);
            }
        }

        enrollmentTable.setItems(filtered);
        updateRecordCount();
    }

    private void updateRecordCount() {
        int count = enrollmentTable.getItems().size();
        recordCount.setText("Showing " + count + " records");
    }

    @FXML
    private void refreshEnrollments() {
        loadEnrollments();
        showAlert("Info", "Enrollments refreshed!");
    }

    @FXML
    private void enrollStudent() {
        Dialog<EnrollmentData> dialog = new Dialog<>();
        dialog.setTitle("Enroll Student");
        dialog.setHeaderText("Enroll a student in a course");

        ButtonType saveButton = new ButtonType("Enroll", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<String> studentCombo = new ComboBox<>();
        ComboBox<String> courseCombo = new ComboBox<>();

        // Load students
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT u.full_name, s.id FROM students s JOIN users u ON s.user_id = u.id WHERE u.is_active = 1"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                studentCombo.getItems().add(rs.getString("full_name") + " (ID: " + rs.getInt("id") + ")");
            }
            rs.close();
            stmt.close();

            stmt = conn.prepareStatement("SELECT course_name, id FROM courses");
            rs = stmt.executeQuery();
            while (rs.next()) {
                courseCombo.getItems().add(rs.getString("course_name") + " (ID: " + rs.getInt("id") + ")");
            }
            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        grid.add(new Label("Student:"), 0, 0);
        grid.add(studentCombo, 1, 0);
        grid.add(new Label("Course:"), 0, 1);
        grid.add(courseCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                String student = studentCombo.getValue();
                String course = courseCombo.getValue();
                if (student != null && course != null) {
                    return new EnrollmentData(0, student, course, LocalDate.now().toString(), "N/A", "Active");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(enroll -> {
            saveEnrollment(enroll);
        });
    }

    private void saveEnrollment(EnrollmentData enroll) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            String student = enroll.getStudentName();
            String course = enroll.getCourseName();
            int studentId = Integer.parseInt(student.substring(student.lastIndexOf("ID: ") + 4, student.length() - 1));
            int courseId = Integer.parseInt(course.substring(course.lastIndexOf("ID: ") + 4, course.length() - 1));

            String query = "INSERT INTO enrollments (student_id, course_id, enrollment_date) VALUES (?, ?, CURDATE())";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);

            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "Student enrolled successfully!");
            loadEnrollments();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to enroll student: " + e.getMessage());
        }
    }

    @FXML
    private void editGrade() {
        EnrollmentData selected = enrollmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an enrollment to edit grade.");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Grade");
        dialog.setHeaderText("Edit grade for: " + selected.getStudentName() + " - " + selected.getCourseName());

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<String> gradeCombo = new ComboBox<>();
        gradeCombo.getItems().addAll("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F");
        gradeCombo.setValue(selected.getGrade());

        grid.add(new Label("Grade:"), 0, 0);
        grid.add(gradeCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                return gradeCombo.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(grade -> {
            updateGrade(selected.getId(), grade);
        });
    }

    private void updateGrade(int enrollmentId, String grade) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            double gradePoints = getGradePoints(grade);
            String query = "UPDATE enrollments SET grade = ?, grade_points = ?, is_completed = 1 WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, grade);
            stmt.setDouble(2, gradePoints);
            stmt.setInt(3, enrollmentId);

            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "Grade updated successfully!");
            loadEnrollments();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update grade: " + e.getMessage());
        }
    }

    private double getGradePoints(String grade) {
        switch (grade) {
            case "A+": return 4.0;
            case "A": return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B": return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C": return 2.0;
            case "D": return 1.0;
            case "F": return 0.0;
            default: return 0.0;
        }
    }

    @FXML
    private void removeEnrollment() {
        EnrollmentData selected = enrollmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an enrollment to remove.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Enrollment");
        alert.setContentText("Are you sure you want to remove this enrollment?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = DatabaseConnection.getInstance().getConnection();
                String query = "DELETE FROM enrollments WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
                stmt.close();

                showAlert("Success", "Enrollment removed successfully!");
                loadEnrollments();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to remove enrollment: " + e.getMessage());
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

    public static class EnrollmentData {
        private int id;
        private String studentName;
        private String courseName;
        private String date;
        private String grade;
        private String status;

        public EnrollmentData(int id, String studentName, String courseName, String date, String grade, String status) {
            this.id = id;
            this.studentName = studentName;
            this.courseName = courseName;
            this.date = date;
            this.grade = grade;
            this.status = status;
        }

        public int getId() { return id; }
        public String getStudentName() { return studentName; }
        public String getCourseName() { return courseName; }
        public String getDate() { return date; }
        public String getGrade() { return grade; }
        public String getStatus() { return status; }
    }
}