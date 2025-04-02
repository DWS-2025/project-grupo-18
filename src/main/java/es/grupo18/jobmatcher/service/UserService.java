package es.grupo18.jobmatcher.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.grupo18.jobmatcher.dto.CompanyDTO;
import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.mapper.CompanyMapper;
import es.grupo18.jobmatcher.mapper.UserMapper;
import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CompanyMapper companyMapper;

    /**
     * Returns always the same user for simplicity before adding authentication
     * 
     * @return User
     */

    // === ENTITIES ===

    public UserDTO getLoggedUser() {
        return toDTO(userRepository.findAll().get(0));
    }

    public Collection<UserDTO> findAll() {
        return toDTOs(userRepository.findAll());
    }

    public UserDTO findById(long id) {
        return toDTO(userRepository.findById(id).orElse(null));
    }

    public UserDTO save(UserDTO user) { // Saves a user
        userRepository.save(toDomain(user));
        return user;
    }

    void save(User user) {

    }

    public UserDTO save(UserDTO userDTO, MultipartFile imageFile) throws IOException { // Saves a user with an image
        User user = toDomain(userDTO);
        if (!imageFile.isEmpty()) {
            user.setImageFile(imageFile.getBytes());
            user.setImageContentType(imageFile.getContentType());
        }
        this.save(user);
        return toDTO(user);
    }

    public UserDTO update(String name, String email, String password, String phone, String location, String bio,
            int experience) { // Updates the user's profile
        User user = toDomain(getLoggedUser());
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setLocation(location);
        user.setBio(bio);
        user.setExperience(experience);
        return toDTO(user);
    }

    public void deleteById(long id) { // Deletes a user by its id
        userRepository.deleteById(id);
    }

    public UserDTO delete(UserDTO user) { // Deletes a user
        userRepository.deleteById(toDomain(user).getId());
        return user;
    }

    // Method to manage favourite companies

    public void addOrRemoveFavouriteCompany(Long userId, CompanyDTO company) {
        User user = toDomain(getLoggedUser());
        if (user.getFavouriteCompaniesList().contains(companyMapper.toDomain(company))) {
            user.getFavouriteCompaniesList().remove(companyMapper.toDomain(company));
        } else {
            user.getFavouriteCompaniesList().add(companyMapper.toDomain(company));
        }
        userRepository.save(user);
    }

    public boolean isCompanyFavourite(CompanyDTO company) {
        User user = toDomain(getLoggedUser());
        return user.getFavouriteCompaniesList().contains(companyMapper.toDomain(company));
    }

    // Image methods

    public void removeImage() {
        User user = toDomain(getLoggedUser());
        user.setImageFile(null);
    }

    public void updateUserImage(MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            User user = toDomain(getLoggedUser());
            user.setImageFile(imageFile.getBytes());
            user.setImageContentType(imageFile.getContentType());
            save(user);
        }
    }

    private UserDTO toDTO(User user) {
        return userMapper.toDTO(user);
    }

    private User toDomain(UserDTO userDTO) {
        return userMapper.toDomain(userDTO);
    }

    private List<UserDTO> toDTOs(List<User> users) {
        return userMapper.toDTOs(users);
    }

}
