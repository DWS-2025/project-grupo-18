package es.grupo18.jobmatcher.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String text;

    @Min(1)
    @Max(5)
    private int rating;

    @ManyToOne
    private User author;

    @ManyToOne
    private Post post;

    public Review() {
    }

    public Review(String text, int rating) {
        this.text = text;
        this.rating = rating;
    }

    // Getters

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }

    public User getAuthor() {
        return author;
    }

    public Post getPost() {
        return post;
    }

    // Setters

    public void setId(long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", rating=" + rating +
                ", author=" + author +
                ", post=" + post +
                '}';
    }

}
