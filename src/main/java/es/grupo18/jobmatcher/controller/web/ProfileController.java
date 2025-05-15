package es.grupo18.jobmatcher.controller.web;

import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.web.csrf.CsrfToken;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    // OWASP HTML Sanitizer Policy
    private static final PolicyFactory HTML_SANITIZER = Sanitizers.FORMATTING
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.LINKS)
            .and(Sanitizers.STYLES);

    @GetMapping("/profile")
    public String showProfile(Model model, HttpServletRequest request) {
        try {
            UserDTO user = userService.getLoggedUser();
            boolean isBioVisible = user.bio() != null && !user.bio().trim().equals("<p><br></p>");

            model.addAttribute("user", user);
            model.addAttribute("currentTimeMillis", System.currentTimeMillis());
            model.addAttribute("isBioVisible", isBioVisible);

            CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
            if (csrfToken != null) {
                model.addAttribute("token", csrfToken.getToken());
            }

            return "profile/profile";
        } catch (IllegalStateException e) {
            return "redirect:/login?error=" + URLEncoder.encode("Por favor, inicia sesión", StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/profile/image")
    public ResponseEntity<byte[]> getProfileImage() {
        try {
            UserDTO user = userService.getLoggedUser();
            String contentType = user.imageContentType() != null && !user.imageContentType().isBlank()
                    ? user.imageContentType()
                    : "image/jpeg";
            byte[] image = user.image() != null ? user.image() : new byte[0];
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(image);
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/profile/upload_image")
    @ResponseBody
    public ResponseEntity<?> uploadProfileImage(@RequestParam("image") MultipartFile image) {
        try {
            if (image.isEmpty()) {
                return ResponseEntity.badRequest().body("No se ha seleccionado ninguna imagen");
            }
            userService.updateUserImage(image);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir la imagen");
        }
    }

    @PostMapping("/profile/reset_image")
    @ResponseBody
    public ResponseEntity<?> resetProfileImage() {
        try {
            userService.removeImage();
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al resetear la imagen");
        }
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Model model) {
        try {
            model.addAttribute("user", userService.getLoggedUser());
            return "profile/profile_form";
        } catch (IllegalStateException e) {
            return "redirect:/login?error=" + URLEncoder.encode("Por favor, inicia sesión", StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute UserDTO updatedUser) {
        try {
            // Sanitize the bio field to prevent XSS
            if (updatedUser.bio() != null) {
                if (updatedUser.bio().length() > 10000) {
                    return "redirect:/profile/edit?error=" + URLEncoder.encode("La biografía es demasiado larga", StandardCharsets.UTF_8);
                }
                String safeBio = HTML_SANITIZER.sanitize(updatedUser.bio());
                updatedUser = new UserDTO(
                        updatedUser.id(),
                        updatedUser.name(),
                        updatedUser.email(),
                        updatedUser.phone(),
                        updatedUser.location(),
                        safeBio,
                        updatedUser.experience(),
                        updatedUser.image(),
                        updatedUser.imageContentType(),
                        updatedUser.roles(),
                        updatedUser.cvFileName()
                );
            }
            userService.updateProfile(updatedUser);
            return "redirect:/profile";
        } catch (Exception e) {
            return "redirect:/profile/edit?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/profile/delete")
    public String deleteOwnProfile(HttpServletResponse response) {
        try {
            userService.deleteCurrentUserAndLogout(response);
            return "redirect:/login?message=Cuenta eliminada correctamente";
        } catch (Exception e) {
            return "redirect:/profile?error=" + URLEncoder.encode("Error al eliminar la cuenta", StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/profile/upload_cv")
    @ResponseBody
    public ResponseEntity<?> uploadCv(@RequestParam("cv") MultipartFile cv) {
        try {
            userService.uploadCv(cv);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir el CV");
        }
    }

    @GetMapping("/profile/download_cv")
    public ResponseEntity<Resource> downloadCv() {
        try {
            File cvFile = userService.getCvFile();

            if (!cvFile.exists() || !cvFile.isFile())
                return ResponseEntity.notFound().build();

            UserDTO user = userService.getLoggedUser();
            String safeFilename = user.cvFileName().replaceAll("[\\r\\n\"]", "_");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeFilename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(cvFile.length())
                    .body(new FileSystemResource(cvFile));

        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/profile/delete_cv")
    @ResponseBody
    public ResponseEntity<?> deleteCv() {
        try {
            userService.deleteCv();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al borrar el CV");
        }
    }
}