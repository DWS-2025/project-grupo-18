package es.grupo18.jobmatcher.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.grupo18.jobmatcher.dto.CompanyDTO;
import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.mapper.CompanyMapper;
import es.grupo18.jobmatcher.mapper.UserMapper;
import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.repository.UserRepository;
import es.grupo18.jobmatcher.security.RepositoryUserDetailsService;

@Service
public class UserService {

    @Autowired
    private RepositoryUserDetailsService repositoryUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CompanyMapper companyMapper;

    // === ENTITIES ===

    public UserDTO getLoggedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return toDTO(userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email)));
    }

    public Collection<UserDTO> findAll() {
        return toDTOs(userRepository.findAll());
    }

    public UserDTO findById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toDTO(user);
    }

    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDTO);
    }

    public UserDTO save(UserDTO user) {
        if (user.roles() == null || user.roles().isEmpty()) {
            user = new UserDTO(
                    user.id(),
                    user.name(),
                    user.email(),
                    user.phone(),
                    user.location(),
                    user.bio(),
                    user.experience(),
                    user.image(),
                    user.imageContentType(),
                    List.of("USER"));
        }
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

    public void deleteById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public UserDTO delete(UserDTO user) {
        userRepository.deleteById(toDomain(user).getId());
        return user;
    }

    // Method to manage favourite companies
    public void addOrRemoveFavouriteCompany(Long userId, CompanyDTO companyDTO) {
        UserDTO currentUserDTO = findById(userId);

        User updatedUser = toDomain(currentUserDTO);

        User originalUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        updatedUser.setPosts(originalUser.getPosts());
        updatedUser.setReviews(originalUser.getReviews());

        List<Company> favourites = new ArrayList<>(originalUser.getFavouriteCompaniesList());

        if (favourites.stream().anyMatch(c -> c.getId() == companyDTO.id())) {
            favourites.removeIf(c -> c.getId() == companyDTO.id());
        } else {
            favourites.add(companyMapper.toDomain(companyDTO));
        }

        updatedUser.setFavouriteCompaniesList(favourites);
        userRepository.save(updatedUser);
    }

    public boolean isCompanyFavourite(CompanyDTO company) {
        User user = userRepository.findById(getLoggedUser().id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getFavouriteCompaniesList().stream()
                .anyMatch(c -> c.getId() == company.id());
    }

    public Collection<CompanyDTO> getFavouriteCompanies() {
        User user = userRepository.findById(getLoggedUser().id())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return companyMapper.toDTOs(user.getFavouriteCompaniesList());
    }

    public void removeCompanyFromAllUsers(Long companyId) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getFavouriteCompaniesList().removeIf(c -> c.getId() == companyId)) {
                userRepository.save(user);
            }
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

    public void removeImageById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getImage() == null) {
            throw new RuntimeException("Image not found");
        }

        user.setImage(null);
        user.setImageContentType(null);
        userRepository.save(user);
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

    public void updateUserImage(Long id, MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            existingUser.setImage(image.getBytes());
            existingUser.setImageContentType(image.getContentType());

            userRepository.save(existingUser);
        }
    }

    public void removeImage(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getImage() == null) {
            throw new RuntimeException("Image not found");
        }

        user.setImage(null);
        user.setImageContentType(null);
        userRepository.save(user);
    }

    // Helpers

    private UserDTO toDTO(User user) {
        return userMapper.toDTO(user);
    }

    private User toDomain(UserDTO userDTO) {
        return userMapper.toDomain(userDTO);
    }

    private List<UserDTO> toDTOs(List<User> users) {
        return userMapper.toDTOs(users);
    }

    public UserDTO createEmpty() {
        return new UserDTO(
                null,
                "",
                "",
                "",
                "",
                "",
                0,
                null,
                null,
                new ArrayList<>());
    }

    public boolean isAdmin(UserDTO user) {
        return user.roles() != null && user.roles().contains("ADMIN");
    }

    public boolean isUser(UserDTO user) {
        return user.roles() != null && user.roles().contains("USER");
    }

    public boolean hasRole(String username, String role) {
        return repositoryUserDetailsService.loadUserByUsername(username).getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }

}
