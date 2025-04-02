package es.grupo18.jobmatcher.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String showIndexPage() {
        return "index"; // Renders index.mustache
    }

}
