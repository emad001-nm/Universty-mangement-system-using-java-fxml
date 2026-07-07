// src/main/java/com/university/controllers/Admin/DashboardViewController.java
package com.university.controllers.Admin;

import com.university.models.DatabaseConnection;
import com.university.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardViewController {
    @FXML private Label welcomeLabel;
    @FXML private Label totalStudents;
    @FXML private Label totalTeachers;
    @FXML private Label totalCourses;
    @FXML private Label totalDepartments;
    @FXML private Label pendingFees;
    @FXML private Label todayAttendance;
    @FXML private Label recentActivities;

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        if (session.isLoggedIn()) {
            welcomeLabel.setText("Welcome back, " + session.getFullName() + "!");
            loadStatistics();
            loadRecentActivities();
        }
    }

    private void loadStatistics() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            String[] queries = {
                    "SELECT COUNT(*) FROM students",
                    "SELECT COUNT(*) FROM teachers",
                    "SELECT COUNT(*) FROM courses",
                    "SELECT COUNT(*) FROM departments",
                    "SELECT COUNT(*) FROM fees WHERE status IN ('Pending', 'Partial', 'Overdue')",
                    "SELECT COUNT(*) FROM attendance WHERE date = CURDATE()"
            };

            PreparedStatement stmt = conn.prepareStatement(queries[0]);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) totalStudents.setText(String.valueOf(rs.getInt(1)));
            rs.close();
            stmt.close();

            stmt = conn.prepareStatement(queries[1]);
            rs = stmt.executeQuery();
            if (rs.next()) totalTeachers.setText(String.valueOf(rs.getInt(1)));
            rs.close();
            stmt.close();

            stmt = conn.prepareStatement(queries[2]);
            rs = stmt.executeQuery();
            if (rs.next()) totalCourses.setText(String.valueOf(rs.getInt(1)));
            rs.close();
            stmt.close();

            stmt = conn.prepareStatement(queries[3]);
            rs = stmt.executeQuery();
            if (rs.next()) totalDepartments.setText(String.valueOf(rs.getInt(1)));
            rs.close();
            stmt.close();

            stmt = conn.prepareStatement(queries[4]);
            rs = stmt.executeQuery();
            if (rs.next()) pendingFees.setText(String.valueOf(rs.getInt(1)));
            rs.close();
            stmt.close();

            stmt = conn.prepareStatement(queries[5]);
            rs = stmt.executeQuery();
            if (rs.next()) todayAttendance.setText(String.valueOf(rs.getInt(1)));
            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRecentActivities() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            StringBuilder activities = new StringBuilder();

            String enrollQuery = "SELECT '📚 New enrollment' as activity, enrollment_date as date " +
                    "FROM enrollments ORDER BY enrollment_date DESC LIMIT 1";
            PreparedStatement stmt1 = conn.prepareStatement(enrollQuery);
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next()) {
                activities.append("• ").append(rs1.getString("activity"));
                if (rs1.getDate("date") != null) {
                    activities.append(" (").append(rs1.getDate("date")).append(")");
                }
                activities.append("\n");
            }
            rs1.close();
            stmt1.close();

            String userQuery = "SELECT '👤 New user registered' as activity, created_at as date " +
                    "FROM users ORDER BY created_at DESC LIMIT 1";
            PreparedStatement stmt2 = conn.prepareStatement(userQuery);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                activities.append("• ").append(rs2.getString("activity"));
                if (rs2.getDate("date") != null) {
                    activities.append(" (").append(rs2.getDate("date")).append(")");
                }
                activities.append("\n");
            }
            rs2.close();
            stmt2.close();

            String feeQuery = "SELECT '💰 Fee payment received' as activity, payment_date as date " +
                    "FROM fees WHERE payment_date IS NOT NULL ORDER BY payment_date DESC LIMIT 1";
            PreparedStatement stmt3 = conn.prepareStatement(feeQuery);
            ResultSet rs3 = stmt3.executeQuery();
            if (rs3.next()) {
                activities.append("• ").append(rs3.getString("activity"));
                if (rs3.getDate("date") != null) {
                    activities.append(" (").append(rs3.getDate("date")).append(")");
                }
                activities.append("\n");
            }
            rs3.close();
            stmt3.close();

            if (activities.length() == 0) {
                recentActivities.setText("No recent activities");
            } else {
                recentActivities.setText(activities.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            recentActivities.setText("Failed to load activities");
        }
    }
}