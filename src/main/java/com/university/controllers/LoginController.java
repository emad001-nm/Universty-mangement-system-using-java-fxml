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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator loadingIndicator;

    @FXML
    public void initialize() {
        // Enter key support
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

        loadingIndicator.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both email and password!");
            return;
        }

        loginButton.setDisable(true);
        loadingIndicator.setVisible(true);
        errorLabel.setText("");

        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(event -> performLogin(email, password));
        pause.play();
    }

    private void performLogin(String email, String password) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            String query = "SELECT * FROM users WHERE email = ? AND password = ? AND is_active = 1";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUniqueId(rs.getString("unique_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setPhone(rs.getString("phone"));
                user.setGender(rs.getString("gender"));
                user.setActive(rs.getBoolean("is_active"));

                SessionManager.getInstance().setCurrentUser(user);

                errorLabel.setStyle("-fx-text-fill: #2ecc71;");
                errorLabel.setText("✓ Login successful! Redirecting...");

                Stage stage = (Stage) emailField.getScene().getWindow();

                PauseTransition redirect = new PauseTransition(Duration.seconds(0.5));
                redirect.setOnFinished(e -> {
                    stage.close();
                    openDashboard();
                });
                redirect.play();

            } else {
                errorLabel.setText("Invalid email or password!");
                loginButton.setDisable(false);
                loadingIndicator.setVisible(false);
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Database error: " + e.getMessage());
            loginButton.setDisable(false);
            loadingIndicator.setVisible(false);
        }
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("University Management System - Dashboard");
            stage.setScene(new Scene(root, 1100, 700));
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load dashboard: " + e.getMessage());
        }
    }
}