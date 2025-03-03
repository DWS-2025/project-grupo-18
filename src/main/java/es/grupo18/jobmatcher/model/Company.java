package es.grupo18.jobmatcher.model;

import java.util.ArrayList;
import java.util.List;

public class Company extends Account {
    private String bio;
    private String location;
    private String imagePath;
    private List<User> favouriteUsersList;

    // Empty constructor
    public Company() {
        this.favouriteUsersList = new ArrayList<>();
    }

    // Constructor 
    public Company(long accountId, String name, String email, String password, String bio, String location, String imagePath) {
        super(accountId, name, email, password);
        this.bio = bio;
        this.location = location;
        this.imagePath = imagePath;
        this.favouriteUsersList = new ArrayList<>();
    }

    // Constructor completo
    public Company(long accountId, String name, String email, String password, String bio, String location, String imagePath, List<User> favouriteUsersList) {
        super(accountId, name, email, password);
        this.bio = bio;
        this.location = location;
        this.imagePath = imagePath;
        this.favouriteUsersList = (favouriteUsersList != null) ? new ArrayList<>(favouriteUsersList) : new ArrayList<>();
    }

    // Getters

    public String getBio() { return bio; }
    public String getImagePath() { return imagePath; }
    public String getLocation() { return location; }

    // Setters

    public void setBio(String bio) { this.bio = bio; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setLocation(String location) { this.location = location; }

    // Methods to manage favourite users

    public void addFavouriteUser(User user) {
        if (user != null && !favouriteUsersList.contains(user)) {
            favouriteUsersList.add(user);
        }
    }

    public void removeFavouriteUser(User user) {
        if (user != null) {
            favouriteUsersList.remove(user);
        }
    }
    
    public List<User> getFavouriteUsers(){return new ArrayList<>(favouriteUsersList);}

    // Image methods

    public void removeImage() {
        this.imagePath = null;
    }

}
