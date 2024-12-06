package Classes;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private HikariDataSource dataSource;

    // Database connection URL and credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cm2601?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root"; // MySQL username
    private static final String DB_PASSWORD = ""; // MySQL password

    // Constructor that initializes the connection pool (HikariCP)
    public DatabaseHandler() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMaximumPoolSize(10);  // Set the maximum number of connections in the pool
        config.setConnectionTimeout(30000);  // Set connection timeou
        config.setIdleTimeout(600000);  // Set idle timeout
        config.setMaxLifetime(1800000);  // Set maximum lifetime for a connection

        dataSource = new HikariDataSource(config);
    }

    // Establishes the connection and returns it using the connection pool
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Add a new admin user (Admin User)
    public boolean addAdmin(AdminUser user) {
        String query = "INSERT INTO Users (username, password, isAdmin) VALUES (?, ?, 1)"; // Admin user
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding admin: " + e.getMessage());
            return false;
        }
    }

    // Add a new user (Normal User)
    public boolean addUser(RegularUser user) {
        String query = "INSERT INTO Users (username, password, isAdmin) VALUES (?, ?, 0)"; // Normal user
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }


    public ObservableList<User> fetchAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String query = "SELECT userID, username, password, isAdmin FROM users";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                String userID = resultSet.getString("userID");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                int isAdmin = resultSet.getInt("isAdmin");

                User user;
                if (isAdmin == 1) {
                    // Pass the isAdmin value to the constructor of AdminUser
                    user = new AdminUser(userID, username, password, isAdmin); // AdminUser with isAdmin = 1
                } else {
                    user = new RegularUser(userID, username, password); // RegularUser doesn't need isAdmin
                }
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }

        return users;
    }



    // Fetch a specific user by username, returning either AdminUser or RegularUser
    public User fetchUserByUsername(String username) {
        String query = "SELECT userID, username, password, isAdmin FROM users WHERE username = ?";
        User user = null;

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String userID = rs.getString("userID");
                String password = rs.getString("password");
                int isAdmin = rs.getInt("isAdmin");

                // When isAdmin = 1, create an AdminUser
                if (isAdmin == 1) {
                    user = new AdminUser(userID, username, password, isAdmin); // Pass isAdmin as the fourth parameter
                } else {
                    user = new RegularUser(userID, username, password); // RegularUser
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by username: " + e.getMessage());
        }

        return user; // Return the user object (AdminUser or RegularUser)
    }



    // Fetch all articles from the database
    public List<Article> fetchAllArticles() {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT * FROM Articles";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Article article = new Article(
                        rs.getInt("articleID"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("category"),
                        rs.getString("source"),
                        rs.getDate("addedDate")
                );
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch articles: " + e.getMessage());
        }
        return articles;
    }

    // Add an article to the database
    public void addArticle(Article article) {
        String query = "INSERT INTO Articles (title, content, category, source, addedDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, article.getTitle());
            stmt.setString(2, article.getContent());
            stmt.setString(3, article.getCategory());
            stmt.setString(4, article.getSource());
            stmt.setDate(5, new java.sql.Date(article.getAddedDate().getTime()));
            stmt.executeUpdate();
            System.out.println("Article added successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to add article: " + e.getMessage());
        }
    }

    // Fetch articles by category
    public List<Article> fetchArticlesByCategory(String category) {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT articleID, title, content, category, source, addedDate FROM articles WHERE category = ?";

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, category);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int articleID = rs.getInt("articleID");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String source = rs.getString("source");
                Date addedDate = rs.getDate("addedDate");

                // Create an Article object and add it to the list
                Article article = new Article(articleID, title, content, category, source, addedDate);
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching articles by category: " + e.getMessage());
        }

        return articles;
    }

    // Fetch the next article in the same category after the current article
    public Article fetchNextArticle(String currentCategory, int currentArticleID) {
        String query = "SELECT * FROM articles WHERE category = ? AND articleID > ? ORDER BY articleID ASC LIMIT 1";
        Article nextArticle = null;

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, currentCategory); // Set the category
            pstmt.setInt(2, currentArticleID); // Set the current article ID

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                nextArticle = new Article(
                        rs.getInt("articleID"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("category"),
                        rs.getString("source"),
                        rs.getDate("addedDate")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nextArticle;
    }

    public Article fetchArticleByTitle(String title) {
        String query = "SELECT * FROM Articles WHERE title = ?";
        Article article = null;

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, title); // Bind the title parameter to the query
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Create an Article object from the result set
                article = new Article(
                        rs.getInt("articleID"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("category"),
                        rs.getString("source"),
                        rs.getDate("addedDate") // Assuming addedDate is of type Date
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching article by title: " + e.getMessage());
        }

        return article; // Return the Article object, or null if not found
    }

    public void updateArticle(int articleID, Article updatedArticle) {
        String query = "UPDATE Articles SET title = ?, content = ?, category = ?, source = ?, addedDate = ? WHERE articleID = ?";

        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {

            // Set the parameters for the prepared statement
            stmt.setString(1, updatedArticle.getTitle());
            stmt.setString(2, updatedArticle.getContent());
            stmt.setString(3, updatedArticle.getCategory());
            stmt.setString(4, updatedArticle.getSource());
            stmt.setDate(5, new java.sql.Date(updatedArticle.getAddedDate().getTime()));  // Convert Java Date to SQL Date
            stmt.setInt(6, articleID);  // Set the articleID to identify which article to update

            // Execute the update query
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Article updated successfully!");
            } else {
                System.out.println("No article found with the given ID.");
            }

        } catch (SQLException e) {
            System.err.println("Failed to update article: " + e.getMessage());
        }
    }

    // Fetch all articles read by a user
    public List<Article> fetchArticlesReadByUser(int userID) {
        List<Article> readArticles = new ArrayList<>();
        String query = "SELECT a.articleID, a.title, a.content, a.category, a.source, a.addedDate " +
                "FROM user_reads ua " +
                "JOIN articles a ON ua.articleID = a.articleID " +
                "WHERE ua.userID = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userID); // Bind the userID parameter

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int articleID = resultSet.getInt("articleID");
                    String title = resultSet.getString("title");
                    String content = resultSet.getString("content");
                    String category = resultSet.getString("category");
                    String source = resultSet.getString("source");
                    Date addedDate = resultSet.getDate("addedDate");

                    // Create an Article object and add it to the list
                    Article article = new Article(articleID, title, content, category, source, addedDate);
                    readArticles.add(article);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return readArticles;
    }

    public int getArticleCountContainingTerm(String term) {
        int count = 0;
        String query = "SELECT COUNT(DISTINCT articleID) FROM articles WHERE content LIKE ?";

        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            // Use LIKE operator to search for articles containing the term (case-insensitive search)
            stmt.setString(1, "%" + term + "%");  // Search for articles that contain the term

            ResultSet rs = stmt.executeQuery();

            // Get the count of articles containing the term
            if (rs.next()) {
                count = rs.getInt(1);  // The count is in the first column
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public boolean deleteUser(String username) {
        String query = "DELETE FROM users WHERE username = ?";

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, username); // Set the username parameter for the query

            int rowsAffected = pstmt.executeUpdate();  // Execute the query to delete the user

            return rowsAffected > 0;  // Return true if the user was deleted (rowsAffected > 0)
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;  // Return false if there was an error deleting the user
        }
    }


    public boolean deleteArticle(int articleID) {
        String query = "DELETE FROM articles WHERE articleID = ?";

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, articleID); // Set the articleID parameter for the query

            int rowsAffected = pstmt.executeUpdate();  // Execute the query to delete the article

            return rowsAffected > 0;  // Return true if the article was deleted (rowsAffected > 0)
        } catch (SQLException e) {
            System.err.println("Error deleting article: " + e.getMessage());
            return false;  // Return false if there was an error deleting the article
        }
    }

    public void recordUserLike(int articleID) {
        String checkQuery = "SELECT COUNT(*) FROM user_likes WHERE userID = ? AND articleID = ?";
        String insertQuery = "INSERT INTO user_likes (userID, articleID) VALUES (?, ?)";

        try (Connection connection = getConnection()) {
            int userID = UserSession.getInstance().getUserID(); // Get the current logged-in user's ID

            // Check if the user has already liked this article
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, userID); // Set the userID parameter
                checkStmt.setInt(2, articleID); // Set the articleID parameter

                ResultSet resultSet = checkStmt.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // If the user has already liked this article, do nothing
                    System.out.println("User " + userID + " has already liked article " + articleID);
                    return;
                }
            }

            // If not already liked, insert the like record into the user_likes table
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, userID); // Set the userID parameter
                insertStmt.setInt(2, articleID); // Set the articleID parameter

                insertStmt.executeUpdate();
                System.out.println("User " + userID + " liked article " + articleID);
            }

        } catch (SQLException e) {
            System.err.println("Error recording user like: " + e.getMessage());
        }
    }

    public int getCurrentUserID() {
        return UserSession.getInstance().getUserID(); // Fetch the current user ID from the session
    }

    // Method to add a user's read activity (store in user_reads table)
    public boolean addUserReadActivity(UserReads userRead) {
        System.out.println(userRead.getUserID());;
        String query = "INSERT INTO user_reads (userID, articleID, readDate) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, userRead.getUserID()); // Set the userID from the UserReads object
            pstmt.setInt(2, userRead.getArticleID()); // Set the articleID from the UserReads object
            pstmt.setDate(3, new java.sql.Date(userRead.getReadDate().getTime())); // Set the readDate from the UserReads object (convert Date to SQL Date)

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if the activity was logged successfully
        } catch (SQLException e) {
            System.err.println("Error logging read activity: " + e.getMessage());
            return false; // Return false if there was an error
        }
    }



}
