package com.example.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HelloController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cm2601";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "SELECT isAdmin FROM Users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                boolean isAdmin = resultSet.getBoolean("isAdmin");
                if (isAdmin) {
                    statusLabel.setText("Welcome, Admin!");
                } else {
                    statusLabel.setText("Welcome, User!");
                }
            } else {
                statusLabel.setText("Invalid credentials. Please try again.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error connecting to the database.");
            e.printStackTrace();
        }
    }
}

