// src/main/java/com/university/controllers/AdminDashboardController.java
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

public class AdminDashboardController {
    @FXML private Label userLabel;
    @FXML private Label roleLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label totalStudents;
    @FXML private Label totalTeachers;
    @FXML private Label totalCourses;
    @FXML private Label totalDepartments;
    @FXML private Label pendingFees;
    @FXML private Label todayAttendance;

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        if (session.isLoggedIn()) {
            userLabel.setText(session.getFullName());
            roleLabel.setText("Administrator");
            welcomeLabel.setText("Welcome back, " + session.getFullName() + "!");
            loadStatistics();
        }
    }

    private void loadStatistics() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            PreparedStatement stmt1 = conn.prepareStatement("SELECT COUNT(*) FROM students");
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next()) totalStudents.setText(String.valueOf(rs1.getInt(1)));
            rs1.close();
            stmt1.close();

            PreparedStatement stmt2 = conn.prepareStatement("SELECT COUNT(*) FROM teachers");
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) totalTeachers.setText(String.valueOf(rs2.getInt(1)));
            rs2.close();
            stmt2.close();

            PreparedStatement stmt3 = conn.prepareStatement("SELECT COUNT(*) FROM courses");
            ResultSet rs3 = stmt3.executeQuery();
            if (rs3.next()) totalCourses.setText(String.valueOf(rs3.getInt(1)));
            rs3.close();
            stmt3.close();

            PreparedStatement stmt4 = conn.prepareStatement("SELECT COUNT(*) FROM departments");
            ResultSet rs4 = stmt4.executeQuery();
            if (rs4.next()) totalDepartments.setText(String.valueOf(rs4.getInt(1)));
            rs4.close();
            stmt4.close();

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

    @FXML private void showUserManagement() {
        showInfo("User Management", "Add/Edit/Delete users functionality will be available soon.");
    }

    @FXML private void showDepartmentManagement() {
        showInfo("Department Management", "Manage departments functionality will be available soon.");
    }

    @FXML private void showCourseManagement() {
        showInfo("Course Management", "Manage courses functionality will be available soon.");
    }

    @FXML private void showReports() {
        showInfo("Reports", "Generate reports functionality will be available soon.");
    }

    private void showInfo(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}