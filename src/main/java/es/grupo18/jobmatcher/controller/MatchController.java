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

    /*
    @GetMapping("/match") // Show the match page
    public String showMatchPage(Model model) {
        List<Company> companies = companyService.findAll();
        User currentUser = userService.getLoggedUser();

        // Creates two separated lists of companies: favourite and non-favourite
        List<Company> favouriteCompanies = currentUser.getFavouriteCompaniesList();
        List<Company> nonFavouriteCompanies = new ArrayList<>();

        // Evaluates which companies are not in the favourite list
        for (Company company : companies) {
            if (!favouriteCompanies.contains(company)) {
                nonFavouriteCompanies.add(company);
            }
        }

        model.addAttribute("favouriteCompanies", favouriteCompanies);
        model.addAttribute("nonFavouriteCompanies", nonFavouriteCompanies);

        return "match";
    }
    */

    @PostMapping("/addFavourite") // Add a company to the user's favourite list
    public String addFavourite(@RequestParam long companyId) {
        User currentUser = userService.getLoggedUser();
        Company company = companyService.findById(companyId);

        if (company != null) {
            userService.addOrRemoveFavouriteCompany(currentUser.getId(), company);
        }

        return "redirect:/match"; // Recharges the match page to show updates
    }

    @PostMapping("/removeFavourite") // Remove a company from the user's favourite list
    public String removeFavourite(@RequestParam long companyId, @RequestParam String origin) {
        User currentUser = userService.getLoggedUser();
        Company company = companyService.findById(companyId);

        if (company != null) {
            userService.addOrRemoveFavouriteCompany(currentUser.getId(), company);
        }

        return "redirect:" + origin;
    }

    @GetMapping("/consultant") // Show the consultant match page
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

        return "matchConsultant";
    }

    @GetMapping("/company/{companyId}") 
    public String getCompanyPage(@PathVariable Long id, Model model) {
        try {
            Company company = companyService.findById(id);
            model.addAttribute("company", company);
            return "company"; 
        } catch (EntityNotFoundException e) {
            return "error"; 
        }
    }

}
