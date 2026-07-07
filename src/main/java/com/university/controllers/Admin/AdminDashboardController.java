// src/main/java/com/university/controllers/Admin/AdminDashboardController.java
package com.university.controllers.Admin;

import com.university.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AdminDashboardController {
    @FXML private StackPane contentArea;
    @FXML private Label userLabel;
    @FXML private Label roleLabel;

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        if (session.isLoggedIn()) {
            userLabel.setText(session.getFullName());
            roleLabel.setText("Administrator");
            showDashboard();
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
        loadView("/fxml/admin/dashboard_view.fxml");
    }

    @FXML
    private void showUserManagement() {
        loadView("/fxml/admin/user_management.fxml");
    }

    @FXML
    private void showDepartmentManagement() {
        loadView("/fxml/admin/department_management.fxml");
    }

    @FXML
    private void showCourseManagement() {
        loadView("/fxml/admin/course_management.fxml");
    }

    @FXML
    private void showEnrollmentManagement() {
        loadView("/fxml/admin/enrollment_management.fxml");
    }

    @FXML
    private void showFeeManagement() {
        loadView("/fxml/admin/fee_management.fxml");
    }

    @FXML
    private void showReports() {
        loadView("/fxml/admin/reports.fxml");
    }

    @FXML
    private void showSystemManagement() {
        loadView("/fxml/admin/system_management.fxml");
    }

    @FXML
    private void showProfile() {
        loadView("/fxml/admin/profile.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Failed to load: " + fxmlPath + "\n" + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-padding: 20;");
            contentArea.getChildren().add(errorLabel);
        }
    }
}