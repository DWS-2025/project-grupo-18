package es.grupo18.jobmatcher.controller.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LoginWebController {

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request, Authentication auth) {
        if (isLoggedIn(auth)) {
            return "redirect:/main";
        }

        Long blockedUntil = (Long) request.getSession().getAttribute("loginBlockedUntil");
        long now = System.currentTimeMillis();

        if (blockedUntil != null && now < blockedUntil) {
            long seg = (blockedUntil - now) / 1000;
            System.out.println("⏳ Usuario bloqueado. Tiempo restante: " + seg + "s");
            model.addAttribute("loginBlocked", true);
            model.addAttribute("secondsRemaining", seg);
            return "login/loginerror"; 
        }

        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", token.getToken());
        return "login/login";
    }

    @GetMapping("/loginerror")
    public String loginError(Model model, Authentication auth, HttpServletRequest request) {
        if (isLoggedIn(auth)) {
            return "redirect:/main";
        }

        long now = System.currentTimeMillis();
        request.getSession().setAttribute("loginBlockedUntil", now + 10_000);

        model.addAttribute("loginError", true);
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
