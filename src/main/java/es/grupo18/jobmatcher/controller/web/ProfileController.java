package es.grupo18.jobmatcher.controller.web;

import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String showProfile(Model model) {
        model.addAttribute("user", userService.getLoggedUser());
        model.addAttribute("currentTimeMillis", System.currentTimeMillis());
        return "profile/profile";
    }

    @GetMapping("/profile/image")
    public ResponseEntity<byte[]> getProfileImage() {
        UserDTO user = userService.getLoggedUser();
        if (user.image() != null) {
            String contentType = user.imageContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "image/jpeg";
            }
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(user.image());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/profile/upload_image")
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
    public String resetProfileImage() {
        userService.removeImage();
        return "redirect:/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Model model) {
        model.addAttribute("user", userService.getLoggedUser());
        return "profile/profile_form";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam String phone,
            @RequestParam String location,
            @RequestParam String bio,
            @RequestParam int experience) {

        UserDTO current = userService.getLoggedUser();

        UserDTO updated = new UserDTO(
                current.id(),
                name,
                email,
                phone,
                location,
                bio,
                experience,
                current.image(),
                current.imageContentType());

        userService.updateProfile(updated);
        return "redirect:/profile";
    }

}
