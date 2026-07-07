// src/main/java/com/university/models/Admin.java
package com.university.models;

import java.time.LocalDate;

public class Admin extends User {
    private String adminCode;
    private String position;
    private String department;

    public Admin() {
        super();
    }

    public Admin(int userId, String uniqueId, String fullName, String email, String password,
                 String phone, String address, LocalDate dateOfBirth, String gender,
                 String adminCode, String position, String department) {
        super(userId, uniqueId, fullName, email, password, "admin", phone, address, dateOfBirth, gender);
        this.adminCode = adminCode;
        this.position = position;
        this.department = department;
    }

    // Getters and Setters
    public String getAdminCode() { return adminCode; }
    public void setAdminCode(String adminCode) { this.adminCode = adminCode; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}