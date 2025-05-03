package es.grupo18.jobmatcher.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;

@Controller
public class LoginWebController {

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", token.getToken());
        return "login/login"; 
    }

    @GetMapping("/loginerror")
    public String loginError(Model model) {
        model.addAttribute("loginError");
        return "login/loginerror"; 
    }
}
