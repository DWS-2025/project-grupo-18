package es.grupo18.jobmatcher.controller.rest;

import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.service.UserService;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Page<UserDTO> getAll(Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO dto) {
        UserDTO created = userService.save(dto);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        try {
            userService.update(id, dto);
            return ResponseEntity.ok(userService.update(id, dto));
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la cuenta");
        }
    }

    @PostMapping("/me/cv")
    public ResponseEntity<?> uploadCv(@RequestParam("cv") MultipartFile cv) {
        try {
            userService.uploadCv(cv);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al subir CV: " + e.getMessage());
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al borrar el CV");
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
            return ResponseEntity.badRequest().body("Error al subir CV: " + e.getMessage());
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al borrar el CV");
        }
    }

}
