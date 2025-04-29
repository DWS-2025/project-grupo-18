package es.grupo18.jobmatcher.controller.web;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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

        // Añadimos info de rol
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
    public String newUser(@ModelAttribute UserDTO userDTO) {
        userService.save(userDTO);
        return "redirect:/users";
    }

    @GetMapping("/{userId}")
    public String getUser(Model model, @PathVariable Long userId) {
        try {
            model.addAttribute("user", userService.findById(userId));
            model.addAttribute("currentTimeMillis", System.currentTimeMillis());

            UserDTO currentUser = userService.getLoggedUser();
            model.addAttribute("isAdmin", userService.isAdmin(currentUser));
            model.addAttribute("isUser", userService.isUser(currentUser));

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
    public String updateUser(@PathVariable Long userId, @ModelAttribute UserDTO updatedUser) {
        try {
            userService.update(userId, updatedUser);
            return "redirect:/users/" + userId;
        } catch (NoSuchElementException e) {
            return "user/user_not_found";
        }
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
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
    public String uploadUserImage(@PathVariable Long userId, @RequestParam("image") MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/jpg")
                    || contentType.equals("image/png") || contentType.equals("image/webp"))) {
                userService.updateUserImage(userId, image);
                return "redirect:/users/" + userId;
            } else {
                return "redirect:/users/" + userId + "?error=Formato de imagen no válido";
            }
        }
        return "redirect:/users/" + userId + "?error=No se ha seleccionado ninguna imagen";
    }

    @PostMapping("/{userId}/reset_image")
    public String resetUserImage(@PathVariable Long userId) {
        userService.removeImage(userId);
        return "redirect:/users/" + userId;
    }
}
