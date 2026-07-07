// src/main/java/com/university/controllers/Teacher/DashboardViewController.java
package com.university.controllers.Teacher;

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
    @FXML private Label totalCourses;
    @FXML private Label todayClasses;
    @FXML private Label department;
    @FXML private Label designation;

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        if (session.isLoggedIn()) {
            welcomeLabel.setText("Welcome back, " + session.getFullName() + "!");
            loadStatistics();
            loadTeacherInfo();
        }
    }

    private void loadStatistics() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            int teacherId = SessionManager.getInstance().getTeacherId();

            if (teacherId > 0) {
                String studentQuery = "SELECT COUNT(DISTINCT e.student_id) as total " +
                        "FROM enrollments e JOIN courses c ON e.course_id = c.id WHERE c.teacher_id = ?";
                PreparedStatement stmt1 = conn.prepareStatement(studentQuery);
                stmt1.setInt(1, teacherId);
                ResultSet rs1 = stmt1.executeQuery();
                if (rs1.next()) totalStudents.setText(String.valueOf(rs1.getInt("total")));
                rs1.close();
                stmt1.close();

                String courseQuery = "SELECT COUNT(*) as total FROM courses WHERE teacher_id = ?";
                PreparedStatement stmt2 = conn.prepareStatement(courseQuery);
                stmt2.setInt(1, teacherId);
                ResultSet rs2 = stmt2.executeQuery();
                if (rs2.next()) totalCourses.setText(String.valueOf(rs2.getInt("total")));
                rs2.close();
                stmt2.close();

                String classQuery = "SELECT COUNT(*) as total FROM class_schedule " +
                        "WHERE teacher_id = ? AND day_of_week = DAYNAME(CURDATE())";
                PreparedStatement stmt3 = conn.prepareStatement(classQuery);
                stmt3.setInt(1, teacherId);
                ResultSet rs3 = stmt3.executeQuery();
                if (rs3.next()) todayClasses.setText(String.valueOf(rs3.getInt("total")));
                rs3.close();
                stmt3.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTeacherInfo() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            int userId = SessionManager.getInstance().getUserId();

            String query = "SELECT t.*, d.dept_name FROM teachers t " +
                    "LEFT JOIN departments d ON t.department_id = d.id WHERE t.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                department.setText(rs.getString("dept_name") != null ? rs.getString("dept_name") : "N/A");
                designation.setText(rs.getString("qualification") != null ? rs.getString("qualification") : "N/A");
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}