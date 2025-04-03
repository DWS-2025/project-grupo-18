package es.grupo18.jobmatcher.service;

import java.io.IOException;
import java.util.ArrayList;
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
        userRepository.save(user);
        return toDTO(user);
    }

    public void deleteById(long id) { // Deletes a user by its id
        userRepository.deleteById(id);
    }

    public UserDTO delete(UserDTO user) { // Deletes a user
        userRepository.deleteById(toDomain(user).getId());
        return user;
    }

    public Collection<CompanyDTO> getFavouriteCompanies() {
        Long id = getLoggedUser().id();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return companyMapper.toDTOs(user.getFavouriteCompaniesList());
    }

    // Method to manage favourite companies

    public void addOrRemoveFavouriteCompany(Long userId, CompanyDTO companyDTO) {
        // Obtener el DTO actual desde el repositorio
        UserDTO currentUserDTO = findById(userId);
        List<CompanyDTO> favourites = new ArrayList<>(getFavouriteCompanies());

        boolean alreadyFavourite = favourites.stream()
                .anyMatch(c -> c.id().equals(companyDTO.id()));

        if (alreadyFavourite) {
            favourites.removeIf(c -> c.id().equals(companyDTO.id()));
        } else {
            favourites.add(companyDTO);
        }

        // Creamos un nuevo UserDTO con la lista modificada
        User user = toDomain(currentUserDTO);
        user.setFavouriteCompaniesList(companyMapper.toDomains(favourites)); // nuevo método mapper

        userRepository.save(user); // guardamos la entidad regenerada
    }

    public boolean isCompanyFavourite(CompanyDTO company) {
        User user = toDomain(getLoggedUser());
        return user.getFavouriteCompaniesList().contains(companyMapper.toDomain(company));
    }

    // Image methods

    public void removeImage() {
        User user = toDomain(getLoggedUser());
        user.setImageFile(null);
        userRepository.save(user);
    }

    public void updateUserImage(MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            User user = toDomain(getLoggedUser());
            user.setImageFile(imageFile.getBytes());
            user.setImageContentType(imageFile.getContentType());
            userRepository.save(user);
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
