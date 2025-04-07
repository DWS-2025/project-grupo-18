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

    @GetMapping("/error") // Error page
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        int statusCode = 500; // Default error message
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }

        model.addAttribute("status", statusCode);

        if (statusCode == HttpStatus.NOT_FOUND.value()) { // 404
            model.addAttribute("error", "Página no encontrada");
        } else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) { // 405
            model.addAttribute("error", "Método HTTP no permitido");
        }else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) { // 500
            model.addAttribute("error", "Error interno del servidor");
        } else { // Other errors
            model.addAttribute("error", "Error inesperado");
        } 

        return "error";
    }

}
