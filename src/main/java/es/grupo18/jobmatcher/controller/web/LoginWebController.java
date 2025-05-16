package es.grupo18.jobmatcher.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginWebController {

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request, Authentication auth) {
        if (isLoggedIn(auth)) {
            return "redirect:/main";
        }
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", token.getToken());
        return "login/login";
    }

    @GetMapping("/loginerror")
    public String loginError(Model model, Authentication auth) {
        if (isLoggedIn(auth)) {
            return "redirect:/main";
        }
        model.addAttribute("loginError");
        return "login/loginerror";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("AuthToken", null);
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        accessCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie("RefreshToken", null);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        SecurityContextHolder.clearContext();

        return "redirect:/";
    }

    private boolean isLoggedIn(Authentication auth) {
        return auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
    }

}
