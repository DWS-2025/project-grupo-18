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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String email;
    private String location;
    private String bio;

    @ManyToMany(mappedBy = "favouriteCompaniesList")
    private List<User> favouriteUsersList = new ArrayList<>();

    public Company() {
    }

    // Complete constructor

    public Company(String name, String email, String location, String bio, String imagePath,
            List<User> favouriteUsersList) {
        this.name = name;
        this.email = email;
        this.location = location;
        this.bio = bio;
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

    public String getLocation() {
        return location;
    }

    public String getBio() {
        return bio;
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

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setFavouriteUsersList(List<User> favouriteUsersList) {
        this.favouriteUsersList = favouriteUsersList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Company company = (Company) o;

        return id == company.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", location='" + location + '\'' +
                ", bio='" + bio + '\'' +
                ", favouriteUsersList=" + favouriteUsersList +
                '}';
    }

}
