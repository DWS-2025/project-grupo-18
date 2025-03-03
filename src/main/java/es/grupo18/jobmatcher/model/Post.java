package es.grupo18.jobmatcher.model;

public class Post {

    private static long idCounter = 1; // ID counter to generate unique IDs
    private long postId;
    private String title;
    private String content;
    private String timestamp; 
    private String imagePath;
    private Account owner;

    // Empty constructor

    public Post() {
        this.postId = generateNewPostId();
    }

    // Completo sin id, generada automaticamente
    public Post(String title, String content, String timestamp, String imagePath, Account owner) {
        this(generateNewPostId(), title, content, timestamp, imagePath, owner);
    }

    // Constructor completo
    public Post(long postId, String title, String content, String timestamp, String imagePath, Account owner) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.imagePath = imagePath;
        this.owner = owner;
    }

    // Automathic ID generator

    private static synchronized long generateNewPostId() {
        return idCounter++;
    }

    // Getters

    public long getPostId() { return postId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    public String getImagePath() { return imagePath; }
    public Account getOwner() { return owner; }

    // Setters

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setOwner(Account owner) { this.owner = owner; }

    // Image method

    public void removeImage() { this.imagePath = null; }

}
