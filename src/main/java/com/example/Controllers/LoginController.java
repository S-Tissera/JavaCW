package com.example.Controllers;

import Classes.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField; // Field to show plain text password

    @FXML
    private CheckBox showPasswordCheckbox;

    @FXML
    private Label statusLabel;

    private DatabaseHandler dbHandler; // Assuming the DatabaseHandler instance

    @FXML
    private void initialize() {
        // Bind the visible password field with the password field
        visiblePasswordField.managedProperty().bind(showPasswordCheckbox.selectedProperty());
        visiblePasswordField.visibleProperty().bind(showPasswordCheckbox.selectedProperty());
        passwordField.managedProperty().bind(showPasswordCheckbox.selectedProperty().not());
        passwordField.visibleProperty().bind(showPasswordCheckbox.selectedProperty().not());

        // Sync the text between the two fields
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        dbHandler = new DatabaseHandler(); // Instantiate DatabaseHandler
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            // Use the fetchUserByUsername method from the DatabaseHandler to get the User object
            User user = dbHandler.fetchUserByUsername(username);

            // If the user is found and the password matches
            if (user != null && user.getPassword().equals(password)) {
                // Set user details in the UserSession
                UserSession.getInstance().setUser(user);

                System.out.println("User logged in: ID = " + user.getUserID() + ", Username = " + user.getUsername() + ", Role = " + (user.isAdmin() ? "Admin" : "User"));

                // Get the current stage (login window)
                Stage loginStage = (Stage) usernameField.getScene().getWindow();

                // Navigate to the appropriate window based on admin or regular user role
                if (user.isAdmin()) {
                    ((AdminUser) user).accessAdminDashboard(loginStage); // Pass the loginStage to close it
                } else {
                    ((RegularUser) user).accessUserDashboard(loginStage); // Access user-specific dashboard
                }
            } else {
                statusLabel.setText("Invalid credentials. Please try again.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error connecting to the database.");
            e.printStackTrace();
        }
    }



    @FXML
    protected void toUserSignup(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("userSignUp.fxml")); // Ensure the fxml path is correct
        stage.setScene(new Scene(root, 190, 272));
        stage.show();

        Stage previousStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        previousStage.close();
    }
}
