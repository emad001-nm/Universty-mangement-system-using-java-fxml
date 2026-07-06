// src/main/java/com/university/utils/SessionManager.java
package com.university.utils;

import com.university.models.User;
import com.university.controllers.LoginController.ExtendedUser;

public class SessionManager {
    private static SessionManager instance = null;
    private ExtendedUser currentUser;

    private SessionManager() {
        // Private constructor for Singleton
    }

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

    public void setCurrentUser(ExtendedUser user) {
        this.currentUser = user;
    }

    public ExtendedUser getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
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

    public String getUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public int getUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    // Convenience methods for role-specific data
    public Integer getStudentId() {
        return currentUser != null ? currentUser.getStudentId() : null;
    }

    public String getRollNumber() {
        return currentUser != null ? currentUser.getRollNumber() : null;
    }

    public Integer getTeacherId() {
        return currentUser != null ? currentUser.getTeacherId() : null;
    }

    public String getEmployeeId() {
        return currentUser != null ? currentUser.getEmployeeId() : null;
    }
}