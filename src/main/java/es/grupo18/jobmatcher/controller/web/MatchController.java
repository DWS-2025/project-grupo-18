package es.grupo18.jobmatcher.controller.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import es.grupo18.jobmatcher.dto.CompanyDTO;
import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.service.CompanyService;
import es.grupo18.jobmatcher.service.UserService;

@Controller
public class MatchController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @GetMapping("/matches")
    public String showCompanies(Model model) {
        Collection<CompanyDTO> allCompanies = companyService.findAll();
        Collection<CompanyDTO> favouriteCompanies = userService.getFavouriteCompanies();

        List<CompanyDTO> nonFavouriteCompanies = new ArrayList<>();
        for (CompanyDTO company : allCompanies) {
            if (!favouriteCompanies.contains(company)) {
                nonFavouriteCompanies.add(company);
            }
        }

        model.addAttribute("favouriteCompanies", favouriteCompanies);
        model.addAttribute("nonFavouriteCompanies", nonFavouriteCompanies);
        return "match/matches";
    }

    @PostMapping("/matches/{companyId}/addFavourite")
    public String addFavourite(@PathVariable long companyId) {

        UserDTO currentUser = userService.getLoggedUser();
        CompanyDTO company = companyService.findById(companyId);

        if (company != null) {
            userService.addOrRemoveFavouriteCompany(currentUser.id(), company);
            companyService.addOrRemoveFavouriteUser(companyId, currentUser);
        }

        return "redirect:/matches";
    }

    @PostMapping("/matches/{companyId}/removeFavourite")
    public String removeFavourite(@PathVariable long companyId, @RequestParam(required = false) String origin) {

        UserDTO currentUser = userService.getLoggedUser();
        CompanyDTO company = companyService.findById(companyId);

        if (company != null) {
            userService.addOrRemoveFavouriteCompany(currentUser.id(), company);
            companyService.addOrRemoveFavouriteUser(companyId, currentUser);
        }

        return "redirect:" + (origin != null && !origin.isBlank() ? origin : "/matches");
    }

    @GetMapping("/matches/likes")
    public String showConsultantMatchPage(Model model) {
  
        UserDTO currentUser = userService.getLoggedUser();
        Collection<CompanyDTO> favouriteCompanies = userService.getFavouriteCompanies();

        List<CompanyDTO> mutualMatchCompanies = new ArrayList<>();
        for (CompanyDTO company : favouriteCompanies) {
            if (companyService.isUserFavourite(company.id(), currentUser)) {
                mutualMatchCompanies.add(company);
            }
        }

        model.addAttribute("mutualMatchCompanies", mutualMatchCompanies);
        return "match/likes";
    }

    @GetMapping("/company/{companyId}")
    public String getCompanyPage(@PathVariable("companyId") Long companyId, Model model) {

        CompanyDTO company = companyService.findById(companyId);
        if (company != null) {
            model.addAttribute("company", company);
            return "company/show_company";
        } else {
            return "error";
        }
    }

}
