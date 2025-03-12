package es.grupo18.jobmatcher.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;
    private String content;
    private String timestamp;
    private String imagePath;

    @ManyToOne
    private User author;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    // Only generates new ID

    public Post() {
    }

    // Complete without ID

    public Post(String title, String content, String timestamp, String imagePath) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.imagePath = imagePath;
    }

    // Getters

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getImagePath() {
        return imagePath;
    }

    public User getAuthor() {
        return author;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    // Setters

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "Post [id=" + id + ", title=" + title + ", content=" + content + ", timestamp=" + timestamp
                + ", imagePath="
                + imagePath + ", owner=" + author + ", reviews=" + reviews + "]";
    }

}
