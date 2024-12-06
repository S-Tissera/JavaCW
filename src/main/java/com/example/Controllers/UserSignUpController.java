package com.example.Controllers;

import Classes.RegularUser; // Import RegularUser class
import Classes.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class UserSignUpController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button signUpButton;
    @FXML
    private Label messageLabel;

    private final DatabaseHandler dbHandler;

    public UserSignUpController() {
        dbHandler = new DatabaseHandler();
    }

    @FXML
    private void initialize() {
        signUpButton.setOnAction(e -> handleSignUp());
    }

    private void handleSignUp() {
        // Retrieve user input
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        // Create and populate RegularUser object (since User is abstract)
        RegularUser newUser = new RegularUser(null, username, password); // RegularUser constructor

        // Save the new user in the database
        boolean userAdded = dbHandler.addUser(newUser); // Pass RegularUser to addUser method
        if (userAdded) {
            messageLabel.setText("Sign-up successful! You can now log in.");
            clearFields();
        } else {
            messageLabel.setText("Failed to sign up. Username might already exist.");
        }
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    protected void toLogin(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("loginController.fxml"));
        stage.setScene(new Scene(root, 400, 300));
        stage.show();

        Stage previousStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        previousStage.close();
    }
}
