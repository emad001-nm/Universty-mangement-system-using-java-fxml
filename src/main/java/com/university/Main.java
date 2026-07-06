// src/main/java/com/university/Main.java
package com.university;

import com.university.models.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database connection
            DatabaseConnection.getInstance();

            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 450, 500);

            primaryStage.setTitle("University Management System - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            System.out.println("✓ Application started successfully!");

        } catch (Exception e) {
            System.err.println("✗ Failed to start application!");
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // Close database connection
        DatabaseConnection.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}