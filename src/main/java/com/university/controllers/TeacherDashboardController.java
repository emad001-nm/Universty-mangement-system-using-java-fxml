// src/main/java/com/university/controllers/TeacherDashboardController.java
package com.university.controllers;

import com.university.models.DatabaseConnection;
import com.university.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TeacherDashboardController {
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
        SessionManager session = SessionManager.getInstance();
        if (session.isLoggedIn()) {
            userLabel.setText(session.getFullName());
            roleLabel.setText("Teacher");
            welcomeLabel.setText("Welcome back, " + session.getFullName() + "!");
            loadTeacherInfo();
        }
    }

    private void loadTeacherInfo() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            int userId = SessionManager.getInstance().getCurrentUser().getId();

            String query = "SELECT t.*, d.dept_name " +
                    "FROM teachers t " +
                    "LEFT JOIN departments d ON t.department_id = d.id " +
                    "WHERE t.user_id = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                department.setText(rs.getString("dept_name") != null ? rs.getString("dept_name") : "N/A");
                designation.setText(rs.getString("qualification") != null ? rs.getString("qualification") : "N/A");
            }

            rs.close();
            stmt.close();

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

    @FXML private void showMyCourses() {
        showInfo("My Courses", "Your assigned courses will be displayed here.");
    }

    @FXML private void showAttendanceMark() {
        showInfo("Attendance Mark", "Mark attendance functionality will be available soon.");
    }

    @FXML private void showGradeManagement() {
        showInfo("Grade Management", "Manage student grades functionality will be available soon.");
    }

    @FXML private void showSchedule() {
        showInfo("Schedule", "Your class schedule will be displayed here.");
    }

    private void showInfo(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}