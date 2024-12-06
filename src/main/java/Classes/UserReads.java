package Classes;

import java.util.Date;

public class UserReads {
    private int userID;
    private int articleID;
    private Date readDate;

    // Constructor
    public UserReads(int userID, int articleID, Date readDate) {
        this.userID = userID;
        this.articleID = articleID;
        this.readDate = readDate;
    }

    // Getters and Setters
    public int getUserID() {
        return userID;
    }


    public int getArticleID() {
        return articleID;
    }


    public Date getReadDate() {
        return readDate;
    }


    // Optional: Override toString() for easier debugging
    @Override
    public String toString() {
        return "UserReads [userID=" + userID + ", articleID=" + articleID + ", readDate=" + readDate + "]";
    }
}
