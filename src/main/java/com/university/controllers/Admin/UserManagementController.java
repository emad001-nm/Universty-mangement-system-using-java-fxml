// src/main/java/com/university/controllers/UserManagementController.java
package com.university.controllers.Admin;

import com.university.models.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserManagementController {
    @FXML private TableView<UserData> userTable;
    @FXML private TableColumn<UserData, Integer> userIdCol;
    @FXML private TableColumn<UserData, String> userUniqueIdCol;
    @FXML private TableColumn<UserData, String> userFullNameCol;
    @FXML private TableColumn<UserData, String> userEmailCol;
    @FXML private TableColumn<UserData, String> userRoleCol;
    @FXML private TableColumn<UserData, String> userStatusCol;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterRoleCombo;
    @FXML private ComboBox<String> filterStatusCombo;
    @FXML private Label recordCount;

    private ObservableList<UserData> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize table columns
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userUniqueIdCol.setCellValueFactory(new PropertyValueFactory<>("uniqueId"));
        userFullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        userStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Setup filters
        filterRoleCombo.getItems().addAll("All Roles", "admin", "student", "teacher");
        filterRoleCombo.setValue("All Roles");
        filterStatusCombo.getItems().addAll("All Status", "Active", "Inactive");
        filterStatusCombo.setValue("All Status");

        // Add listeners
        searchField.textProperty().addListener((obs, old, newVal) -> filterUsers());
        filterRoleCombo.valueProperty().addListener((obs, old, newVal) -> filterUsers());
        filterStatusCombo.valueProperty().addListener((obs, old, newVal) -> filterUsers());

        loadUsers();
    }

    @FXML
    private void loadUsers() {
        userList.clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT id, unique_id, full_name, email, role, is_active FROM users ORDER BY id";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UserData user = new UserData(
                        rs.getInt("id"),
                        rs.getString("unique_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getBoolean("is_active")
                );
                userList.add(user);
            }

            rs.close();
            stmt.close();
            userTable.setItems(userList);
            updateRecordCount();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load users: " + e.getMessage());
        }
    }

    @FXML
    private void filterUsers() {
        String search = searchField.getText().toLowerCase();
        String roleFilter = filterRoleCombo.getValue();
        String statusFilter = filterStatusCombo.getValue();

        ObservableList<UserData> filtered = FXCollections.observableArrayList();

        for (UserData user : userList) {
            boolean matchesSearch = search.isEmpty() ||
                    user.getFullName().toLowerCase().contains(search) ||
                    user.getUniqueId().toLowerCase().contains(search) ||
                    user.getEmail().toLowerCase().contains(search);

            boolean matchesRole = roleFilter.equals("All Roles") ||
                    user.getRole().equals(roleFilter);

            boolean matchesStatus = statusFilter.equals("All Status") ||
                    (statusFilter.equals("Active") && user.isActive()) ||
                    (statusFilter.equals("Inactive") && !user.isActive());

            if (matchesSearch && matchesRole && matchesStatus) {
                filtered.add(user);
            }
        }

        userTable.setItems(filtered);
        updateRecordCount();
    }

    private void updateRecordCount() {
        int count = userTable.getItems().size();
        recordCount.setText("Showing " + count + " records");
    }

    @FXML
    private void refreshUsers() {
        loadUsers();
        showAlert("Info", "Users refreshed!");
    }

    @FXML
    private void addUser() {
        Dialog<UserData> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Create a new user account");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField fullName = new TextField();
        fullName.setPromptText("Full Name");
        TextField email = new TextField();
        email.setPromptText("Email");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        TextField phone = new TextField();
        phone.setPromptText("Phone");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("student", "teacher", "admin");
        roleCombo.setValue("student");

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullName, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(email, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(password, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phone, 1, 3);
        grid.add(new Label("Role:"), 0, 4);
        grid.add(roleCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                return new UserData(0, null, fullName.getText(), email.getText(),
                        roleCombo.getValue(), true);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            saveUser(user, password.getText(), phone.getText());
        });
    }

    private void saveUser(UserData user, String password, String phone) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            String uniqueId = user.getRole().toUpperCase().substring(0, 3) +
                    System.currentTimeMillis() % 1000000;

            String query = "INSERT INTO users (unique_id, full_name, email, password, role, phone) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, uniqueId);
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, password);
            stmt.setString(5, user.getRole());
            stmt.setString(6, phone);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    int userId = keys.getInt(1);

                    String roleQuery = "";
                    switch (user.getRole()) {
                        case "student":
                            roleQuery = "INSERT INTO students (user_id, roll_number, admission_date) VALUES (?, ?, CURDATE())";
                            break;
                        case "teacher":
                            roleQuery = "INSERT INTO teachers (user_id, employee_id, hire_date) VALUES (?, ?, CURDATE())";
                            break;
                        case "admin":
                            roleQuery = "INSERT INTO admins (user_id, admin_code, position) VALUES (?, ?, 'Staff')";
                            break;
                    }

                    if (!roleQuery.isEmpty()) {
                        PreparedStatement roleStmt = conn.prepareStatement(roleQuery);
                        roleStmt.setInt(1, userId);
                        roleStmt.setString(2, uniqueId);
                        roleStmt.executeUpdate();
                        roleStmt.close();
                    }

                    conn.commit();
                    showAlert("Success", "User added successfully!\nUnique ID: " + uniqueId);
                    loadUsers();
                }
            }

            stmt.close();
            conn.setAutoCommit(true);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add user: " + e.getMessage());
        }
    }

    @FXML
    private void editUser() {
        UserData selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a user to edit.");
            return;
        }

        Dialog<UserData> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user: " + selected.getFullName());

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField fullName = new TextField(selected.getFullName());
        TextField email = new TextField(selected.getEmail());
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("student", "teacher", "admin");
        roleCombo.setValue(selected.getRole());

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullName, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(email, 1, 1);
        grid.add(new Label("Role:"), 0, 2);
        grid.add(roleCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                selected.setFullName(fullName.getText());
                selected.setEmail(email.getText());
                selected.setRole(roleCombo.getValue());
                return selected;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            updateUser(user);
        });
    }

    private void updateUser(UserData user) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "UPDATE users SET full_name = ?, email = ?, role = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getUserId());

            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "User updated successfully!");
            loadUsers();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update user: " + e.getMessage());
        }
    }

    @FXML
    private void activateUser() {
        UserData selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a user to activate.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "UPDATE users SET is_active = 1 WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selected.getUserId());
            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "User activated successfully!");
            loadUsers();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to activate user: " + e.getMessage());
        }
    }

    @FXML
    private void deactivateUser() {
        UserData selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a user to deactivate.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "UPDATE users SET is_active = 0 WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selected.getUserId());
            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "User deactivated successfully!");
            loadUsers();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to deactivate user: " + e.getMessage());
        }
    }

    @FXML
    private void deleteUser() {
        UserData selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a user to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Confirm Delete");
        alert.setContentText("Are you sure you want to delete " + selected.getFullName() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = DatabaseConnection.getInstance().getConnection();
                String query = "DELETE FROM users WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, selected.getUserId());
                stmt.executeUpdate();
                stmt.close();

                showAlert("Success", "User deleted successfully!");
                loadUsers();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete user: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class UserData {
        private int userId;
        private String uniqueId;
        private String fullName;
        private String email;
        private String role;
        private boolean isActive;

        public UserData(int userId, String uniqueId, String fullName, String email, String role, boolean isActive) {
            this.userId = userId;
            this.uniqueId = uniqueId;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
            this.isActive = isActive;
        }

        public int getUserId() { return userId; }
        public String getUniqueId() { return uniqueId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getStatus() { return isActive ? "Active" : "Inactive"; }
        public boolean isActive() { return isActive; }

        public void setFullName(String fullName) { this.fullName = fullName; }
        public void setEmail(String email) { this.email = email; }
        public void setRole(String role) { this.role = role; }
    }
}