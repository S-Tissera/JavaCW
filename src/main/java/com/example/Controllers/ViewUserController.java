package com.example.Controllers;

import Classes.DatabaseHandler;
import Classes.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewUserController {

    @FXML
    private TableView<User> userTable; // Table to display users
    @FXML
    private TableColumn<User, String> userIDColumn; // Column for userID
    @FXML
    private TableColumn<User, String> usernameColumn; // Column for username
    @FXML
    private TableColumn<User, String> passwordColumn; // Column for password

    private final DatabaseHandler dbHandler = new DatabaseHandler();

    // Method to initialize the view and populate the user table
    public void initialize() {
        // Load users into the table
        loadUserTable();
    }

    // Method to load all users into the TableView
    private void loadUserTable() {
        try {
            ObservableList<User> users = dbHandler.fetchAllUsers();
            userTable.setItems(users);

            // Set up the columns to display user information
            userIDColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getUserID()));
            usernameColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getUsername()));
            passwordColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading users", e.getMessage());
        }
    }

    // Method to show error message
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void toAdminOptions(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("adminOptions.fxml"));
        stage.setScene(new Scene(root,600, 400));
        stage.show();


        Stage previousStage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        previousStage.close();
    }
}
