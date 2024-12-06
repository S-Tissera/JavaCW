package Classes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminUser extends User {

    // Constructor to accept userID, username, password, and isAdmin
    public AdminUser(String userID, String username, String password, int isAdmin) {
        super(userID, username, password, isAdmin);  // Call the parent class constructor with all parameters
    }

    @Override
    public boolean hasAdminPrivileges() {
        return true; // Admins have privileges
    }

    @FXML
    public void accessAdminDashboard(Stage loginStage) {
        try {
            // Ensure the correct path to the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Controllers/adminOptions.fxml")); // Correct path
            Parent root = loader.load();

            // Create a new stage for the admin dashboard
            Stage stage = new Stage();
            stage.setTitle("Admin Dashboard");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();

            // Close the login page (current window)
            loginStage.close(); // Close the passed login stage

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading admin dashboard.");
        }
    }

}
