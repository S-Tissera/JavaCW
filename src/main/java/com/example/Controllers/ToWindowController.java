package com.example.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ToWindowController {

    @FXML
    protected void toAddArticle(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("addArticle.fxml"));
        stage.setScene(new Scene(root,627, 400));
        stage.show();


        Stage previousStage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        previousStage.close();
    }

    @FXML
    protected void toUpdateArticle(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("updateArticle.fxml"));
        stage.setScene(new Scene(root,627, 400));
        stage.show();


        Stage previousStage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        previousStage.close();
    }

    @FXML
    protected void toDeleteArticle(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("deleteArticle.fxml"));
        stage.setScene(new Scene(root,440, 414));
        stage.show();


        Stage previousStage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        previousStage.close();
    }

    @FXML
    protected void toViewArticle(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("viewArticles.fxml"));
        stage.setScene(new Scene(root,692, 610));
        stage.show();


        Stage previousStage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        previousStage.close();
    }

    @FXML
    protected void toViewUser(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("viewUser.fxml"));
        stage.setScene(new Scene(root,613, 456));
        stage.show();


        Stage previousStage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        previousStage.close();
    }

    @FXML
    protected void toAddAdmin(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("addAdmin.fxml"));
        stage.setScene(new Scene(root,302, 229));
        stage.show();


        Stage previousStage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        previousStage.close();
    }

    @FXML
    protected void toDeleteUser(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("deleteUser.fxml"));
        stage.setScene(new Scene(root,400, 330));
        stage.show();


        Stage previousStage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        previousStage.close();
    }

    @FXML
    protected void toAdminOptions(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("adminOptions.fxml"));
        stage.setScene(new Scene(root,692, 610));
        stage.show();


        Stage previousStage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        previousStage.close();
    }
}
