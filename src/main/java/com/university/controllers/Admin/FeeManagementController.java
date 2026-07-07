// src/main/java/com/university/controllers/FeeManagementController.java
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

public class FeeManagementController {
    @FXML private TableView<FeeData> feeTable;
    @FXML private TableColumn<FeeData, Integer> feeIdCol;
    @FXML private TableColumn<FeeData, String> feeStudentCol;
    @FXML private TableColumn<FeeData, Integer> feeSemesterCol;
    @FXML private TableColumn<FeeData, Double> feeTotalCol;
    @FXML private TableColumn<FeeData, Double> feePaidCol;
    @FXML private TableColumn<FeeData, Double> feeDueCol;
    @FXML private TableColumn<FeeData, String> feeStatusCol;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatusCombo;
    @FXML private Label recordCount;

    private ObservableList<FeeData> feeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        feeIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        feeStudentCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        feeSemesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));
        feeTotalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        feePaidCol.setCellValueFactory(new PropertyValueFactory<>("paid"));
        feeDueCol.setCellValueFactory(new PropertyValueFactory<>("due"));
        feeStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        filterStatusCombo.getItems().addAll("All Status", "Pending", "Partial", "Paid", "Overdue");
        filterStatusCombo.setValue("All Status");

        searchField.textProperty().addListener((obs, old, newVal) -> filterFees());
        filterStatusCombo.valueProperty().addListener((obs, old, newVal) -> filterFees());

        loadFees();
    }

    @FXML
    private void loadFees() {
        feeList.clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT f.*, u.full_name as student_name " +
                    "FROM fees f " +
                    "JOIN students s ON f.student_id = s.id " +
                    "JOIN users u ON s.user_id = u.id " +
                    "ORDER BY f.due_date DESC";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                FeeData fee = new FeeData(
                        rs.getInt("id"),
                        rs.getString("student_name"),
                        rs.getInt("semester"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("paid_amount"),
                        rs.getDouble("total_amount") - rs.getDouble("paid_amount"),
                        rs.getString("status")
                );
                feeList.add(fee);
            }

            rs.close();
            stmt.close();
            feeTable.setItems(feeList);
            updateRecordCount();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load fees: " + e.getMessage());
        }
    }

    @FXML
    private void filterFees() {
        String search = searchField.getText().toLowerCase();
        String statusFilter = filterStatusCombo.getValue();

        ObservableList<FeeData> filtered = FXCollections.observableArrayList();

        for (FeeData fee : feeList) {
            boolean matchesSearch = search.isEmpty() ||
                    fee.getStudentName().toLowerCase().contains(search);

            boolean matchesStatus = statusFilter.equals("All Status") ||
                    fee.getStatus().equals(statusFilter);

            if (matchesSearch && matchesStatus) {
                filtered.add(fee);
            }
        }

        feeTable.setItems(filtered);
        updateRecordCount();
    }

    private void updateRecordCount() {
        int count = feeTable.getItems().size();
        recordCount.setText("Showing " + count + " records");
    }

    @FXML
    private void refreshFees() {
        loadFees();
        showAlert("Info", "Fees refreshed!");
    }

    @FXML
    private void addFee() {
        Dialog<FeeData> dialog = new Dialog<>();
        dialog.setTitle("Add Fee");
        dialog.setHeaderText("Create a new fee record");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<String> studentCombo = new ComboBox<>();
        TextField semester = new TextField();
        semester.setPromptText("Semester");
        TextField amount = new TextField();
        amount.setPromptText("Total Amount");

        // Load students
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT u.full_name, s.id FROM students s JOIN users u ON s.user_id = u.id WHERE u.is_active = 1"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                studentCombo.getItems().add(rs.getString("full_name") + " (ID: " + rs.getInt("id") + ")");
            }
            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        grid.add(new Label("Student:"), 0, 0);
        grid.add(studentCombo, 1, 0);
        grid.add(new Label("Semester:"), 0, 1);
        grid.add(semester, 1, 1);
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amount, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                String student = studentCombo.getValue();
                if (student != null && !semester.getText().isEmpty() && !amount.getText().isEmpty()) {
                    return new FeeData(0, student, Integer.parseInt(semester.getText()),
                            Double.parseDouble(amount.getText()), 0.0,
                            Double.parseDouble(amount.getText()), "Pending");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(fee -> {
            saveFee(fee);
        });
    }

    private void saveFee(FeeData fee) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            String student = fee.getStudentName();
            int studentId = Integer.parseInt(student.substring(student.lastIndexOf("ID: ") + 4, student.length() - 1));

            String query = "INSERT INTO fees (student_id, semester, total_amount, status) VALUES (?, ?, ?, 'Pending')";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            stmt.setInt(2, fee.getSemester());
            stmt.setDouble(3, fee.getTotal());

            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "Fee record added successfully!");
            loadFees();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add fee: " + e.getMessage());
        }
    }

    @FXML
    private void markFeePaid() {
        FeeData selected = feeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a fee record to mark as paid.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "UPDATE fees SET status = 'Paid', paid_amount = total_amount, payment_date = CURDATE() " +
                    "WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selected.getId());
            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "Fee marked as paid successfully!");
            loadFees();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to mark fee as paid: " + e.getMessage());
        }
    }

    @FXML
    private void recordPayment() {
        FeeData selected = feeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a fee record to record payment.");
            return;
        }

        if ("Paid".equals(selected.getStatus())) {
            showAlert("Info", "This fee is already fully paid.");
            return;
        }

        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Record Payment");
        dialog.setHeaderText("Record payment for: " + selected.getStudentName());

        ButtonType saveButton = new ButtonType("Record", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label currentLabel = new Label("Current Status: " + selected.getStatus());
        Label dueLabel = new Label("Due Amount: $" + String.format("%.2f", selected.getDue()));
        TextField amountField = new TextField();
        amountField.setPromptText("Payment Amount");

        grid.add(currentLabel, 0, 0);
        grid.add(dueLabel, 0, 1);
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                try {
                    return Double.parseDouble(amountField.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(amount -> {
            recordPaymentAmount(selected.getId(), amount, selected);
        });
    }

    private void recordPaymentAmount(int feeId, double amount, FeeData fee) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            double newPaid = fee.getPaid() + amount;
            String newStatus = newPaid >= fee.getTotal() ? "Paid" : "Partial";

            String query = "UPDATE fees SET paid_amount = ?, status = ?, payment_date = CURDATE() WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1, newPaid);
            stmt.setString(2, newStatus);
            stmt.setInt(3, feeId);

            stmt.executeUpdate();
            stmt.close();

            showAlert("Success", "Payment recorded successfully!");
            loadFees();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to record payment: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class FeeData {
        private int id;
        private String studentName;
        private int semester;
        private double total;
        private double paid;
        private double due;
        private String status;

        public FeeData(int id, String studentName, int semester, double total, double paid, double due, String status) {
            this.id = id;
            this.studentName = studentName;
            this.semester = semester;
            this.total = total;
            this.paid = paid;
            this.due = due;
            this.status = status;
        }

        public int getId() { return id; }
        public String getStudentName() { return studentName; }
        public int getSemester() { return semester; }
        public double getTotal() { return total; }
        public double getPaid() { return paid; }
        public double getDue() { return due; }
        public String getStatus() { return status; }

        public void setPaid(double paid) { this.paid = paid; }
        public void setStatus(String status) { this.status = status; }
    }
}