package es.grupo18.jobmatcher.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity(name = "UserTable")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String phone;
    private String location;
    private String bio;
    private int experience;

    @Lob
    private byte[] imageFile;
    private String imageContentType;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewsList;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> postsList;

    @ManyToMany
    @JoinTable(name = "user_company_favourites", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "company_id"))
    private List<Company> favouriteCompaniesList = new ArrayList<>();

    // Empty constructor for initializations

    public User() {
    }

    // Complete constructor

    public User(String name, String email, String password, String phone, String location, String bio, int experience,
            byte[] imageFile,
            List<Review> reviewsList, List<Post> postsList, List<Company> favouriteCompaniesList) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.location = location;
        this.bio = bio;
        this.experience = experience;
        this.imageFile = imageFile;
        this.reviewsList = (reviewsList != null) ? new ArrayList<>(reviewsList) : new ArrayList<>();
        this.postsList = (postsList != null) ? new ArrayList<>(postsList) : new ArrayList<>();
        this.favouriteCompaniesList = (favouriteCompaniesList != null) ? new ArrayList<>(favouriteCompaniesList)
                : new ArrayList<>();
    }

    public boolean hasImage() {
        return imageFile != null && imageFile.length > 0 && imageContentType != null;
    }

    // Getters

    public Long getId() {
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

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public String getBio() {
        return bio;
    }

    public int getExperience() {
        return experience;
    }

    public byte[] getImageFile() {
        return imageFile;
    }

    public String getImageContentType() {
        return imageContentType;
    }
    
    public List<Review> getReviews() {
        return reviewsList;
    }

    public List<Post> getPosts() {
        return postsList;
    }

    public List<Company> getFavouriteCompaniesList() {
        return favouriteCompaniesList;
    }

    // Setters

    public void setId(Long id) {
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setImageFile(byte[] imageFile) {
        this.imageFile = imageFile;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public void setReviews(List<Review> reviewsList) {
        this.reviewsList = reviewsList;
    }

    public void setPosts(List<Post> postsList) {
        this.postsList = postsList;
    }

    public void setFavouriteCompaniesList(List<Company> favouriteCompaniesList) {
        this.favouriteCompaniesList = favouriteCompaniesList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        User user = (User) o;

        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", location='" + location + '\'' +
                ", bio='" + bio + '\'' +
                ", experience='" + experience + '\'' +
                ", reviewsList=" + reviewsList + '\'' +
                ", postsList=" + postsList + '\'' +
                ", favouriteCompaniesList=" + favouriteCompaniesList + '\'' +
                '}';
    }

}
