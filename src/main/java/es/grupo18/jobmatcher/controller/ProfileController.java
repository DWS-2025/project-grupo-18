package es.grupo18.jobmatcher.controller;

import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ProfileController {

    private static final Path IMAGES_FOLDER = Paths.get("src/main/resources/static/img/");
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        User user = userService.getUser();

        // Si no tiene imagen, asigna la imagen por defecto
        if (user.getImagePath() == null || user.getImagePath().isEmpty()) {
            user.setImagePath("/img/profile.jpg");
        }

        model.addAttribute("user", user);
        model.addAttribute("studies", user.getDegrees());
        model.addAttribute("skills", user.getSkills());
        model.addAttribute("experience", user.getExperience());

        return "profile";
    }

    @PostMapping("/profile/upload_image")
public String uploadImage(@RequestParam("image") MultipartFile image) throws IOException {
    User user = userService.getUser();
    
    if (!image.isEmpty()) {
        // Asegurar que la carpeta existe
        Files.createDirectories(IMAGES_FOLDER);

        // Nombre único basado en el ID del usuario
        String fileName = "profile_" + user.getAccountId() + ".jpg";
        Path imagePath = IMAGES_FOLDER.resolve(fileName);
        
        // Guardar la imagen en el servidor
        image.transferTo(imagePath);

        // Actualizar la ruta en el usuario
        String relativePath = "/img/" + fileName;
        userService.updateUserImage(relativePath);
    }

    return "redirect:/profile";
}


    @GetMapping("/profile/edit") // Shows the profile editor
    public String editProfile(Model model) {
        User user = userService.getUser();
        model.addAttribute("name", user.getName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("phone", user.getPhone());
        model.addAttribute("location", user.getLocation());
        model.addAttribute("bio", user.getBio());
        return "profileEditor";
    }

    @PostMapping("/profile/edit") // Saves the profile changes
    public String saveProfile(@RequestParam String name, @RequestParam String email, @RequestParam String phone,
            @RequestParam String location, @RequestParam String bio) {
        userService.updateUserProfile(name, email, phone, location, bio);
        return "redirect:/profile";
    }

    @GetMapping("/profile/form") // Shows the form to edit the user's profile (CV, skills and experiences)
    public String editProfileInfo(Model model) {
        User user = userService.getUser();
        model.addAttribute("user", user);
        model.addAttribute("studies", String.join(", ", user.getDegrees()));
        model.addAttribute("skills", String.join(", ", user.getSkills()));
        model.addAttribute("experience", user.getExperience());
        return "form";
    }

    @PostMapping("/profile/form") // Saves the profile info changes
    public String saveProfileInfo(@RequestParam String studies, @RequestParam String skills,
            @RequestParam Integer experience) {
        userService.updateUserDetails(studies, skills, experience);
        return "redirect:/profile";
    }

    @PostMapping("/profile/reset_image")
    public ResponseEntity<Void> resetProfileImage() throws IOException {
        User user = userService.getUser();

        Path userImagePath = Paths.get("src/main/resources/static" + user.getImagePath());

        // Verifies if the image exists and if is not the default one, deletes it
        if (Files.exists(userImagePath) && !user.getImagePath().equals("/img/profile.jpg")) {
            Files.delete(userImagePath);
        }

        // Resets the image to the default one
        userService.updateUserImage("/img/profile.jpg");

        return ResponseEntity.ok().build();
    }

}
