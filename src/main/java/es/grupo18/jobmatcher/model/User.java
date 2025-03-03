package es.grupo18.jobmatcher.model;

import java.util.ArrayList;
import java.util.List;

public class User extends Account {

    private String phone;
    private String location;
    private String bio;
    private Integer experience;
    private List<String> degreesList;
    private List<String> skillsList;
    private String imagePath;
    private List<Company> favouriteCompaniesList;

    // Empty constructor for initializations

    public User() {
        this.degreesList = new ArrayList<>();
        this.skillsList = new ArrayList<>();
        this.favouriteCompaniesList = new ArrayList<>();
        this.experience = 0;
    }

    // Constructor without favourite companies
    
    public User(Long accountId, String name, String email, String password, String phone, String location, 
                String bio, Integer experience, List<String> degreesList, List<String> skillsList, String imagePath) {
        super(accountId, name, email, password);
        this.phone = phone;
        this.location = location;
        this.bio = bio;
        this.experience = experience;
        this.degreesList = (degreesList != null) ? new ArrayList<>(degreesList) : new ArrayList<>();
        this.skillsList = (skillsList != null) ? new ArrayList<>(skillsList) : new ArrayList<>();
        this.imagePath = imagePath;
        this.favouriteCompaniesList = new ArrayList<>();
    }

    // Constructor with only bio and image

    public User(Long accountId, String name, String email, String password, String bio, String imagePath) {
        super(accountId, name, email, password);
        this.bio = bio;
        this.imagePath = imagePath;
        this.degreesList = new ArrayList<>();
        this.skillsList = new ArrayList<>();
        this.favouriteCompaniesList = new ArrayList<>();
        this.experience = 0;
    }

    // Getters

    public String getPhone() { return phone; }
    public String getLocation() { return location; }
    public String getBio() { return bio; }
    public Integer getExperience() { return experience; }
    public String getImagePath() { return imagePath; }

    // Return copies of the lists to avoid modifications

    public List<String> getDegrees() { return new ArrayList<>(degreesList); }
    public List<String> getSkills() { return new ArrayList<>(skillsList); }

    // Setters

    public void setPhone(String phone) { this.phone = phone; }
    public void setLocation(String location) { this.location = location; }
    public void setBio(String bio) { this.bio = bio; }
    public void setExperience(Integer experience) { this.experience = experience; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    // Secure methods to modify lists

    public void setDegrees(List<String> degreesList) {
        this.degreesList = (degreesList != null) ? new ArrayList<>(degreesList) : new ArrayList<>();
    }

    public void setSkills(List<String> skillsList) {
        this.skillsList = (skillsList != null) ? new ArrayList<>(skillsList) : new ArrayList<>();
    }

    public void setFavouriteCompanies(List<Company> favouriteCompaniesList) {
        this.favouriteCompaniesList = (favouriteCompaniesList != null) ? new ArrayList<>(favouriteCompaniesList) : new ArrayList<>();
    }

    // Methods to manage favourite companies

    public void addFavouriteCompany(Company company) {
        if (company != null && !favouriteCompaniesList.contains(company)) {
            favouriteCompaniesList.add(company);
        }
    }

    public void removeFavouriteCompany(Company company) {
        if (company != null) {
            favouriteCompaniesList.remove(company);
        }
    }

    public List<Company> getFavouriteCompanies() { return new ArrayList<>(favouriteCompaniesList); }

    // Image methods

    public void removeImage() {
        this.imagePath = null;
    }
    
}
