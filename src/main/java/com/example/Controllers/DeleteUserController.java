package com.example.Controllers;

import Classes.DatabaseHandler;
import Classes.User;
import Classes.AdminUser;
import Classes.RegularUser;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class DeleteUserController {

    @FXML
    private ComboBox<String> userComboBox;

    @FXML
    private TextArea userDetailsArea;

    @FXML
    private Button deleteButton;

    @FXML
    private Label statusLabel;

    private final DatabaseHandler dbHandler = new DatabaseHandler();
    private ObservableList<User> users; // Cache of users for detailed display

    @FXML
    public void initialize() {
        loadUsers();

        // Show user details when a user is selected
        userComboBox.setOnAction(event -> displayUserDetails());

        // Handle delete button click
        deleteButton.setOnAction(event -> handleDelete());
    }

    private void loadUsers() {
        try {
            // Fetch all users from the database
            users = dbHandler.fetchAllUsers();
            for (User user : users) {
                userComboBox.getItems().add(user.getUsername());
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading users: " + e.getMessage());
        }
    }

    private void displayUserDetails() {
        String selectedUser = userComboBox.getValue();
        if (selectedUser == null || selectedUser.isEmpty()) {
            userDetailsArea.setText("Please select a user to view details.");
            return;
        }

        for (User user : users) {
            if (user.getUsername().equals(selectedUser)) {
                String userDetails = "User ID: " + user.getUserID() + "\n" +
                        "Username: " + user.getUsername() + "\n";

                // Check if user is an admin or regular user and display accordingly
                if (user instanceof AdminUser) {
                    userDetails += "Role: Admin";
                } else if (user instanceof RegularUser) {
                    userDetails += "Role: Regular User";
                }

                userDetailsArea.setText(userDetails);
                return;
            }
        }

        userDetailsArea.setText("Selected user not found.");
    }

    private void handleDelete() {
        String selectedUser = userComboBox.getValue();
        if (selectedUser == null || selectedUser.isEmpty()) {
            statusLabel.setText("Please select a user to delete.");
            return;
        }

        // Show confirmation alert
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this user?");
        alert.setContentText("User: " + selectedUser);

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // Perform user deletion
                boolean isDeleted = dbHandler.deleteUser(selectedUser);
                if (isDeleted) {
                    statusLabel.setText("User deleted successfully.");
                    // Remove user from ComboBox and list of users
                    userComboBox.getItems().remove(selectedUser);
                    users.removeIf(user -> user.getUsername().equals(selectedUser));
                    userDetailsArea.clear();
                } else {
                    statusLabel.setText("Error deleting user. Please try again.");
                }
            } catch (Exception e) {
                statusLabel.setText("Error deleting user: " + e.getMessage());
            }
        }
    }

    @FXML
    protected void toAdminOptions(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("adminOptions.fxml"));
        stage.setScene(new Scene(root, 600, 400));
        stage.show();

        Stage previousStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        previousStage.close();
    }
}
