package es.grupo18.jobmatcher.controller.web;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import java.io.File;

import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model, Pageable page) {
        Pageable pageable = PageRequest.of(page.getPageNumber(), 10);
        Page<UserDTO> usersPage = userService.findAll(pageable);

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("hasPrev", usersPage.hasPrevious());
        model.addAttribute("prev", pageable.getPageNumber() - 1);
        model.addAttribute("hasNext", usersPage.hasNext());
        model.addAttribute("next", pageable.getPageNumber() + 1);

        UserDTO currentUser = userService.getLoggedUser();
        model.addAttribute("isAdmin", userService.isAdmin(currentUser));
        model.addAttribute("isUser", userService.isUser(currentUser));

        return "user/users";
    }

    @GetMapping("/new")
    public String showUserForm(Model model) {
        model.addAttribute("user", userService.createEmpty());

        UserDTO currentUser = userService.getLoggedUser();
        model.addAttribute("isAdmin", userService.isAdmin(currentUser));
        model.addAttribute("isUser", userService.isUser(currentUser));

        return "user/user_form";
    }

    @PostMapping("/new")
    public String newUser(@ModelAttribute UserDTO userDTO, Model model) {
        try {
            userService.save(userDTO);
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", userDTO);
            return "user/user_form";
        }
    }

    @GetMapping("/{userId}")
    public String getUser(Model model, @PathVariable Long userId) {
        UserDTO currentUser = userService.getLoggedUser();

        if (currentUser != null && currentUser.id().equals(userId)) {
            return "redirect:/profile";
        }

        try {
            UserDTO targetUser = userService.findById(userId);
            model.addAttribute("user", targetUser);
            model.addAttribute("currentTimeMillis", System.currentTimeMillis());

            model.addAttribute("isAdmin", userService.isAdmin(currentUser));
            model.addAttribute("isUser", userService.isUser(currentUser));
            model.addAttribute("isAdminOrUser", userService.isAdmin(currentUser) || userService.isUser(currentUser));
            model.addAttribute("isSelf", false);

            return "user/user_detail";
        } catch (NoSuchElementException e) {
            return "user/user_not_found";
        }
    }

    @GetMapping("/{userId}/edit")
    public String editUser(Model model, @PathVariable Long userId) {
        try {
            model.addAttribute("user", userService.findById(userId));

            UserDTO currentUser = userService.getLoggedUser();
            model.addAttribute("isAdmin", userService.isAdmin(currentUser));
            model.addAttribute("isUser", userService.isUser(currentUser));

            return "user/user_form";
        } catch (NoSuchElementException e) {
            return "user/user_not_found";
        }
    }

    @PostMapping("/{userId}/edit")
    public String updateUser(
            @PathVariable Long userId,
            @ModelAttribute UserDTO updatedUser,
            Model model) {

        try {
            userService.update(userId, updatedUser);
            return "redirect:/users/" + userId;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", updatedUser);
            model.addAttribute("user", userService.findById(userId));

            UserDTO currentUser = userService.getLoggedUser();
            model.addAttribute("isAdmin", userService.isAdmin(currentUser));
            model.addAttribute("isUser", userService.isUser(currentUser));
            return "user/user_form";
        } catch (NoSuchElementException e) {
            return "user/user_not_found";
        }
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        UserDTO currentUser = userService.getLoggedUser();
        if (currentUser.id().equals(userId)) {
            return "redirect:/profile";
        }

        try {
            userService.deleteById(userId);
            return "redirect:/users";
        } catch (NoSuchElementException e) {
            return "user/user_not_found";
        }
    }

    @GetMapping("/{userId}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long userId) {
        try {
            UserDTO user = userService.findById(userId);
            String contentType = user.imageContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "image/jpeg";
            }
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(user.image());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/upload_image")
    @ResponseBody
    public ResponseEntity<?> uploadUserImage(@PathVariable Long userId, @RequestParam("image") MultipartFile image)
            throws IOException {
        if (!image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/jpg")
                    || contentType.equals("image/png") || contentType.equals("image/webp"))) {
                userService.updateUserImage(userId, image);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body("Invalid image format");
            }
        }
        return ResponseEntity.badRequest().body("No image selected");
    }

    @PostMapping("/{userId}/reset_image")
    @ResponseBody
    public ResponseEntity<?> resetUserImage(@PathVariable Long userId) {
        userService.removeImage(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/download_cv")
    public ResponseEntity<Resource> downloadUserCv(@PathVariable Long userId) {
        try {
            File cvFile = userService.getCvFile(userId);

            if (!cvFile.exists() || !cvFile.isFile()) {
                return ResponseEntity.notFound().build();
            }

            UserDTO user = userService.findById(userId);
            String safeFilename = user.cvFileName().replaceAll("[\\r\\n\"]", "_");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeFilename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(cvFile.length())
                    .body(new FileSystemResource(cvFile));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/upload_cv")
    @ResponseBody
    public ResponseEntity<?> uploadUserCv(@RequestParam("cv") MultipartFile cv, @PathVariable Long userId) {
        try {
            userService.uploadCv(cv, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading CV");
        }
    }

    @PostMapping("/{userId}/delete_cv")
    @ResponseBody
    public ResponseEntity<?> deleteUserCv(@PathVariable Long userId) {
        try {
            userService.deleteCv(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting CV");
        }
    }

}
