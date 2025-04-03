package es.grupo18.jobmatcher.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @Column(length = 1000)
    private String content;

    private LocalDateTime timestamp;

    @Lob
    private byte[] image;
    private String imageContentType;

    @ManyToOne
    private User author;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Review> reviews = new ArrayList<>();

    // Only generates new ID

    public Post() {
    }

    // Complete without ID

    public Post(String title, String content, LocalDateTime timestamp, String imagePath) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Post(String title, String content, LocalDateTime timestamp, String imagePath, User author) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.author = author;
    }

    public Post(String title, String content, LocalDateTime date, byte[] image, User user) {
        this.title = title;
        this.content = content;
        this.timestamp = date;
        this.image = image;
        this.author = user;
        this.reviews = new ArrayList<>();
    }

    public boolean hasImage() {
        return image != null && image.length > 0 && imageContentType != null;
    }

    // Getters

    public long getId() {
        return id;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public byte[] getImage() {
        return image;
    }

    public User getAuthor() {
        return author;
    }

    public List<Review> getReviews() {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        return reviews;
    }

    // Setters
    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getFormattedTimestamp() {
        if (timestamp == null)
            return "";
        return timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Post post = (Post) o;

        return id == post.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "Post [id=" + id + ", title=" + title + ", content=" + content + ", timestamp=" + timestamp
                + ", imagePath="
                + image + ", owner=" + author + ", reviews=" + reviews + "]";
    }

}
