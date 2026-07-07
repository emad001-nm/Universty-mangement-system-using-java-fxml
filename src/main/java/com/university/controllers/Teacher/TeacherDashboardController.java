// src/main/java/com/university/controllers/Teacher/TeacherDashboardController.java
package com.university.controllers.Teacher;

import com.university.models.DatabaseConnection;
import com.university.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TeacherDashboardController {
    @FXML private StackPane contentArea;
    @FXML private Label userLabel;
    @FXML private Label roleLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label totalStudents;
    @FXML private Label totalCourses;
    @FXML private Label todayClasses;
    @FXML private Label department;
    @FXML private Label designation;

    @FXML
    public void initialize() {
        try {
            SessionManager session = SessionManager.getInstance();
            if (session.isLoggedIn()) {
                if (userLabel != null) userLabel.setText(session.getFullName());
                if (roleLabel != null) roleLabel.setText("Teacher");
                if (welcomeLabel != null) welcomeLabel.setText("Welcome back, " + session.getFullName() + "!");
                loadTeacherInfo();
                loadStatistics();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTeacherInfo() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            int userId = SessionManager.getInstance().getUserId();

            String query = "SELECT t.*, d.dept_name " +
                    "FROM teachers t " +
                    "LEFT JOIN departments d ON t.department_id = d.id " +
                    "WHERE t.user_id = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                if (department != null) {
                    department.setText(rs.getString("dept_name") != null ? rs.getString("dept_name") : "N/A");
                }
                if (designation != null) {
                    designation.setText(rs.getString("qualification") != null ? rs.getString("qualification") : "N/A");
                }
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStatistics() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            int teacherId = SessionManager.getInstance().getTeacherId();

            if (teacherId > 0) {
                // Get total students
                String studentQuery = "SELECT COUNT(DISTINCT e.student_id) as total " +
                        "FROM enrollments e " +
                        "JOIN courses c ON e.course_id = c.id " +
                        "WHERE c.teacher_id = ?";
                PreparedStatement stmt1 = conn.prepareStatement(studentQuery);
                stmt1.setInt(1, teacherId);
                ResultSet rs1 = stmt1.executeQuery();
                if (rs1.next() && totalStudents != null) {
                    totalStudents.setText(String.valueOf(rs1.getInt("total")));
                }
                rs1.close();
                stmt1.close();

                // Get total courses
                String courseQuery = "SELECT COUNT(*) as total FROM courses WHERE teacher_id = ?";
                PreparedStatement stmt2 = conn.prepareStatement(courseQuery);
                stmt2.setInt(1, teacherId);
                ResultSet rs2 = stmt2.executeQuery();
                if (rs2.next() && totalCourses != null) {
                    totalCourses.setText(String.valueOf(rs2.getInt("total")));
                }
                rs2.close();
                stmt2.close();

                // Get today's classes
                String classQuery = "SELECT COUNT(*) as total FROM class_schedule " +
                        "WHERE teacher_id = ? AND day_of_week = DAYNAME(CURDATE())";
                PreparedStatement stmt3 = conn.prepareStatement(classQuery);
                stmt3.setInt(1, teacherId);
                ResultSet rs3 = stmt3.executeQuery();
                if (rs3.next() && todayClasses != null) {
                    todayClasses.setText(String.valueOf(rs3.getInt("total")));
                }
                rs3.close();
                stmt3.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("University Management System - Login");
            stage.setScene(new Scene(root, 450, 500));
            stage.setResizable(false);
            stage.show();

            Stage currentStage = (Stage) userLabel.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDashboard() {
        loadView("/fxml/teacher/dashboard_view.fxml");
    }

    @FXML
    private void showMyCourses() {
        loadView("/fxml/teacher/my_courses.fxml");
    }

    @FXML
    private void showAttendanceMark() {
        loadView("/fxml/teacher/attendance_mark.fxml");
    }

    @FXML
    private void showGradeManagement() {
        loadView("/fxml/teacher/grade_management.fxml");
    }

    @FXML
    private void showSchedule() {
        loadView("/fxml/teacher/schedule_view.fxml");
    }

    @FXML
    private void showProfile() {
        loadView("/fxml/teacher/profile.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            if (contentArea != null) {
                contentArea.getChildren().clear();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent view = loader.load();
                contentArea.getChildren().add(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (contentArea != null) {
                Label errorLabel = new Label("Failed to load: " + fxmlPath + "\n" + e.getMessage());
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-padding: 20;");
                contentArea.getChildren().add(errorLabel);
            }
        }
    }
}