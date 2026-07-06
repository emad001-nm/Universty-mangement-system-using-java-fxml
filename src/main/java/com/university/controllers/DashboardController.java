// src/main/java/com/university/controllers/DashboardController.java
package com.university.controllers;

import com.university.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DashboardController {
    @FXML private Label userLabel;
    @FXML private Label welcomeLabel;
    @FXML private Button studentBtn;
    @FXML private Button teacherBtn;

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        if (session.isLoggedIn()) {
            String fullName = session.getCurrentUser().getFullName();
            String role = session.getCurrentUser().getRole();
            userLabel.setText("Welcome, " + fullName + " (" + role + ")");
            welcomeLabel.setText("Hello, " + fullName + "!");

            // Hide buttons based on role
            if (session.isStudent()) {
                studentBtn.setVisible(false);
                teacherBtn.setVisible(false);
            } else if (session.isTeacher()) {
                teacherBtn.setVisible(false);
            }
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login - University Management System");
            stage.setScene(new Scene(root, 600, 500));
            stage.setResizable(false);
            stage.show();

            // Close current window
            Stage currentStage = (Stage) userLabel.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDashboard() {
        // Refresh dashboard or show welcome
    }

    @FXML
    private void showStudents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Student Management");
            stage.setScene(new Scene(root, 800, 600));
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
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showCourses() {
        System.out.println("Show courses management");
    }
}