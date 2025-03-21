package es.grupo18.jobmatcher.controller;

import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.service.CompanyService;
import es.grupo18.jobmatcher.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    private static final Path IMAGES_FOLDER = Paths.get("src/main/resources/static/img");

    @GetMapping("/profile")
    public String showProfile(Model model) {
        model.addAttribute("user", userService.getLoggedUser());
        return "profile";
    }

    @PostMapping("/profile/upload_image")
    public String uploadImage(@RequestParam("image") MultipartFile image) throws IOException {
        User user = userService.getLoggedUser();

        if (!image.isEmpty()) {
            // Ensure the images folder exists
            Files.createDirectories(IMAGES_FOLDER);

            // Unique file name for the image
            String fileName = "profile_" + user.getId() + ".jpg";
            Path imagePath = IMAGES_FOLDER.resolve(fileName);

            // Saves the image in the images folder
            image.transferTo(imagePath);

            // Updates the user's image path
            String relativePath = "/img/" + fileName;
            userService.updateUserImage(relativePath);
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/reset_image")
    public ResponseEntity<Void> resetProfileImage() throws IOException {
        User user = userService.getLoggedUser();

        Path userImagePath = Paths.get("src/main/resources/static" + user.getImagePath());

        // Verifies if the image exists and if is not the default one, deletes it
        if (Files.exists(userImagePath) && !user.getImagePath().equals("/img/profile.jpg")) {
            Files.delete(userImagePath);
        }

        // Resets the image to the default one
        userService.updateUserImage("/img/profile.jpg");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam String name, @RequestParam String email, @RequestParam String password,
            @RequestParam String phone,
            @RequestParam String location, @RequestParam String bio, @RequestParam int experience) {
        userService.update(name, email, password, phone, location, bio, experience);
        return "redirect:/profile";
    }

    @GetMapping("/companies")
    public String getCompanies(Model model) {
        model.addAttribute("companies", companyService.findAll());
        return "companies";
    }

    @PostMapping("/companies/new")
    public String newCompany(Model model, Company company) {
        companyService.save(company);
        return "redirect:/companies";
    }

    @GetMapping("/companies/{companyId}")
    public String getCompany(Model model, @PathVariable long id) {
        Optional<Company> company = Optional.ofNullable(companyService.findById(id));
        if (company.isPresent()) {
            model.addAttribute("company", company.get());
            return "show_company";
        } else {
            return "company_not_found";
        }
    }

    @GetMapping("/companies/{companyId}/edit")
    public String editCompany(Model model, @PathVariable long id) {
        Optional<Company> company = Optional.ofNullable(companyService.findById(id));
        if (company.isPresent()) {
            model.addAttribute("company", company.get());
            return "edit_company";
        } else {
            return "company_not_found";
        }
    }

    @PostMapping("/companies/{companyId}/edit")
    public String updateCompany(Model model, @PathVariable long id, Company updatedCompany) {
        Optional<Company> op = Optional.ofNullable(companyService.findById(id));
        if (op.isPresent()) {
            Company oldCompany = op.get();
            companyService.update(oldCompany, updatedCompany);
            return "redirect:/companies/" + id;
        } else {
            return "company_not_found";
        }
    }

    @PostMapping("/companies/{companyId}/delete")
    public String deleteCompany(@PathVariable long id) {
        Optional<Company> op = Optional.ofNullable(companyService.findById(id));
        if (op.isPresent()) {
            companyService.delete(op.get());
            return "redirect:/companies";
        } else {
            return "company_not_found";
        }
    }
    
}
