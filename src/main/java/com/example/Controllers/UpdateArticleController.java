package com.example.Controllers;


import Classes.Article;
import Classes.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class UpdateArticleController {

    @FXML
    private ComboBox<String> titleComboBox; // Dropdown for selecting article title
    @FXML
    private TextField titTXT; // Article title input
    @FXML
    private TextField titTXT2; // Source input
    @FXML
    private TextArea artTXT; // Content display area
    @FXML
    private TextField titTXT1; // Category display
    @FXML
    private Button chooseFileButton; // Button to choose the file
    @FXML
    private Button updateButton; // Button to update the article

    private final DatabaseHandler dbHandler = new DatabaseHandler();
    private File selectedFile;
    private int articleID; // The ID of the article being updated

    // Method to initialize the ComboBox and load the article titles
    public void initialize() {
        loadArticleTitles();

        // Set listener to load article data when an article title is selected
        titleComboBox.setOnAction(event -> loadArticleData());

        // Set action for the "Update Article" button
        updateButton.setOnAction(event -> updateArticle());
    }

    // Method to load article titles into the ComboBox
    private void loadArticleTitles() {
        try {
            // Fetch all articles
            List<Article> articles = dbHandler.fetchAllArticles();

            // Populate the ComboBox with article titles
            for (Article article : articles) {
                titleComboBox.getItems().add(article.getTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to load article data into fields when an article title is selected
    private void loadArticleData() {
        String selectedTitle = titleComboBox.getValue(); // Get the selected title from the ComboBox

        if (selectedTitle != null) {
            try {
                // Fetch article by title
                Article article = dbHandler.fetchArticleByTitle(selectedTitle);

                if (article != null) {
                    articleID = article.getArticleID();
                    titTXT.setText(article.getTitle());
                    titTXT2.setText(article.getSource());
                    artTXT.setText(article.getContent());
                    titTXT1.setText(article.getCategory());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // This method is used to choose a text file for the article content
    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                artTXT.setText(String.valueOf(content));
            } catch (IOException ex) {
                ex.printStackTrace();
                artTXT.setText("Error reading the file.");
            }
        }
    }

    @FXML
    private void updateArticle() {
        // Retrieve input values
        String title = titTXT.getText();
        String source = titTXT2.getText();
        String content = artTXT.getText();

        // Validate inputs
        if (title.isEmpty() || source.isEmpty() || content.isEmpty()) {
            artTXT.setText("Please fill in all required fields.");
            return;
        }

        // Identify the category using NLP based on the content
        String category = identifyCategory(content);
        titTXT1.setText(category); // Display the identified category

        // Create an updated Article object
        Article updatedArticle = new Article(
                articleID, // Use the same articleID for the update
                title,
                content,
                category,
                source,
                new java.util.Date() // Current date for the updated date
        );

        // Update the article in the database
        try {
            dbHandler.updateArticle(articleID, updatedArticle);
            artTXT.setText("Article updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            artTXT.setText("Error updating article.");
        }
    }

    /**
     * Identifies the category of the article content using NLP.
     *
     * @param content The article content to analyze.
     * @return The identified category.
     */
    private String identifyCategory(String content) {
        // Example of simple keyword-based NLP categorization
        if (content.toLowerCase().contains("technology")) {
            return "Technology";
        } else if (content.toLowerCase().contains("health")) {
            return "Health";
        } else if (content.toLowerCase().contains("sports")) {
            return "Sports";
        } else {
            return "General";
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
