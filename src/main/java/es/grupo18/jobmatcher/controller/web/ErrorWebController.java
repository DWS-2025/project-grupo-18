package es.grupo18.jobmatcher.controller.web;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ErrorWebController {

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(HttpServletRequest request, Model model) {
        String referrer = request.getHeader("Referer");
        model.addAttribute("referer", referrer);
        return "403";
    }
}
