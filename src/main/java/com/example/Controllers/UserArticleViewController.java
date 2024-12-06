package com.example.Controllers;

import Classes.Article;
import Classes.DatabaseHandler;
import Classes.RecommendationEngine;
import Classes.UserReads;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserArticleViewController {

    @FXML
    private TabPane categoryTabPane;

    @FXML
    private ListView<String> technologyListView, healthListView, sportListView, generalListView, recommendationListView;

    @FXML
    private Button readButton, recommendationButton;

    private DatabaseHandler dbHandler;
    private RecommendationEngine recommendationEngine;
    private List<Article> technologyArticles, healthArticles, sportArticles, generalArticles, recommendedArticles;

    @FXML
    private void initialize() {
        dbHandler = new DatabaseHandler();
        recommendationEngine = new RecommendationEngine();

        // Load articles by category
        technologyArticles = dbHandler.fetchArticlesByCategory("Technology");
        healthArticles = dbHandler.fetchArticlesByCategory("Health");
        sportArticles = dbHandler.fetchArticlesByCategory("Sport");
        generalArticles = dbHandler.fetchArticlesByCategory("General");

        // Populate the ListViews with article titles
        populateListView(technologyListView, technologyArticles);
        populateListView(healthListView, healthArticles);
        populateListView(sportListView, sportArticles);
        populateListView(generalListView, generalArticles);

        // Set action for buttons
        readButton.setOnAction(event -> openArticle());
        recommendationButton.setOnAction(event -> displayRecommendations());
    }

    private void populateListView(ListView<String> listView, List<Article> articles) {
        listView.setItems(FXCollections.observableArrayList(
                articles.stream().map(Article::getTitle).toList()
        ));
    }

    private void openArticle() {
        // Identify the selected tab and list view
        Tab selectedTab = categoryTabPane.getSelectionModel().getSelectedItem();
        ListView<String> selectedListView = switch (selectedTab.getText()) {
            case "Technology" -> technologyListView;
            case "Health" -> healthListView;
            case "Sport" -> sportListView;
            case "General" -> generalListView;
            case "Recommendations" -> recommendationListView;
            default -> null;
        };

        if (selectedListView == null) return;

        // Get the selected article title
        String selectedTitle = selectedListView.getSelectionModel().getSelectedItem();
        if (selectedTitle == null) return;

        // Find the article based on the selected title
        Article selectedArticle = switch (selectedTab.getText()) {
            case "Technology" -> findArticleByTitle(technologyArticles, selectedTitle);
            case "Health" -> findArticleByTitle(healthArticles, selectedTitle);
            case "Sport" -> findArticleByTitle(sportArticles, selectedTitle);
            case "General" -> findArticleByTitle(generalArticles, selectedTitle);
            case "Recommendations" -> findArticleByTitle(recommendedArticles, selectedTitle);
            default -> null;
        };

        if (selectedArticle != null) {
            logArticleRead(selectedArticle);
            showArticleDetail(selectedArticle, selectedTab.getText());
        }
    }

    private Article findArticleByTitle(List<Article> articles, String title) {
        return articles.stream()
                .filter(article -> article.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }

    private void logArticleRead(Article article) {
        try {
            // Get current user ID from the session
            int userID = dbHandler.getCurrentUserID();

            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Convert formatted string to java.sql.Date (extract only the date part)
            java.sql.Date sqlDate = java.sql.Date.valueOf(formattedDate.split(" ")[0]);

            // Create a UserReads object
            UserReads userRead = new UserReads(userID, article.getArticleID(), sqlDate);
            System.out.println(userRead);

            // Call the method to insert the read activity
            boolean success = dbHandler.addUserReadActivity(userRead);

            // Log success or failure
            if (success) {
                System.out.println("Article read activity logged successfully.");
            } else {
                System.err.println("Failed to log article read activity.");
            }

        } catch (Exception e) {
            System.err.println("Error logging article read activity: " + e.getMessage());
        }
    }




    private void showArticleDetail(Article article, String category) {
        try {
            // Load the FXML for the article detail view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("articleDetailView.fxml"));
            Parent root = loader.load();

            // Get the controller of the article detail view
            ArticleDetailViewController controller = loader.getController();

            // Pass the article details to the controller
            controller.setArticleDetail(article, category);

            // Create a new stage for the article detail view
            Stage stage = new Stage();
            stage.setTitle("Article Detail - " + article.getTitle());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading ArticleDetailView.fxml: " + e.getMessage());
        }
    }

    private void displayRecommendations() {
        try {
            int userID = dbHandler.getCurrentUserID(); // Assumes a method to get the logged-in user ID
            recommendedArticles = recommendationEngine.recommendArticles(userID);

            if (recommendedArticles.isEmpty()) {
                System.out.println("No similar articles found.");
                recommendationListView.setItems(FXCollections.observableArrayList("No similar articles found."));
            } else {
                // Populate the recommendation ListView
                populateListView(recommendationListView, recommendedArticles);
            }

            // Select the "Recommendations" tab
            categoryTabPane.getSelectionModel().select(categoryTabPane.getTabs().stream()
                    .filter(tab -> tab.getText().equals("Recommendations"))
                    .findFirst()
                    .orElse(null));
        } catch (Exception e) {
            System.err.println("Error fetching recommendations: " + e.getMessage());
        }
    }

}
