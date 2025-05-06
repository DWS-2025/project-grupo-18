package es.grupo18.jobmatcher.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    private String encoded_password;
    private String phone;
    private String location;
    private String bio;
    private int experience;

    @Lob
    private byte[] image;
    private String imageContentType;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewsList;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> postsList;

    @ManyToMany
    @JoinTable(name = "user_company_favourites", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "company_id"))
    private List<Company> favouriteCompaniesList = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    // Empty constructor for initializations

    public User() {
    }

    // Complete constructor

    public User(String name, String email, String encoded_password, String phone, String location, String bio, int experience,
            byte[] image,
            List<Review> reviewsList, List<Post> postsList, List<Company> favouriteCompaniesList) {
        this.name = name;
        this.email = email;
        this.encoded_password = encoded_password;
        this.phone = phone;
        this.location = location;
        this.bio = bio;
        this.experience = experience;
        this.image = image;
        this.reviewsList = (reviewsList != null) ? new ArrayList<>(reviewsList) : new ArrayList<>();
        this.postsList = (postsList != null) ? new ArrayList<>(postsList) : new ArrayList<>();
        this.favouriteCompaniesList = (favouriteCompaniesList != null) ? new ArrayList<>(favouriteCompaniesList)
                : new ArrayList<>();
    }

    public boolean hasImage() {
        return image != null && image.length > 0 && imageContentType != null;
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

    public String getEncoded_password() {
        return encoded_password;
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

    public byte[] getImage() {
        return image;
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

    public List<String> getRoles() {
        return roles;
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

    public void setEncoded_password(String encoded_password) {
        this.encoded_password = encoded_password;
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

    public void setImage(byte[] image) {
        this.image = image;
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
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
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
                ", encoded_password='" + encoded_password + '\'' +
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
