package es.grupo18.jobmatcher.controller.web;

import es.grupo18.jobmatcher.security.jwt.RegisterRequest;
import es.grupo18.jobmatcher.security.jwt.UserLoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RegisterWebController {

    @Autowired
    private UserLoginService userLoginService;

    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpServletRequest request) {
        model.addAttribute("name", "");
        model.addAttribute("email", "");
        model.addAttribute("password", "");
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        if (token != null) {
            model.addAttribute("token", token.getToken());
        }
        return "register/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterRequest req,
            BindingResult result,
            Model model,
            HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Datos inválidos");
            addAttributesToModel(model, req, request);
            return "register/register";
        }

        try {
            userLoginService.registerAndAuthenticate(req);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            addAttributesToModel(model, req, request);
            return "register/register";
        }

        return "redirect:/";
    }

    private void addAttributesToModel(Model model, RegisterRequest req, HttpServletRequest request) {
        model.addAttribute("name", req.getName());
        model.addAttribute("email", req.getEmail());
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        if (token != null) {
            model.addAttribute("token", token.getToken());
        }
    }

}
