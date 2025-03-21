package es.grupo18.jobmatcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import es.grupo18.jobmatcher.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsersController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }
    
}
