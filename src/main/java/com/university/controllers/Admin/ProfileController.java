// src/main/java/com/university/controllers/Admin/ProfileController.java
package com.university.controllers.Admin;

import com.university.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;

public class ProfileController {
    @FXML private Label profileName;
    @FXML private Label profileEmail;
    @FXML private Label profileRole;
    @FXML private Label profileUniqueId;
    @FXML private Label profilePhone;
    @FXML private Label profileGender;
    @FXML private Label profileAddress;
    @FXML private Button changePasswordBtn;

    @FXML
    public void initialize() {
        loadProfileData();
    }

    private void loadProfileData() {
        try {
            SessionManager session = SessionManager.getInstance();
            if (session.isLoggedIn()) {
                profileName.setText(session.getFullName());
                profileEmail.setText(session.getEmail());
                profileRole.setText(capitalize(session.getRole()));
                profileUniqueId.setText(session.getUniqueId());
                profilePhone.setText(session.getPhone() != null && !session.getPhone().isEmpty() ? session.getPhone() : "N/A");
                profileGender.setText(session.getGender() != null && !session.getGender().isEmpty() ? session.getGender() : "N/A");
                profileAddress.setText(session.getAddress() != null && !session.getAddress().isEmpty() ? session.getAddress() : "N/A");
            } else {
                setDefaultValues();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setErrorValues();
        }
    }

    private void setDefaultValues() {
        profileName.setText("Not logged in");
        profileEmail.setText("N/A");
        profileRole.setText("N/A");
        profileUniqueId.setText("N/A");
        profilePhone.setText("N/A");
        profileGender.setText("N/A");
        profileAddress.setText("N/A");
    }

    private void setErrorValues() {
        profileName.setText("Error loading profile");
        profileEmail.setText("Error");
        profileRole.setText("Error");
        profileUniqueId.setText("Error");
        profilePhone.setText("Error");
        profileGender.setText("Error");
        profileAddress.setText("Error");
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "N/A";
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    @FXML
    private void handleChangePassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Change your password");
        dialog.setContentText("Enter new password:");

        dialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                // Here you would update the password in the database
                showAlert("Success", "Password changed successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Password cannot be empty!", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleEditProfile() {
        showAlert("Info", "Edit profile functionality coming soon!", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}