package es.grupo18.jobmatcher.model;

import java.util.List;
import java.util.ArrayList;

public abstract class Account {
    private static long idCounter = 1;
    private long accountId;
    private String name;
    private String email;
    private String password;
    private List<Post> postsList;

    // Constructors

    public Account() {
        this.accountId = generateNewAccountId();
        this.postsList = new ArrayList<>();
    }

    public Account(String name, String email, String password){
        this.accountId = generateNewAccountId();
        this.name = name;
        this.email = email;
        this.password = password;
        this.postsList = new ArrayList<>();

    }

    public Account(long accountId, String name, String email, String password) {
        this.accountId = accountId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.postsList = new ArrayList<>();
    }


    public Account(long accountId, String name, String email, String password,  List<Post> postsList) {
        this.accountId = accountId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.postsList = (postsList != null) ? new ArrayList<>(postsList) : new ArrayList<>();
    }

    // Getters

    public long getAccountId() { return accountId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    // Setters

    public void setAccountId(long accountId) { this.accountId = accountId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    // Update methods

    public void updateName(String newName){this.name = newName;}
    public void updateEmail(String newEmail){this.email = newEmail;}
    public void updatePassword(String newPassword){this.password = newPassword;}
    
    // Methods to manage posts
    public void addPost(Post post) {
        if (post != null && !postsList.contains(post)) {
            postsList.add(post);
        }
    }

    public void removePost(Post post) {
        if (post != null) {
            postsList.remove(post);
        }
    }
    public List<Post> getPosts(){return new ArrayList<>(postsList);}

    // Generación segura de ID
    private static synchronized long generateNewAccountId(){
        return idCounter++;
    }

}
