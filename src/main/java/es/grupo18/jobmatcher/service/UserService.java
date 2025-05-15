package es.grupo18.jobmatcher.service;

import es.grupo18.jobmatcher.dto.CompanyDTO;
import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.mapper.CompanyMapper;
import es.grupo18.jobmatcher.mapper.UserMapper;
import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.repository.UserRepository;
import es.grupo18.jobmatcher.security.RepositoryUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

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

    private static final String CV_STORAGE_DIR = "cv_storage";
    private static final Path CV_STORAGE_PATH = Paths.get(System.getProperty("user.dir"), CV_STORAGE_DIR)
            .toAbsolutePath().normalize();

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
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
                    List.of("USER"),
                    user.cvFileName());
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

            String contentType = image.getContentType();
            if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/jpg") ||
                    contentType.equals("image/png") || contentType.equals("image/webp"))) {
                throw new IllegalArgumentException("Formato de imagen no válido");
            }

            existingUser.setImage(image.getBytes());
            existingUser.setImageContentType(contentType);
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

    public void uploadCv(MultipartFile file) throws IOException {
        Long userId = getLoggedUser().id();
        uploadCv(file, userId);
    }

    public void uploadCv(MultipartFile file, Long userId) throws IOException {
        User user = getUserOrThrow(userId);
        validatePdf(file);

        Path userDir = getUserCvDir(user);
        Files.createDirectories(userDir);

        deleteExistingCv(user, userDir);

        String newFileName = UUID.randomUUID() + ".pdf";
        file.transferTo(userDir.resolve(newFileName));

        user.setCvFileName(newFileName);
        userRepository.save(user);
    }

    public File getCvFile() {
        return getCvFile(getLoggedUser().id());
    }

    public File getCvFile(Long userId) {
        User user = getUserOrThrow(userId);
        if (user.getCvFileName() == null)
            throw new IllegalStateException("No hay CV disponible");

        Path path = getUserCvDir(user).resolve(sanitizeFilename(user.getCvFileName()));
        File file = path.toFile();
        if (!file.exists())
            throw new IllegalStateException("Archivo CV no encontrado");

        return file;
    }

    public void deleteCv() throws IOException {
        deleteCv(getLoggedUser().id());
    }

    public void deleteCv(Long userId) throws IOException {
        User user = getUserOrThrow(userId);
        if (user.getCvFileName() == null)
            return;

        Path userDir = getUserCvDir(user);
        Path cvPath = userDir.resolve(sanitizeFilename(user.getCvFileName()));
        Files.deleteIfExists(cvPath);

        File dir = userDir.toFile();
        if (dir.exists() && dir.isDirectory() && dir.list().length == 0)
            dir.delete();

        user.setCvFileName(null);
        userRepository.save(user);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }

    private void validatePdf(MultipartFile file) throws IOException {
        if (file.isEmpty())
            throw new IllegalArgumentException("El archivo está vacío");

        if (!"application/pdf".equals(file.getContentType()))
            throw new IllegalArgumentException("Solo se permiten archivos PDF");

        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".pdf"))
            throw new IllegalArgumentException("Nombre de archivo inválido");

        try (InputStream in = file.getInputStream()) {
            byte[] header = new byte[4];
            if (in.read(header) != 4 || header[0] != '%' || header[1] != 'P' || header[2] != 'D' || header[3] != 'F')
                throw new IllegalArgumentException("El archivo no parece un PDF válido");
        }
    }

    private void deleteExistingCv(User user, Path userDir) throws IOException {
        if (user.getCvFileName() != null) {
            Path path = userDir.resolve(sanitizeFilename(user.getCvFileName()));
            Files.deleteIfExists(path);
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
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

    public void deleteCurrentUserAndLogout(HttpServletResponse response) {
        UserDTO user = getLoggedUser();
        userRepository.deleteById(user.id());

        clearAuthCookies(response);

        SecurityContextHolder.clearContext();
    }

    private void clearAuthCookies(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("AuthToken", null);
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        accessCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie("RefreshToken", null);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
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
                new ArrayList<>(),
                null);
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

    private Path getUserCvDir(User user) {
        String input = "cv-salt-" + user.getId();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            String hex = bytesToHex(hash).substring(0, 16); // 16 hex chars = 8 bytes
            return CV_STORAGE_PATH.resolve("u" + hex);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}