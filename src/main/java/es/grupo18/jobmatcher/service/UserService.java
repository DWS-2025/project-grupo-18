package es.grupo18.jobmatcher.service;

import java.io.IOException;
import java.util.List;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Returns always the same user for simplicity before adding authentication
     * 
     * @return User
     */

    public User getLoggedUser() {
        return userRepository.findAll().get(0);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void save(User user) { // Saves a user
        userRepository.save(user);
    }

    public void save(User user, MultipartFile imageFile) throws IOException { // Saves a user with an image
        if (!imageFile.isEmpty()) {
            user.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(),
                    imageFile.getSize()));
        }
        this.save(user);
    }

    public void update(String name, String email, String password, String phone, String location, String bio,
            int experience) { // Updates the user's profile
        User user = getLoggedUser();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setLocation(location);
        user.setBio(bio);
        user.setExperience(experience);
    }

    public void deleteById(long id) { // Deletes a user by its id

    }

    public void delete(User user) { // Deletes a user
        userRepository.deleteById(user.getId());
    }

    // Method to manage favourite companies

    public void addOrRemoveFavouriteCompany(Long userId, Company company) {
        User user = getLoggedUser();
        if (user.getFavouriteCompaniesList().contains(company)) {
            user.getFavouriteCompaniesList().remove(company);
        } else {
            user.getFavouriteCompaniesList().add(company);
        }
        userRepository.save(user);
    }

    public boolean isCompanyFavourite(Company company) {
        User user = getLoggedUser();
        return user.getFavouriteCompaniesList().contains(company);
    }

    // Image methods

    public void removeImage() {
        User user = getLoggedUser();
        user.setImageFile(null);
    }

    public void updateUserImage(MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            User user = getLoggedUser();
            user.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
            save(user);
        }
    }

}
