package es.grupo18.jobmatcher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import es.grupo18.jobmatcher.model.User;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Controller
public class ProfileController {

    private static final Path IMAGES_FOLDER = Paths.get("src/main/resources/static/img/");

    // Usuario en memoria
    private static final User user = new User(
        1L, 
        "Juan Pérez", 
        "juan.perez@email.com", 
        "password123",
        "123456789",
        "Madrid España",
        "Apasionado por la tecnología y la programación.",
        5,
        Arrays.asList("Grado en Ingeniería Informática", "Máster en Ciberseguridad"),
        Arrays.asList("Java", "Spring Boot", "SQL", "Pentesting"),
        "/img/profile.jpg"
    );

    @GetMapping("/profile")
    public String showProfile(Model model) {
        model.addAttribute("user", user);
        model.addAttribute("studies", user.getDegrees()); 
        model.addAttribute("skills", user.getSkills());
        model.addAttribute("experience", user.getExperience());
        return "profile";
    }


    @PostMapping("/profile/upload_image")
    public String uploadImage(@RequestParam("image") MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            // Asegurar que la carpeta de imágenes existe
            Files.createDirectories(IMAGES_FOLDER);

            // Guardar la imagen con el nombre del usuario
            Path imagePath = IMAGES_FOLDER.resolve("profile_" + user.getAccountId() + ".jpg");
            image.transferTo(imagePath);

            // Actualizar la ruta de la imagen en el usuario
            user.setImagePath("/img/profile_" + user.getAccountId() + ".jpg");
        }
        
        return "redirect:/profile"; // Redirige para recargar la página y ver la nueva imagen
    }



    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        model.addAttribute("user", user); // Usa el usuario en memoria
        return "profileEditor";
    }

    @PostMapping("/profile/edit")
    public String saveProfile(@RequestParam String name, @RequestParam String email, @RequestParam String phone,
                              @RequestParam String location, @RequestParam String about) {
        // Actualiza los valores en memoria
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setLocation(location);
        user.setBio(about);
        
        return "redirect:/profile";
    }

    @GetMapping("/profile/form")
    public String editProfileInfo(Model model) {
        model.addAttribute("user", user);
        model.addAttribute("studies", String.join(", ", user.getDegrees()));
        model.addAttribute("skills", String.join(", ", user.getSkills()));
        model.addAttribute("experience", user.getExperience());
        return "form"; 
    }

    @PostMapping("/profile/form")
    public String saveProfileInfo(@RequestParam String studies, @RequestParam String skills, @RequestParam Integer experience) {
        // Convertir los textos en listas separadas por comas
        List<String> updatedStudies = Arrays.asList(studies.split(",\\s*"));
        List<String> updatedSkills = Arrays.asList(skills.split(",\\s*"));

        // Actualizar en memoria
        user.setDegrees(updatedStudies);
        user.setSkills(updatedSkills);
        user.setExperience(experience);

        return "redirect:/profile";
    }



}
