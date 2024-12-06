package com.example.Controllers;

import Classes.Article;
import Classes.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AdminViewArticleController {

    @FXML
    private ListView<String> articlesListView; // ListView to display article titles
    @FXML
    private Label statusLabel; // Label for status messages

    private final DatabaseHandler dbHandler = new DatabaseHandler();
    private List<Article> articles; // Cache of articles for mapping titles to content

    @FXML
    public void initialize() {
        loadArticleTitles();

        // Set an action when a title is clicked
        articlesListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click to view the article content
                String selectedTitle = articlesListView.getSelectionModel().getSelectedItem();
                if (selectedTitle != null) {
                    openArticleContent(selectedTitle);
                }
            }
        });
    }

    // Load article titles into the ListView
    private void loadArticleTitles() {
        try {
            // Fetch all articles
            articles = dbHandler.fetchAllArticles();

            // Extract titles and set them in the ListView
            ObservableList<String> titles = FXCollections.observableArrayList();
            for (Article article : articles) {
                titles.add(article.getTitle());
            }
            articlesListView.setItems(titles);

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load articles.");
        }
    }

    // Open the article content page
    private void openArticleContent(String selectedTitle) {
        if (selectedTitle != null) {
            // Find the article by title from the cached list
            Article selectedArticle = articles.stream()
                    .filter(article -> article.getTitle().equals(selectedTitle))
                    .findFirst()
                    .orElse(null);

            if (selectedArticle != null) {
                showArticleDetail(selectedArticle); // Call showArticleDetail
            } else {
                statusLabel.setText("Article not found.");
            }
        } else {
            statusLabel.setText("No article selected.");
        }
    }

    // Display the article details in a new window
    private void showArticleDetail(Article selectedArticle) {
        try {
            // Load the content view from FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewArticleContent.fxml"));
            Parent root = loader.load();

            // Pass the selected article to the content controller
            ViewArticleContentController contentController = loader.getController();
            contentController.setArticle(selectedArticle);

            // Open the article detail view in a new window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Article Details - " + selectedArticle.getTitle());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load article details.");
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
