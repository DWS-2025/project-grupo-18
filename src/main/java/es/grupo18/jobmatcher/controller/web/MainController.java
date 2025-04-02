package es.grupo18.jobmatcher.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/main") // Shows the main page
    public String showMainPage() {
        return "main";
    }

}
