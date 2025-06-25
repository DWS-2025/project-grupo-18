package es.grupo18.jobmatcher.controller.rest;

import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.security.jwt.AuthResponse;
import es.grupo18.jobmatcher.security.jwt.RegisterRequest;
import es.grupo18.jobmatcher.security.jwt.UserLoginService;
import es.grupo18.jobmatcher.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private void validateFileName(String fileName) {
        if (!fileName.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException("Invalid file name");
        }
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserLoginService userLoginService;

    @GetMapping
    public Page<UserDTO> getAll(Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isOwner(#id)")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<AuthResponse> createUser(@Valid @RequestBody RegisterRequest request) {
        request.setName(request.getName());
        AuthResponse response = userLoginService.register(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            UserDTO current = userService.getLoggedUser();
            String oldEmail = current.email();
            UserDTO sanitizedDto = new UserDTO(
                    dto.id(),
                    dto.name(),
                    dto.email(),
                    dto.phone(),
                    dto.location(),
                    dto.bio(),
                    dto.experience(),
                    dto.image(),
                    dto.imageContentType(),
                    dto.roles(),
                    dto.cvFileName());
            userService.update(id, sanitizedDto);
            if (!oldEmail.equals(sanitizedDto.email()) && id.equals(1)) {
                userLoginService.logout(request, response);
            }
            return ResponseEntity.ok(userService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getSelf() {
        return ResponseEntity.ok(userService.getLoggedUser());
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> updateSelf(@RequestBody UserDTO dto, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            UserDTO current = userService.getLoggedUser();
            String oldEmail = current.email();
            Long myId = current.id();
            UserDTO sanitizedDto = new UserDTO(
                    myId,
                    dto.name(),
                    dto.email(),
                    dto.phone(),
                    dto.location(),
                    dto.bio(),
                    dto.experience(),
                    dto.image(),
                    dto.imageContentType(),
                    current.roles(),
                    current.cvFileName());

            userService.update(myId, sanitizedDto);
            if (!oldEmail.equals(sanitizedDto.email())) {
                userLoginService.logout(request, response);
            }
            return ResponseEntity.ok(userService.findById(myId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> delete(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteOwnAccount(HttpServletRequest req,
            HttpServletResponse res) {
        try {
            userService.deleteCurrentUserAndLogout(req, res);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting account");
        }
    }

    @PostMapping("/me/cv")
    public ResponseEntity<?> uploadCv(@RequestParam("cv") MultipartFile cv) {
        try {
            String safeFileName = Paths.get(cv.getOriginalFilename()).getFileName().toString();
            validateFileName(safeFileName);
            userService.uploadCv(cv);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading CV: " + e.getMessage());
        }
    }

    @GetMapping("/me/cv")
    public ResponseEntity<Resource> downloadCv() {
        try {
            File file = userService.getCvFile();
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(file.length())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/me/cv")
    public ResponseEntity<?> deleteCv() {
        try {
            userService.deleteCv();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting CV");
        }
    }

    @PostMapping("/{id}/cv")
    public ResponseEntity<?> uploadUserCv(
            @PathVariable("id") Long userId,
            @RequestParam("cv") MultipartFile file) throws IOException {
        try {
            userService.uploadCv(file, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading CV: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/cv")
    public ResponseEntity<Resource> downloadUserCv(@PathVariable("id") Long userId) throws IOException {
        try {
            File cvFile = userService.getCvFile(userId);
            InputStreamResource body = new InputStreamResource(new FileInputStream(cvFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + cvFile.getName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(body);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}/cv")
    public ResponseEntity<?> deleteUserCv(@PathVariable("id") Long userId) throws IOException {
        try {
            userService.deleteCv(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting CV");
        }
    }

}
