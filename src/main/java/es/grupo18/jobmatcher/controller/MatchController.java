package es.grupo18.jobmatcher.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.service.CompanyService;
import es.grupo18.jobmatcher.service.UserService;
import jakarta.persistence.EntityNotFoundException;

@Controller
public class MatchController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @GetMapping("/matches")
    public String showCompanies(Model model) {
        List<Company> allCompanies = companyService.findAll();
        User currentUser = userService.getLoggedUser();
        List<Company> favouriteCompanies = currentUser.getFavouriteCompaniesList();

        List<Company> nonFavouriteCompanies = new ArrayList<>();
        for (Company company : allCompanies) {
            if (!favouriteCompanies.contains(company)) {
                nonFavouriteCompanies.add(company);
            }
        }

        model.addAttribute("favouriteCompanies", favouriteCompanies);
        model.addAttribute("nonFavouriteCompanies", nonFavouriteCompanies);
        return "match/matches";
    }

    @PostMapping("/matches/{companyId}/addFavourite") // Add a company to the user's favourite list
    public String addFavourite(@PathVariable long companyId) {
        User currentUser = userService.getLoggedUser();
        Company company = companyService.findById(companyId);

        if (company != null) {
            userService.addOrRemoveFavouriteCompany(currentUser.getId(), company);
        }

        return "redirect:/matches"; // Recharges the match page to show updates
    }

    @PostMapping("/matches/{companyId}/removeFavourite") // Remove a company from the user's favourite list
    public String removeFavourite(@PathVariable long companyId, @RequestParam String origin) {
        User currentUser = userService.getLoggedUser();
        Company company = companyService.findById(companyId);

        if (company != null) {
            userService.addOrRemoveFavouriteCompany(currentUser.getId(), company);
        }
        return "redirect:" + (origin != null && !origin.isBlank() ? origin : "/matches");
    }

    @GetMapping("/matches/likes") // Show the consultant match page
    public String showConsultantMatchPage(Model model) {
        User currentUser = userService.getLoggedUser();
        List<Company> favouriteCompanies = currentUser.getFavouriteCompaniesList();
        List<Company> mutualMatchCompanies = new ArrayList<>();

        // Filters companies that have the user in their favourite list
        for (Company company : favouriteCompanies) {
            if (companyService.isUserFavourite(company.getId(), currentUser)) {
                mutualMatchCompanies.add(company);
            }
        }

        model.addAttribute("mutualMatchCompanies", mutualMatchCompanies);

        return "match/likes";
    }

    @GetMapping("/company/{companyId}")
    public String getCompanyPage(@PathVariable("companyId") Long companyId, Model model) {
        try {
            Company company = companyService.findById(companyId);
            model.addAttribute("company", company);
            return "company/show_company";
        } catch (EntityNotFoundException e) {
            return "error";
        }
    }

}
