// src/main/java/module-info.java
module com.university {
    // Required JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;

    // MySQL JDBC Driver - Correct module name
    requires java.sql;
    requires mysql.connector.j;  // Note: It's "mysql.connector.j" not "mysql.connector.java"

    // BCrypt for password hashing
    requires jbcrypt;

    // Open packages for JavaFX FXML
    opens com.university to javafx.fxml;
    opens com.university.controllers to javafx.fxml;
    opens com.university.models to javafx.base;
    opens com.university.utils to javafx.fxml;

    // Export packages
    exports com.university;
    exports com.university.controllers;
    exports com.university.models;
    exports com.university.utils;
}