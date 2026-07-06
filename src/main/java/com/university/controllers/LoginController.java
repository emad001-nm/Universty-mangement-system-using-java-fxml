// src/main/java/com/university/controllers/LoginController.java
package com.university.controllers;

import com.university.models.DatabaseConnection;
import com.university.models.User;
import com.university.utils.SessionManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    @FXML private VBox loginContainer;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator loadingIndicator;

    // Inner class to hold extended user data
    public static class ExtendedUser extends User {
        private Integer studentId;
        private String rollNumber;
        private Integer teacherId;
        private String employeeId;
        private Integer adminId;
        private String adminIdCode;

        // Getters and Setters for extended fields
        public Integer getStudentId() { return studentId; }
        public void setStudentId(Integer studentId) { this.studentId = studentId; }

        public String getRollNumber() { return rollNumber; }
        public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

        public Integer getTeacherId() { return teacherId; }
        public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }

        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

        public Integer getAdminId() { return adminId; }
        public void setAdminId(Integer adminId) { this.adminId = adminId; }

        public String getAdminIdCode() { return adminIdCode; }
        public void setAdminIdCode(String adminIdCode) { this.adminIdCode = adminIdCode; }
    }

    @FXML
    public void initialize() {
        // Set up enter key handler
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });

        emailField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });

        // Initially hide loading indicator
        loadingIndicator.setVisible(false);

        // Add focus effect
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                emailField.getStyleClass().add("login-field-focused");
            } else {
                emailField.getStyleClass().remove("login-field-focused");
            }
        });
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password!");
            return;
        }

        // Show loading
        loginButton.setDisable(true);
        loadingIndicator.setVisible(true);
        errorLabel.setText("");

        // Simulate async login
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(event -> performLogin(email, password));
        pause.play();
    }

    private void performLogin(String email, String password) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            // Simpler query without JOIN to avoid issues
            String query = "SELECT * FROM users WHERE email = ? AND password = ? AND is_active = 1";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Create ExtendedUser object
                ExtendedUser user = new ExtendedUser();
                user.setId(rs.getInt("id"));
                user.setUniqueId(rs.getString("unique_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setGender(rs.getString("gender"));
                user.setActive(rs.getBoolean("is_active"));

                // Get role-specific data in separate queries
                String role = user.getRole();
                if ("student".equals(role)) {
                    String studentQuery = "SELECT id as student_id, roll_number FROM students WHERE user_id = ?";
                    PreparedStatement studentStmt = conn.prepareStatement(studentQuery);
                    studentStmt.setInt(1, user.getId());
                    ResultSet studentRs = studentStmt.executeQuery();
                    if (studentRs.next()) {
                        user.setStudentId(studentRs.getInt("student_id"));
                        user.setRollNumber(studentRs.getString("roll_number"));
                    }
                    studentRs.close();
                    studentStmt.close();

                } else if ("teacher".equals(role)) {
                    String teacherQuery = "SELECT id as teacher_id, employee_id FROM teachers WHERE user_id = ?";
                    PreparedStatement teacherStmt = conn.prepareStatement(teacherQuery);
                    teacherStmt.setInt(1, user.getId());
                    ResultSet teacherRs = teacherStmt.executeQuery();
                    if (teacherRs.next()) {
                        user.setTeacherId(teacherRs.getInt("teacher_id"));
                        user.setEmployeeId(teacherRs.getString("employee_id"));
                    }
                    teacherRs.close();
                    teacherStmt.close();

                } else if ("admin".equals(role)) {
                    String adminQuery = "SELECT id as admin_id, admin_id as admin_code FROM admins WHERE user_id = ?";
                    PreparedStatement adminStmt = conn.prepareStatement(adminQuery);
                    adminStmt.setInt(1, user.getId());
                    ResultSet adminRs = adminStmt.executeQuery();
                    if (adminRs.next()) {
                        user.setAdminId(adminRs.getInt("admin_id"));
                        user.setAdminIdCode(adminRs.getString("admin_code"));
                    }
                    adminRs.close();
                    adminStmt.close();
                }

                // Login successful
                SessionManager.getInstance().setCurrentUser(user);

                // Show success
                errorLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                errorLabel.setText("✓ Login successful! Redirecting...");

                // Close login window and open dashboard
                Stage stage = (Stage) emailField.getScene().getWindow();

                PauseTransition redirect = new PauseTransition(Duration.seconds(0.5));
                redirect.setOnFinished(e -> {
                    stage.close();
                    openDashboard();
                });
                redirect.play();

            } else {
                // Check if user exists but is inactive
                String checkQuery = "SELECT is_active FROM users WHERE email = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, email);
                ResultSet checkRs = checkStmt.executeQuery();

                if (checkRs.next() && !checkRs.getBoolean("is_active")) {
                    showError("Account is deactivated. Please contact administrator.");
                } else {
                    showError("Invalid email or password!");
                }
                checkRs.close();
                checkStmt.close();

                // Re-enable login
                loginButton.setDisable(false);
                loadingIndicator.setVisible(false);
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
            loginButton.setDisable(false);
            loadingIndicator.setVisible(false);
        }
    }

    private void showError(String message) {
        errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        errorLabel.setText("✗ " + message);
        loginButton.setDisable(false);
        loadingIndicator.setVisible(false);

        // Shake animation for error
        errorLabel.setScaleX(1.0);
        PauseTransition shake = new PauseTransition(Duration.millis(100));
        shake.setOnFinished(e -> {
            errorLabel.setScaleX(1.1);
            PauseTransition shake2 = new PauseTransition(Duration.millis(100));
            shake2.setOnFinished(e2 -> errorLabel.setScaleX(1.0));
            shake2.play();
        });
        shake.play();
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("University Management System - Dashboard");
            stage.setScene(new Scene(root, 1200, 700));
            stage.setMinWidth(1024);
            stage.setMinHeight(600);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load dashboard: " + e.getMessage());
        }
    }
}