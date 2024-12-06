package Classes;

public abstract class User {
    private String userID;
    private String username;
    private String password;
    private int isAdmin; // 0 for regular user, 1 for admin

    // Constructors
    public User(String userID, String username, String password, int isAdmin) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // Getters and Setters
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    // Utility method to check if user has admin privileges
    public boolean hasAdminPrivileges() {
        return isAdmin == 1;  // Returns true if the user is an admin
    }


    public boolean isAdmin() {
        return this.isAdmin == 1; // Return true if admin (1), otherwise false
    }
}
