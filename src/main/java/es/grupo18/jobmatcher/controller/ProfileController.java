package es.grupo18.jobmatcher.controller;

import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String showProfile(Model model) {
        model.addAttribute("user", userService.getLoggedUser());
        return "profile";
    }

    @GetMapping("/profile/image")
    public ResponseEntity<Object> getProfileImage() throws SQLException {
        User user = userService.getLoggedUser();
        if (user.getImageFile() != null) {
            Resource file = new InputStreamResource(user.getImageFile().getBinaryStream());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .contentLength(user.getImageFile().length())
                    .body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/profile/upload_image")
    public String uploadProfileImage(@RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        userService.save(userService.getLoggedUser(), imageFile);
        return "redirect:/profile";
    }

    @PostMapping("/profile/reset_image")
    public String resetProfileImage() {
        User user = userService.getLoggedUser();
        user.setImageFile(null);
        userService.save(user);
        return "redirect:/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Model model) {
        model.addAttribute("user", userService.getLoggedUser());
        return "profile_form";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String location,
            @RequestParam String bio,
            @RequestParam int experience) {

        User user = userService.getLoggedUser();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setLocation(location);
        user.setBio(bio);
        user.setExperience(experience);
        userService.save(user);
        return "redirect:/profile";
    }

}
