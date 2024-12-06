package Classes;

public class UserSession {

    // Singleton instance
    private static UserSession instance;

    // User session data
    private User currentUser;

    // Private constructor to prevent instantiation
    private UserSession() {}

    // Method to get the singleton instance of the session
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Set the user session details
    public void setUser(User user) {
        this.currentUser = user;
    }

    // Getters for the user session details
    public String getUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.hasAdminPrivileges();
    }

    public int getUserID() {
        return currentUser != null ? Integer.parseInt(currentUser.getUserID()) : 0;
    }

    // Clear the user session
    public void clearSession() {
        currentUser = null;
    }

    // Get the current user object (can be AdminUser or RegularUser)
    public User getCurrentUser() {
        return currentUser;
    }
}
