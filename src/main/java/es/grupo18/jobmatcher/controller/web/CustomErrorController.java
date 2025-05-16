package es.grupo18.jobmatcher.controller.web;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        int statusCode = 500;
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }

        model.addAttribute("status", statusCode);

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            model.addAttribute("error", "Page not found");
            return "error/404";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            model.addAttribute("error", "Access denied");
            return "error/403";
        } else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
            model.addAttribute("error", "HTTP method not allowed");
            return "error/405";
        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            model.addAttribute("error", "Internal server error");
            return "error/500";
        } else {
            model.addAttribute("error", "Unexpected error");
            return "error/error";
        }
    }

}
