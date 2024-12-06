package com.example.Controllers;

import Classes.Article;
import Classes.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ArticleDetailViewController {

    @FXML
    private Label articleTitleLabel;

    @FXML
    private TextArea articleContentArea;

    @FXML
    private Label statusLabel; // New label for status messages

    private Article currentArticle;
    private String currentCategory;
    private DatabaseHandler dbHandler;

    @FXML
    private void initialize() {
        dbHandler = new DatabaseHandler();
    }

    public void setArticleDetail(Article article, String category) {
        this.currentArticle = article;
        this.currentCategory = category;

        // Set the article details in the UI
        articleTitleLabel.setText(article.getTitle());
        articleContentArea.setText(article.getContent());

        // Clear the status message whenever a new article is loaded
        statusLabel.setText("");
    }

    @FXML
    private void handleLikeButtonClick() {
        if (currentArticle != null) {
            dbHandler.recordUserLike(currentArticle.getArticleID());
            System.out.println("Article liked: " + currentArticle.getTitle());

            // Update the status label
            statusLabel.setText("Added to your Likes List");
        }
    }

    @FXML
    private void handleSkipButtonClick() {
        Article nextArticle = dbHandler.fetchNextArticle(currentCategory, currentArticle.getArticleID());
        if (nextArticle != null) {
            setArticleDetail(nextArticle, currentCategory);
        } else {
            articleContentArea.setText("No more articles in this category.");
        }
    }
}
