package Classes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RegularUser extends User {

    // Constructor
    public RegularUser(String userID, String username, String password) {
        super(userID, username, password, 0); // 0 for regular users, indicating they are not admins
    }

    @Override
    public boolean hasAdminPrivileges() {
        return false;
    }

    public void accessUserDashboard(Stage loginStage) {
        try {
            // Ensure the correct path to the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Controllers/userArticleView.fxml")); // Correct path
            Parent root = loader.load();

            // Create a new stage for the admin dashboard
            Stage stage = new Stage();
            stage.setTitle("User Dashboard");
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
