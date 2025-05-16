package es.grupo18.jobmatcher.controller.rest;

import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.service.UserService;
import jakarta.servlet.http.HttpServletResponse;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
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
import java.net.URI;
import java.nio.file.Paths;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private static final PolicyFactory TEXT_SANITIZER = Sanitizers.FORMATTING;

    private String sanitizeText(String text) {
        return text != null ? TEXT_SANITIZER.sanitize(text) : null;
    }

    private void validateFileName(String fileName) {
    if (!fileName.matches("^[a-zA-Z0-9._-]+$")) {
        throw new IllegalArgumentException("Invalid file name");
    }
}

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO dto) {
        UserDTO sanitizedDto = new UserDTO(
            dto.id(),
            sanitizeText(dto.name()),
            sanitizeText(dto.email()),
            sanitizeText(dto.phone()),
            sanitizeText(dto.location()),
            sanitizeText(dto.bio()),
            dto.experience(),
            dto.image(),
            dto.imageContentType(),
            dto.roles(),
            dto.cvFileName()
        );
        UserDTO created = userService.save(sanitizedDto);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isOwner(#id)")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        try {
            // Sanitizamos los campos de texto
            UserDTO sanitizedDto = new UserDTO(
                dto.id(),
                sanitizeText(dto.name()),
                sanitizeText(dto.email()),
                sanitizeText(dto.phone()),
                sanitizeText(dto.location()),
                sanitizeText(dto.bio()),
                dto.experience(),
                dto.image(),
                dto.imageContentType(),
                dto.roles(),
                dto.cvFileName()
            );
            userService.update(id, sanitizedDto);
            return ResponseEntity.ok(userService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
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
    public ResponseEntity<?> deleteOwnAccount(HttpServletResponse response) {
        try {
            userService.deleteCurrentUserAndLogout(response);
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
            userService.uploadCv(cv); // Asegurarse que el servicio use safeFileName
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading CV: " + e.getMessage());
        }
    }

    @GetMapping("/me/cv")
    public ResponseEntity<Resource> downloadCv() {
        try {
            File file = userService.getCvFile(); // Verificar que getCvFile no permita path traversal
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
