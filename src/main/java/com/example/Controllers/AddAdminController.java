package com.example.Controllers;

import Classes.AdminUser; // Import AdminUser class
import Classes.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AddAdminController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button addAdminButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label statusLabel;

    private final DatabaseHandler dbHandler = new DatabaseHandler();

    @FXML
    private void initialize() {
        // Set action for the "Add Admin" button
        addAdminButton.setOnAction(e -> handleAddAdmin());

        // Set action for the "Cancel" button to navigate back to adminOptions.fxml
        cancelButton.setOnAction(this::toAdminOptions);
    }

    private void handleAddAdmin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        // Create an AdminUser, passing null for userID, 1 for isAdmin
        AdminUser newAdmin = new AdminUser(null, username, password, 1);

        boolean isAdded = dbHandler.addAdmin(newAdmin);  // Pass AdminUser to the addAdmin method
        if (isAdded) {
            statusLabel.setText("Admin added successfully!");
        } else {
            statusLabel.setText("Failed to add admin. Try again.");
        }
    }

    // This method is used to navigate back to the Admin Options screen

    @FXML private void toAdminOptions(ActionEvent e) {
        try {
            // Load the adminOptions.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("adminOptions.fxml"));
            Parent root = loader.load();

            // Create a new stage and set the scene with the loaded adminOptions.fxml
            Stage stage = new Stage();
            stage.setTitle("Admin Options");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();

            // Close the current window (the AddAdmin screen)
            Stage currentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            statusLabel.setText("Error navigating back to admin options.");
        }
    }
}
