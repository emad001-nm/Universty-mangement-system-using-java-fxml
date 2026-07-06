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

    public User() {
        this.isActive = true;
    }

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

    @Override
    public String toString() {
        return fullName + " (" + uniqueId + ")";
    }
}