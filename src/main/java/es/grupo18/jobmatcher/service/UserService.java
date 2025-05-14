package es.grupo18.jobmatcher.service;

import es.grupo18.jobmatcher.dto.CompanyDTO;
import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.mapper.CompanyMapper;
import es.grupo18.jobmatcher.mapper.UserMapper;
import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.repository.UserRepository;
import es.grupo18.jobmatcher.security.RepositoryUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public void uploadCv(MultipartFile cv) throws IOException {
        logger.debug("Iniciando subida de CV para usuario autenticado");
        if (cv.isEmpty()) {
            logger.error("No se ha seleccionado ningún archivo");
            throw new IllegalArgumentException("No se ha seleccionado ningún archivo");
        }

        String contentType = cv.getContentType();
        if (!"application/pdf".equals(contentType)) {
            logger.error("Tipo de archivo no permitido: {}", contentType);
            throw new IllegalArgumentException("Solo se permiten archivos PDF");
        }

        String originalFilename = cv.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()
                || !originalFilename.toLowerCase().endsWith(".pdf")) {
            logger.error("Nombre de archivo inválido: {}", originalFilename);
            throw new IllegalArgumentException("Nombre de archivo inválido");
        }

        String sanitizedFilename = sanitizeFilename(originalFilename);
        UserDTO userDTO = getLoggedUser();
        User user = userRepository.findById(userDTO.id())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
        logger.debug("Usuario autenticado: ID {}", user.getId());

        // Verificar y crear directorio base
        File baseDir = CV_STORAGE_PATH.toFile();
        if (!baseDir.exists()) {
            logger.debug("Creando directorio base: {}", baseDir.getAbsolutePath());
            if (!baseDir.mkdirs()) {
                logger.error("No se pudo crear el directorio base: {}", baseDir.getAbsolutePath());
                throw new IOException("No se pudo crear el directorio base: " + baseDir.getAbsolutePath());
            }
        }

        // Verificar permisos de escritura
        if (!Files.isWritable(CV_STORAGE_PATH)) {
            logger.error("El directorio base no tiene permisos de escritura: {}", baseDir.getAbsolutePath());
            throw new IOException("El directorio base no tiene permisos de escritura: " + baseDir.getAbsolutePath());
        }

        // Eliminar CV anterior
        if (user.getCvFileName() != null) {
            Path oldCvPath = CV_STORAGE_PATH.resolve("user_" + user.getId())
                    .resolve(sanitizeFilename(user.getCvFileName()));
            File oldCv = oldCvPath.toFile();
            if (oldCv.exists()) {
                logger.debug("Eliminando CV anterior: {}", oldCv.getAbsolutePath());
                if (!oldCv.delete()) {
                    logger.warn("No se pudo eliminar el CV anterior: {}", oldCv.getAbsolutePath());
                }
            }
        }

        // Crear directorio del usuario
        Path userDirPath = CV_STORAGE_PATH.resolve("user_" + user.getId());
        File userDir = userDirPath.toFile();
        if (!userDir.exists()) {
            logger.debug("Creando directorio de usuario: {}", userDir.getAbsolutePath());
            if (!userDir.mkdirs()) {
                logger.error("No se pudo crear el directorio del usuario: {}", userDir.getAbsolutePath());
                throw new IOException("No se pudo crear el directorio del usuario: " + userDir.getAbsolutePath());
            }
        }

        // Verificar permisos de escritura en el directorio del usuario
        if (!Files.isWritable(userDirPath)) {
            logger.error("El directorio del usuario no tiene permisos de escritura: {}", userDir.getAbsolutePath());
            throw new IOException(
                    "El directorio del usuario no tiene permisos de escritura: " + userDir.getAbsolutePath());
        }

        // Guardar archivo
        Path destFilePath = userDirPath.resolve(sanitizedFilename);
        logger.debug("Guardando CV en: {}", destFilePath.toAbsolutePath());
        try {
            cv.transferTo(destFilePath);
        } catch (IOException e) {
            logger.error("Error al guardar el CV en disco: {}", e.getMessage());
            throw new IOException("Error al guardar el CV en disco: " + e.getMessage(), e);
        }

        user.setCvFileName(originalFilename);
        userRepository.save(user);
        logger.info("Usuario {} subió CV: {}", user.getId(), sanitizedFilename);
    }

    public File getCvFile() {
        UserDTO userDTO = getLoggedUser();
        User user = userRepository.findById(userDTO.id())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        if (user.getCvFileName() == null) {
            logger.error("No hay CV disponible para el usuario {}", user.getId());
            throw new IllegalStateException("No hay CV disponible");
        }

        Path userDirPath = CV_STORAGE_PATH.resolve("user_" + user.getId());
        Path cvFilePath = userDirPath.resolve(sanitizeFilename(user.getCvFileName()));
        File cvFile = cvFilePath.toFile();
        if (!cvFile.exists()) {
            logger.error("Archivo CV no encontrado en disco: {}", cvFile.getAbsolutePath());
            throw new IllegalStateException("Archivo CV no encontrado en disco");
        }

        return cvFile;
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
}