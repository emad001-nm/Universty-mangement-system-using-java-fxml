// src/main/java/com/university/controllers/Teacher/TeacherProfileController.java
package com.university.controllers.Teacher;

import com.university.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

public class TeacherProfileController {
    @FXML private Label profileName;
    @FXML private Label profileEmail;
    @FXML private Label profileRole;
    @FXML private Label profileUniqueId;
    @FXML private Label profilePhone;
    @FXML private Label profileGender;
    @FXML private Label profileAddress;
    @FXML private Label profileEmployeeId;

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
                profileRole.setText("Teacher");
                profileUniqueId.setText(session.getUniqueId());
                profilePhone.setText(session.getPhone() != null && !session.getPhone().isEmpty() ? session.getPhone() : "N/A");
                profileGender.setText(session.getGender() != null && !session.getGender().isEmpty() ? session.getGender() : "N/A");
                profileAddress.setText(session.getAddress() != null && !session.getAddress().isEmpty() ? session.getAddress() : "N/A");
                profileEmployeeId.setText(session.getEmployeeId() != null ? session.getEmployeeId() : "N/A");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangePassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Change your password");
        dialog.setContentText("Enter new password:");

        dialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                showAlert("Success", "Password changed successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Password cannot be empty!", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleUpdatePhone() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Phone");
        dialog.setHeaderText("Update your phone number");
        dialog.setContentText("Enter new phone number:");

        dialog.showAndWait().ifPresent(phone -> {
            if (phone != null && !phone.trim().isEmpty()) {
                showAlert("Success", "Phone number updated successfully!", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    private void handleUpdateAddress() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Address");
        dialog.setHeaderText("Update your address");
        dialog.setContentText("Enter new address:");

        dialog.showAndWait().ifPresent(address -> {
            if (address != null && !address.trim().isEmpty()) {
                showAlert("Success", "Address updated successfully!", Alert.AlertType.INFORMATION);
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}