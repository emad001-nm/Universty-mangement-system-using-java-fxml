// src/main/java/com/university/models/User.java
package com.university.models;

import java.time.LocalDate;

public class User {
    private int id;
    private String uniqueId;
    private String fullName;
    private String email;
    private String password;
    private String role;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private String gender;
    private boolean isActive;

    // Role-specific fields
    private int studentId;
    private String rollNumber;
    private int teacherId;
    private String employeeId;
    private int adminId;
    private String adminIdCode;

    // Default constructor
    public User() {
        this.isActive = true;
        this.studentId = 0;
        this.teacherId = 0;
        this.adminId = 0;
    }

    // Full constructor
    public User(int id, String uniqueId, String fullName, String email, String password,
                String role, String phone, String address, LocalDate dateOfBirth, String gender) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.isActive = true;
        this.studentId = 0;
        this.teacherId = 0;
        this.adminId = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUniqueId() { return uniqueId; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Role-specific getters and setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    public String getAdminIdCode() { return adminIdCode; }
    public void setAdminIdCode(String adminIdCode) { this.adminIdCode = adminIdCode; }

    @Override
    public String toString() {
        return fullName + " (" + uniqueId + ")";
    }
}