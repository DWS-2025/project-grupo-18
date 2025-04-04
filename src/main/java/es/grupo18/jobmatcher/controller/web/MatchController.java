package es.grupo18.jobmatcher.controller.web;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.grupo18.jobmatcher.dto.CompanyDTO;
import es.grupo18.jobmatcher.service.CompanyService;

@Controller
public class MatchController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/matches")
    public String showCompanies(Model model) {
        model.addAttribute("favouriteCompanies", companyService.getFavouriteCompaniesForCurrentUser());
        model.addAttribute("nonFavouriteCompanies", companyService.getNonFavouriteCompaniesForCurrentUser());
        return "match/matches";
    }

    @PostMapping("/matches/{companyId}/addFavourite")
    public String addFavourite(@PathVariable long companyId) {
        companyService.toggleFavouriteCompanyForCurrentUser(companyId);
        return "redirect:/matches";
    }

    @PostMapping("/matches/{companyId}/removeFavourite")
    public String removeFavourite(@PathVariable long companyId, @RequestParam(required = false) String origin) {
        companyService.toggleFavouriteCompanyForCurrentUser(companyId);
        return "redirect:" + (origin != null && !origin.isBlank() ? origin : "/matches");
    }

    @GetMapping("/matches/likes")
    public String showConsultantMatchPage(Model model) {
        List<CompanyDTO> mutualMatchCompanies = companyService.getMutualMatchesForCurrentUser();
        model.addAttribute("mutualMatchCompanies", mutualMatchCompanies);
        return "match/likes";
    }

    @GetMapping("/company/{companyId}")
    public String getCompanyPage(@PathVariable("companyId") Long companyId, Model model) {
        try {
            model.addAttribute("company", companyService.findById(companyId));
            return "company/show_company";
        } catch (NoSuchElementException e) {
            return "error";
        }
    }
    
}
