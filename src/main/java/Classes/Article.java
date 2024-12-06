package Classes;

import java.util.Date;

public class Article {
    private int articleID;
    private String title;
    private String content;
    private String category;
    private String source;
    private Date addedDate;

    // Constructors
    public Article(int articleID, String title, String content, String category, String source, Date addedDate) {
        this.articleID = articleID;
        this.title = title;
        this.content = content;
        this.category = category;
        this.source = source;
        this.addedDate = addedDate;
    }

    // Getters
    public int getArticleID() {
        return articleID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public String getSource() {
        return source;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    // toString method (optional for debugging)
    @Override
    public String toString() {
        return "Article ID: " + articleID + ", Title: " + title + ", Category: " + category;
    }
}
