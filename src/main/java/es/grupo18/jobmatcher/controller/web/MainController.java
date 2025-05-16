package es.grupo18.jobmatcher.controller.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/main")
    public String showMainPage(Model model, Authentication authentication) {
        boolean isAdmin = false;

        if (authentication != null) {
            for (GrantedAuthority auth : authentication.getAuthorities()) {
                if ("ROLE_ADMIN".equals(auth.getAuthority())) {
                    isAdmin = true;
                    break;
                }
            }
        }

        model.addAttribute("isAdmin", isAdmin);
        return "main";
    }
    
}
