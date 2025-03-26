package es.grupo18.jobmatcher.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity(name = "CompanyTable")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String email;
    private String password;
    private String location;
    private String bio;
    private String imagePath;

    @ManyToMany(mappedBy = "favouriteCompaniesList")
    private List<User> favouriteUsersList = new ArrayList<>();

    public Company() {
    }

    // Complete constructor

    public Company(String name, String email, String password, String location, String bio, String imagePath,
            List<User> favouriteUsersList) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.location = location;
        this.bio = bio;
        this.imagePath = imagePath;
        this.favouriteUsersList = (favouriteUsersList != null) ? new ArrayList<>(favouriteUsersList)
                : new ArrayList<>();
    }

    // Getters

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getLocation() {
        return location;
    }

    public String getBio() {
        return bio;
    }

    public String getImagePath() {
        return imagePath;
    }

    public List<User> getFavouriteUsersList() {
        return favouriteUsersList;
    }

    // Setters

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setFavouriteUsersList(List<User> favouriteUsersList) {
        this.favouriteUsersList = favouriteUsersList;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", location='" + location + '\'' +
                ", bio='" + bio + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", favouriteUsersList=" + favouriteUsersList +
                '}';
    }

}
