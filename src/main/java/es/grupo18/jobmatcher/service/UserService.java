package es.grupo18.jobmatcher.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

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

    public UserDTO save(UserDTO userDTO, MultipartFile image) throws IOException {
        User existingUser = userRepository.findById(userDTO.id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!image.isEmpty()) {
            existingUser.setImage(image.getBytes());
            existingUser.setImageContentType(image.getContentType());
        }

        userRepository.save(existingUser);
        return toDTO(existingUser);
    }

    public UserDTO update(long id, UserDTO dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setName(dto.name());
        existingUser.setEmail(dto.email());
        existingUser.setPhone(dto.phone());
        existingUser.setLocation(dto.location());
        existingUser.setBio(dto.bio());
        existingUser.setExperience(dto.experience());

        userRepository.save(existingUser);
        return toDTO(existingUser);
    }

    public UserDTO updateProfile(UserDTO updatedDto) {
        User currentUser = userRepository.findById(getLoggedUser().id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        currentUser.setName(updatedDto.name());
        currentUser.setEmail(updatedDto.email());
        currentUser.setPhone(updatedDto.phone());
        currentUser.setLocation(updatedDto.location());
        currentUser.setBio(updatedDto.bio());
        currentUser.setExperience(updatedDto.experience());

        userRepository.save(currentUser);

        return toDTO(currentUser);
    }

    public void deleteById(long id) { // Deletes a user by its id
        userRepository.deleteById(id);
    }

    public UserDTO delete(UserDTO user) { // Deletes a user
        userRepository.deleteById(toDomain(user).getId());
        return user;
    }

    // Method to manage favourite companies

    public void addOrRemoveFavouriteCompany(Long userId, CompanyDTO companyDTO) {

        UserDTO currentUserDTO = findById(userId);
        List<CompanyDTO> favourites = new ArrayList<>(getFavouriteCompanies());

        boolean alreadyFavourite = favourites.stream()
                .anyMatch(c -> c.id().equals(companyDTO.id()));

        if (alreadyFavourite) {
            favourites.removeIf(c -> c.id().equals(companyDTO.id()));
        } else {
            favourites.add(companyDTO);
        }

        User user = toDomain(currentUserDTO);
        user.setFavouriteCompaniesList(companyMapper.toDomains(favourites));

        userRepository.save(user);
    }

    public boolean isCompanyFavourite(CompanyDTO company) {
        User user = toDomain(getLoggedUser());
        return user.getFavouriteCompaniesList().contains(companyMapper.toDomain(company));
    }

    public Collection<CompanyDTO> getFavouriteCompanies() {
        try {
            UserDTO user = findById(getLoggedUser().id());
            return companyMapper.toDTOs(userMapper.toDomain(user).getFavouriteCompaniesList());
        } catch (NoSuchElementException e) {
            throw new RuntimeException("User not found", e);
        }
    }

    // Image methods

    public void removeImage() {
        UserDTO dto = getLoggedUser();
        User existingUser = userRepository.findById(dto.id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setImage(null);
        existingUser.setImageContentType(null);

        userRepository.save(existingUser);
    }

    public void updateUserImage(MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            UserDTO dto = getLoggedUser();
            User existingUser = userRepository.findById(dto.id())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            existingUser.setImage(image.getBytes());
            existingUser.setImageContentType(image.getContentType());

            userRepository.save(existingUser);
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
