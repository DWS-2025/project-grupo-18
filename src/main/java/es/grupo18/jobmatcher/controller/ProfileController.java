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

import java.sql.Blob;
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

    @PostMapping("/profile/upload_image")
    public ResponseEntity<Object> uploadImage() throws SQLException {
        User user = userService.getLoggedUser();
        if (user.getImageFile() != null) {
            Blob image = user.getImageFile();
            Resource file = new InputStreamResource(image.getBinaryStream());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .contentLength(image.length())
                    .body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/profile/reset_image")
    public ResponseEntity<Void> resetProfileImage() {
        User user = userService.getLoggedUser();
        user.setImageFile(null); 
        userService.save(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam String name, @RequestParam String email, @RequestParam String password,
                                @RequestParam String phone, @RequestParam String location, @RequestParam String bio,
                                @RequestParam int experience) {
        User user = userService.getLoggedUser();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setLocation(location);
        user.setBio(bio);
        user.setExperience(experience);
        userService.save(user);
        return "redirect:/profile";
    }

}
