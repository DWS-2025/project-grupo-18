package es.grupo18.jobmatcher.controller.web;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String showProfile(Model model, HttpServletRequest request) {
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
    }

    @GetMapping("/profile/image")
    public ResponseEntity<byte[]> getProfileImage() {
        try {
            String contentType = userService.getLoggedUser().imageContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "image/jpeg";
            }
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(userService.getLoggedUser().image());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/profile/upload_image")
    @ResponseBody
    public ResponseEntity<?> uploadProfileImage(@RequestParam("image") MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/jpg")
                    || contentType.equals("image/png") || contentType.equals("image/webp"))) {
                userService.updateUserImage(image);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body("Formato de imagen no válido");
            }
        }
        return ResponseEntity.badRequest().body("No se ha seleccionado ninguna imagen");
    }

    @PostMapping("/profile/reset_image")
    @ResponseBody
    public ResponseEntity<?> resetProfileImage() {
        userService.removeImage();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Model model) {
        model.addAttribute("user", userService.getLoggedUser());
        return "profile/profile_form";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute UserDTO updatedUser) {
        try {
            userService.updateProfile(updatedUser);
            return "redirect:/profile";
        } catch (Exception e) {
            return "redirect:/profile/edit?error=" + e.getMessage();
        }
    }

}
