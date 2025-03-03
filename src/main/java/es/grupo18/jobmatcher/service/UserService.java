package es.grupo18.jobmatcher.service;

import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.model.User;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Arrays;

@Service
public class UserService {

    private final User user;

    public UserService() {
        // Simulated user
        user = new User(
            1L, 
            "Juan Javier", 
            "juja@gmail.com", 
            "password123",
            "651479899", 
            "Carballo, A Coruña", 
            "Y tal",
            5, 
            List.of("Grado en Ingeniería Informática", "Máster en Ciberseguridad"),
            List.of("Java", "Spring Boot", "SQL", "Pentesting"),
            "/img/profile.jpg"
        );
    }

    public User getUser() { // Returns the only user in memory
        return user;
    }



    public void updateUserProfile(String name, String email, String phone, String location, String about) { // Updates the user's profile
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setLocation(location);
        user.setBio(about);
    }

    public void updateUserImage(String imagePath) { // Updates the user's image
        user.setImagePath(imagePath);
    }

    public void updateUserDetails(String studies, String skills, Integer experience) { // Updates the user's details
        user.setDegrees(Arrays.asList(studies.split(",\\s*")));
        user.setSkills(Arrays.asList(skills.split(",\\s*")));
        user.setExperience(experience);
    }

    public boolean toggleFavouriteCompany(Company company) {
        if (user.getFavouriteCompanies().contains(company)) {
            user.getFavouriteCompanies().remove(company);
            return false; // Removed from favourites
        } else {
            user.getFavouriteCompanies().add(company);
            return true; // Added to favourites
        }
    }
    
}
