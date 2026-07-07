// src/main/java/com/university/controllers/StudentDashboardController.java
package com.university.controllers;

import com.university.models.DatabaseConnection;
import com.university.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentDashboardController {
    @FXML private Label userLabel;
    @FXML private Label roleLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label studentName;
    @FXML private Label rollNumber;
    @FXML private Label semester;
    @FXML private Label cgpa;
    @FXML private Label totalCredits;
    @FXML private Label attendancePercentage;

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        if (session.isLoggedIn()) {
            userLabel.setText(session.getFullName());
            roleLabel.setText("Student");
            welcomeLabel.setText("Welcome back, " + session.getFullName() + "!");
            loadStudentInfo();
        }
    }

    private void loadStudentInfo() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            int userId = SessionManager.getInstance().getCurrentUser().getId();

            String query = "SELECT s.*, u.full_name " +
                    "FROM students s " +
                    "JOIN users u ON s.user_id = u.id " +
                    "WHERE s.user_id = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                studentName.setText(rs.getString("full_name"));
                rollNumber.setText(rs.getString("roll_number"));
                semester.setText("Semester " + rs.getInt("current_semester"));
                cgpa.setText(String.format("%.2f", rs.getDouble("cgpa")));
                totalCredits.setText(String.valueOf(rs.getInt("total_credits")));
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

    @FXML private void showEnrolledCourses() {
        showInfo("My Courses", "Your enrolled courses will be displayed here.");
    }

    @FXML private void showAttendance() {
        showInfo("Attendance", "Your attendance records will be displayed here.");
    }

    @FXML private void showResults() {
        showInfo("Results", "Your academic results will be displayed here.");
    }

    @FXML private void showFees() {
        showInfo("Fees", "Your fee details will be displayed here.");
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