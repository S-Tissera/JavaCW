package com.example.Controllers;

import Classes.Article;
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
import java.util.List;

public class DeleteArticleController {

    @FXML
    private ComboBox<String> titleComboBox; // Dropdown for selecting article title
    @FXML
    private Button deleteButton; // Button to delete the article
    @FXML
    private Label statusLabel; // Label to show status messages
    @FXML
    private TextArea articleDetailsArea; // TextArea to display article details

    private final DatabaseHandler dbHandler = new DatabaseHandler();
    private List<Article> articles; // Cache of articles to map titles to IDs

    // Method to initialize the ComboBox and load the article titles
    public void initialize() {
        loadArticleTitles();

        // Add a listener to show article details when a title is selected
        titleComboBox.setOnAction(event -> displayArticleDetails());

        // Set action for the "Delete Article" button
        deleteButton.setOnAction(event -> deleteArticle());
    }

    // Method to load article titles into the ComboBox
    private void loadArticleTitles() {
        try {
            // Fetch all articles
            articles = dbHandler.fetchAllArticles();

            // Populate the ComboBox with article titles
            for (Article article : articles) {
                titleComboBox.getItems().add(article.getTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load article titles.");
        }
    }

    // Method to display details of the selected article
    private void displayArticleDetails() {
        String selectedTitle = titleComboBox.getValue();

        if (selectedTitle == null || selectedTitle.isEmpty()) {
            articleDetailsArea.setText("Please select an article to view details.");
            return;
        }

        // Find the selected article and display its details
        for (Article article : articles) {
            if (article.getTitle().equals(selectedTitle)) {
                articleDetailsArea.setText(
                        "Title: " + article.getTitle() + "\n" +
                                "Category: " + article.getCategory() + "\n" +
                                "Source: " + article.getSource() + "\n" +
                                "Content:\n" + article.getContent()
                );
                return;
            }
        }

        articleDetailsArea.setText("Selected article not found.");
    }

    // Method to delete the selected article
    private void deleteArticle() {
        String selectedTitle = titleComboBox.getValue();

        if (selectedTitle == null || selectedTitle.isEmpty()) {
            statusLabel.setText("Please select an article to delete.");
            return;
        }

        // Find the article ID for the selected title
        int articleID = -1;
        for (Article article : articles) {
            if (article.getTitle().equals(selectedTitle)) {
                articleID = article.getArticleID();
                break;
            }
        }

        if (articleID == -1) {
            statusLabel.setText("Selected article not found.");
            return;
        }

        // Call the deleteArticle method to remove the article
        try {
            dbHandler.deleteArticle(articleID);
            statusLabel.setText("Article deleted successfully.");

            // Remove the deleted title from the ComboBox
            titleComboBox.getItems().remove(selectedTitle);
            articleDetailsArea.clear(); // Clear the displayed article details
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to delete article.");
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
