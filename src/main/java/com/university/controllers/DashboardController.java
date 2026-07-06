// src/main/java/com/university/controllers/DashboardController.java
package com.university.controllers;

import com.university.models.DatabaseConnection;
import com.university.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardController {
    @FXML private Label userLabel;
    @FXML private Label roleLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label statsStudents;
    @FXML private Label statsTeachers;
    @FXML private Label statsCourses;
    @FXML private Button studentBtn;
    @FXML private Button teacherBtn;

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        if (session.isLoggedIn()) {
            String fullName = session.getCurrentUser().getFullName();
            String role = session.getCurrentUser().getRole();
            userLabel.setText(fullName);
            roleLabel.setText(role.toUpperCase());
            welcomeLabel.setText("Welcome back, " + fullName + "!");

            // Hide buttons based on role
            if ("student".equals(role)) {
                studentBtn.setVisible(false);
                teacherBtn.setVisible(false);
            } else if ("teacher".equals(role)) {
                teacherBtn.setVisible(false);
            }

            loadStatistics();
        }
    }

    private void loadStatistics() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            // Get student count
            PreparedStatement stmt1 = conn.prepareStatement("SELECT COUNT(*) FROM students");
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next()) statsStudents.setText(String.valueOf(rs1.getInt(1)));
            rs1.close();
            stmt1.close();

            // Get teacher count
            PreparedStatement stmt2 = conn.prepareStatement("SELECT COUNT(*) FROM teachers");
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) statsTeachers.setText(String.valueOf(rs2.getInt(1)));
            rs2.close();
            stmt2.close();

            // Get course count
            PreparedStatement stmt3 = conn.prepareStatement("SELECT COUNT(*) FROM courses");
            ResultSet rs3 = stmt3.executeQuery();
            if (rs3.next()) statsCourses.setText(String.valueOf(rs3.getInt(1)));
            rs3.close();
            stmt3.close();

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
        welcomeLabel.setText("Welcome back, " + SessionManager.getInstance().getCurrentUser().getFullName() + "!");
        loadStatistics();
    }

    @FXML
    private void showStudents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Student Management");
            stage.setScene(new Scene(root, 1000, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showTeachers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/teacher.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Teacher Management");
            stage.setScene(new Scene(root, 1000, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showCourses() {
        showInfo("Courses Management", "Course management functionality will be available soon!");
    }

    @FXML
    private void showEnrollments() {
        showInfo("Enrollments", "Enrollment management functionality will be available soon!");
    }

    @FXML
    private void showSettings() {
        showInfo("Settings", "Settings functionality will be available soon!");
    }

    private void showInfo(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}