// src/main/java/com/university/utils/SessionManager.java
package com.university.utils;

import com.university.models.User;

public class SessionManager {
    private static SessionManager instance = null;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }

    public boolean isStudent() {
        return currentUser != null && "student".equals(currentUser.getRole());
    }

    public boolean isTeacher() {
        return currentUser != null && "teacher".equals(currentUser.getRole());
    }

    public void logout() {
        currentUser = null;
    }

    public String getFullName() {
        return currentUser != null ? currentUser.getFullName() : "";
    }

    public String getRole() {
        return currentUser != null ? currentUser.getRole() : "";
    }

    public String getUniqueId() {
        return currentUser != null ? currentUser.getUniqueId() : "";
    }

    public String getEmail() {
        return currentUser != null ? currentUser.getEmail() : "";
    }

    public String getPhone() {
        return currentUser != null ? currentUser.getPhone() : "";
    }

    public String getAddress() {
        return currentUser != null ? currentUser.getAddress() : "";
    }

    public String getGender() {
        return currentUser != null ? currentUser.getGender() : "";
    }

    public int getUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    public int getStudentId() {
        return currentUser != null ? currentUser.getStudentId() : -1;
    }

    public int getTeacherId() {
        return currentUser != null ? currentUser.getTeacherId() : -1;
    }

    public int getAdminId() {
        return currentUser != null ? currentUser.getAdminId() : -1;
    }

    public String getRollNumber() {
        return currentUser != null ? currentUser.getRollNumber() : "";
    }

    public String getEmployeeId() {
        return currentUser != null ? currentUser.getEmployeeId() : "";
    }

    public String getAdminCode() {
        return currentUser != null ? currentUser.getAdminCode() : "";
    }
}