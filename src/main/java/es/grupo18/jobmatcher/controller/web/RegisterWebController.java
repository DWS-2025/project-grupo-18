package es.grupo18.jobmatcher.controller.web;

import es.grupo18.jobmatcher.security.jwt.RegisterRequest;
import es.grupo18.jobmatcher.security.jwt.UserLoginService;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RegisterWebController {

    @Autowired
    private UserLoginService userLoginService;

    private static final PolicyFactory TEXT_SANITIZER = Sanitizers.FORMATTING;

    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpServletRequest request,
            Authentication auth) {

        if (isLoggedIn(auth)) {
            return "redirect:/main";
        }
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
            HttpServletRequest request,
            Authentication auth) {

        if (isLoggedIn(auth)) {
            return "redirect:/main";
        }
        if (result.hasErrors()) {
            model.addAttribute("error", "Invalid data");
            addAttributesToModel(model, req, request);
            return "register/register";
        }

        try {           
            req.setName(TEXT_SANITIZER.sanitize(req.getName()));
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

    private boolean isLoggedIn(Authentication auth) {
        return auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
    }

}
