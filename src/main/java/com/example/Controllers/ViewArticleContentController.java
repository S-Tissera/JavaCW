package com.example.Controllers;

import Classes.Article;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewArticleContentController {

    @FXML
    private TextField titleField; // TextField to display the article title
    @FXML
    private TextField sourceField; // TextField to display the source
    @FXML
    private TextArea contentArea; // TextArea to display the article content

    // Method to set the article details in the UI
    public void setArticle(Article article) {
        if (article != null) {
            titleField.setText(article.getTitle());
            sourceField.setText(article.getSource());
            contentArea.setText(article.getContent());
        }
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
