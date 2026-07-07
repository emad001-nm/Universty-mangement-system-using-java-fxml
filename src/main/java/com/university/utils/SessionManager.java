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
}