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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

    @Autowired
    private PasswordEncoder encoder;

    private static final String CV_STORAGE_DIR = "cv_storage";
    private static final Path CV_STORAGE_PATH = Paths.get(System.getProperty("user.dir"), CV_STORAGE_DIR)
            .toAbsolutePath().normalize();

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserDTO getLoggedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return toDTO(userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email)));
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

    public UserDTO save(UserDTO dto) {
        if (userRepository.existsByEmail(dto.email()))
            throw new RuntimeException("Email already exists");

        User user = userMapper.toDomain(dto);

        if (user.getRoles() == null || user.getRoles().isEmpty())
            user.setRoles(List.of("USER"));

        return userMapper.toDTO(userRepository.save(user));
    }

    public UserDTO save(UserDTO dto, String rawPassword) {

        User user = userMapper.toDomain(dto);

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(List.of("USER"));
        }

        user.setEncoded_password(
                encoder.encode(
                        (rawPassword == null || rawPassword.isBlank())
                                ? UUID.randomUUID().toString()
                                : rawPassword));

        userRepository.save(user);

        return userMapper.toDTO(user);
    }

    public UserDTO save(UserDTO userDTO, MultipartFile image) throws IOException {
        User existingUser = userRepository.findById(userDTO.id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!image.isEmpty()) {
            validateImage(image);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage originalImage = ImageIO.read(image.getInputStream());
            if (originalImage == null) {
                throw new IllegalArgumentException("The image cannot be processed");
            }

            String format = image.getContentType().equals("image/png") ? "png" : "jpg";
            ImageIO.write(originalImage, format, baos);
            baos.flush();

            existingUser.setImage(baos.toByteArray());
            baos.close();

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

        updatedUser.setEncoded_password(originalUser.getEncoded_password());

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
        removeImageById(dto.id());
    }

    public void removeImageById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        clearImage(user);
    }

    private void clearImage(User user) {
        if (user.getImage() == null) {
            throw new RuntimeException("Image not found");
        }
        user.setImage(null);
        user.setImageContentType(null);
        userRepository.save(user);
    }

    private void processAndSaveImage(User user, MultipartFile image) throws IOException {
        validateImage(image);

        if (image.isEmpty()) {
            throw new IllegalArgumentException("The image cannot be empty");
        }

        BufferedImage originalImage = ImageIO.read(image.getInputStream());
        if (originalImage == null) {
            throw new IllegalArgumentException("The image cannot be processed");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String format = image.getContentType().equals("image/png") ? "png" : "jpg";
        ImageIO.write(originalImage, format, baos);
        baos.flush();

        user.setImage(baos.toByteArray());
        user.setImageContentType(image.getContentType());

        baos.close();
        userRepository.save(user);
    }

    public void updateUserImage(MultipartFile image) throws IOException {
        UserDTO dto = getLoggedUser();
        User user = userRepository.findById(dto.id())
                .orElseThrow(() -> new RuntimeException("User not found"));
        processAndSaveImage(user, image);
    }

    public void updateUserImage(Long id, MultipartFile image) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        processAndSaveImage(user, image);
    }

    private void validateImage(MultipartFile file) throws IOException {
        if (file.isEmpty())
            throw new IllegalArgumentException("File is empty");
        if (file.getSize() > 2 * 1024 * 1024)
            throw new IllegalArgumentException("Maximum allowed: 2MB");

        String contentType = file.getContentType();
        if (!List.of("image/jpeg", "image/png").contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG or PNG are allowed");
        }

        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            is.read(header);
            if (!(isJpeg(header) || isPng(header))) {
                throw new IllegalArgumentException("Invalid header. The file is not a valid image");
            }
        }

        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Corrupted image or it cannot be processed");
        }

    }

    private boolean isJpeg(byte[] header) {
        return header[0] == (byte) 0xFF && header[1] == (byte) 0xD8;
    }

    private boolean isPng(byte[] header) {
        return header[0] == (byte) 0x89 && header[1] == (byte) 0x50 &&
                header[2] == (byte) 0x4E && header[3] == (byte) 0x47;
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

        String originalFileName = Paths.get(file.getOriginalFilename()).getFileName().toString();
        Path destination = userDir.resolve(originalFileName);
        file.transferTo(destination);

        user.setCvFileName(originalFileName);

        userRepository.save(user);
    }

    public File getCvFile() {
        return getCvFile(getLoggedUser().id());
    }

    public File getCvFile(Long userId) {
        User user = getUserOrThrow(userId);
        if (user.getCvFileName() == null)
            throw new IllegalStateException("No CV available");

        Path userDir = getUserCvDir(user);
        Path path = userDir.resolve(Paths.get(user.getCvFileName()).getFileName().toString());
        File file = path.toFile();
        if (!file.exists())
            throw new IllegalStateException("CV file not found");

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
        Path path = userDir.resolve(Paths.get(user.getCvFileName()).getFileName().toString());
        Files.deleteIfExists(path);

        File dir = userDir.toFile();
        if (dir.exists() && dir.isDirectory() && dir.list().length == 0)
            dir.delete();

        user.setCvFileName(null);
        userRepository.save(user);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    private void validatePdf(MultipartFile file) throws IOException {
        validateNotEmpty(file);
        validateMimeType(file);
        validateFileName(file);
        validateFileSize(file);
        validatePdfHeader(file);
    }

    private void validateNotEmpty(MultipartFile file) {
        if (file.isEmpty())
            throw new IllegalArgumentException("The file is empty");
    }

    private void validateMimeType(MultipartFile file) {
        if (!"application/pdf".equals(file.getContentType()))
            throw new IllegalArgumentException("Only PDF files are allowed");
    }

    private void validateFileName(MultipartFile file) {
        String name = Paths.get(file.getOriginalFilename()).getFileName().toString();
        if (file.getOriginalFilename() == null)
            throw new IllegalArgumentException("Invalid file name");
        if (!name.matches("^[a-zA-Z0-9._-]+$"))
            throw new IllegalArgumentException("The file name contains invalid characters");
        if (!name.toLowerCase().endsWith(".pdf"))
            throw new IllegalArgumentException("Invalid file name");
        if (name.equalsIgnoreCase(".pdf"))
            throw new IllegalArgumentException("The file name cannot be just the extension");
        if (name.toLowerCase().matches(".*\\.pdf\\..+"))
            throw new IllegalArgumentException("The file contains multiple suspicious extensions");
        if (name.length() > 50)
            throw new IllegalArgumentException("Name too long");
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > 10 * 1024 * 1024)
            throw new IllegalArgumentException("File is too large");
    }

    private void validatePdfHeader(MultipartFile file) throws IOException {
        try (InputStream in = file.getInputStream()) {
            byte[] header = new byte[4];
            if (in.read(header) != 4 || header[0] != '%' || header[1] != 'P' || header[2] != 'D' || header[3] != 'F')
                throw new IllegalArgumentException("The file does not appear to be a valid PDF");
        }
    }

    private void deleteExistingCv(User user, Path userDir) throws IOException {
        if (user.getCvFileName() != null) {
            Path path = userDir.resolve(Paths.get(user.getCvFileName()).getFileName().toString());
            Files.deleteIfExists(path);
        }
    }

    public void deleteCurrentUserAndLogout(HttpServletRequest request, HttpServletResponse response) {
        UserDTO user = getLoggedUser();
        userRepository.deleteById(user.id());

        clearAuthCookies(response);

        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public void removeImage(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        clearImage(user);
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

    public boolean hasRoleById(Long userId, String role) {
        return userRepository.findById(userId)
                .map(user -> user.getRoles().contains(role))
                .orElse(false);
    }

    public boolean isOwner(Long id) {
        UserDTO current = getLoggedUser();
        return current.id().equals(id);
    }

    public boolean currentUserIsAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null &&
                auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Path getUserCvDir(User user) {
        String input = "cv-salt-" + user.getId();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            String hex = bytesToHex(hash).substring(0, 16);
            return CV_STORAGE_PATH.resolve("u" + hex);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
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
